package au.com.cding21.third_party.banks

import au.com.cding21.third_party.banks.allocators.SynchronousAllocator
import au.com.cding21.third_party.banks.types.Account
import au.com.cding21.third_party.banks.types.BankTransaction
import au.com.cding21.third_party.banks.util.*
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.TimeoutError
import io.ktor.server.plugins.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import java.rmi.ServerException
import java.util.regex.Pattern
import javax.security.auth.login.CredentialNotFoundException

/**
 * Stateless ANZ API Client to access ANZ account data.
 * Note: ANZ has rate limited their authentication API, leading to account suspension if accessed frequently. Access to the ANZ client
 * should be rate limited on a per-user basis, at the (HTTP) requests level.
 */
class ANZClientImpl(
    private val allocator: SynchronousAllocator,
    private val username: String,
    private val password: String,
) : BankClient {
    private val DEFAULT_SELECTOR_WAIT_OPTIONS = Page.WaitForSelectorOptions().setTimeout(5000.0)
    private val DEFAULT_WAIT_TIME = 5000L

    /**
     * Internal login function. Release instance upon task completion.
     */
    private suspend inline fun <T> withLoggedInSession(
        permanent: Boolean = false,
        task: (context: BrowserContext, page: Page) -> T,
    ): T {
        val context = allocator.acquire()
        val page = context.newPage()
        page.navigateAsync("https://login.anz.com/internetbanking")
        page.waitForSelectorAsync("#live-container > div:nth-child(1) > div > div > div > label", DEFAULT_SELECTOR_WAIT_OPTIONS)

        page.locator("#customerRegistrationNumber").fill(username)
        delay(50)
        page.locator("#password").fill(password)
        delay(50)
        page.locator("button[data-test-id=\"log-in-btn\"]").click()

        if (page.isVisible("#customerRegistrationNumber_error")) {
            throw CredentialNotFoundException(page.innerHTML("#customerRegistrationNumber_error"))
        } else if (page.isVisible("#password_error")) {
            throw CredentialNotFoundException(page.innerHTML("#password_error"))
        }

        page.waitForResponseAsync("https://unauthib.anz.com/ib/credential/verify") // ANZ makes 2 calls to this, only 2nd call is useful
        val response = page.waitForResponseAsync("https://unauthib.anz.com/ib/credential/verify")
        if (response.isClientError()) {
            throw CredentialNotFoundException("CRN or password was incorrect")
        } else if (response.status() != 200) {
            throw ServerException("Unknown ANZ Auth API error. Status: ${response.status()}, Data: ${response.body()}")
        }

//        try {
//            page.waitForUrlAsync(Pattern.compile("https://.+/broadcast-message"), Page.WaitForURLOptions().setTimeout(2000.0))
//            page.getByText("Continue to Internet Banking").click()
//        } catch (_: TimeoutError) {
//            // NO-OP
//            println()
//        }

        val result = task(context, page)
        if (!permanent) {
            allocator.release(context)
        }
        return result
    }

    private fun parseAccountsFromJsonString(jsonString: String): List<Account> {
        val rootObj = Json.decodeFromString<JsonObject>(jsonString)
        val dataArray = rootObj.throwIfNullKey("data").jsonArray
        return dataArray.map { Account.fromANZJson(it.jsonObject) }
    }

    private fun parseTransactionsFromJsonString(jsonString: String): List<BankTransaction> {
        val rootObj = Json.decodeFromString<JsonObject>(jsonString)
        val dataArray = rootObj.throwIfNullKey("data").jsonObject.throwIfNullKey("transactionList").jsonArray
        return dataArray.map { BankTransaction.fromANZJson(it.jsonObject) }
    }

    private suspend fun navigateToAccount(
        accountId: String,
        page: Page,
    ) {
        page.waitForSelectorAsync("#card-number", DEFAULT_SELECTOR_WAIT_OPTIONS)
        val accountTabs = page.querySelectorAll("#card-number")
        val filteredAccountTabs = accountTabs.filter { it.innerHTML() == accountId }
        val accountTab = if (filteredAccountTabs.isNotEmpty()) filteredAccountTabs[0] else throw NotFoundException("Account ID not found")
        accountTab.click()
    }

    override suspend fun getAccounts(): List<Account> {
        return withLoggedInSession { _, page ->
            val response = page.waitForResponseAsync("https://authib.anz.com/ib/bff/v2/accounts/summary")
            if (response.isSuccess()) {
                return@withLoggedInSession parseAccountsFromJsonString(response.text())
            }
            throw ServerException("Unknown ANZ Accounts (summary) API error. Status: ${response.status()}, Data: ${response.body()}")
        }
    }

    override suspend fun getTransactions(
        accountId: String,
        limit: Int,
    ): List<BankTransaction> {
        return withLoggedInSession { _, page ->
            navigateToAccount(accountId, page)
            val response = page.waitForResponseAsync("https://authib.anz.com/ib/bff/accounts/v1/transactions")
            if (response.isSuccess()) {
                val results = parseTransactionsFromJsonString(response.text()).toMutableList()
                while (limit > results.size) {
                    page.querySelector("#app-container > div > div > footer").scrollIntoViewIfNeeded()
                    if (page.getByText("Looking for an older transaction?").isVisible) {
                        break
                    }

                    try {
                        val response_ =
                            page.waitForResponseAsync(
                                "https://authib.anz.com/ib/bff/accounts/v1/transactions",
                                Page.WaitForResponseOptions().setTimeout(5000.0),
                            )
                        if (response_.isSuccess()) {
                            results.addAll(parseTransactionsFromJsonString(response_.text()))
                        } else {
                            throw ServerException(
                                "Unknown ANZ Transactions API error. Status: ${response.status()}, Data: ${response.body()}",
                            )
                        }
                    } catch (_: TimeoutError) {
                        // No-Op
                    }
                }
                if (results.size > limit) {
                    return@withLoggedInSession results.dropLast(results.size - limit)
                }
                return@withLoggedInSession results
            }
            throw ServerException("Unknown ANZ Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
        }
    }

    override suspend fun getRealTimeTransactions(accountId: String): Flow<BankTransaction> {
        return withLoggedInSession(true) { context, page ->
            navigateToAccount(accountId, page)

            val response = page.waitForResponseAsync("https://authib.anz.com/ib/bff/accounts/v1/transactions")
            if (!response.isSuccess()) {
                throw ServerException("Unknown ANZ Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
            }

            val results = parseTransactionsFromJsonString(response.text())
            val idSet: MutableSet<String> = HashSet(results.map { it.id })
            return@withLoggedInSession flow {
                var lastUpdatedTime: Long = 0
                while (true) {
                    if (System.currentTimeMillis() - lastUpdatedTime < DEFAULT_WAIT_TIME) {
                        delay(DEFAULT_WAIT_TIME - (System.currentTimeMillis() - lastUpdatedTime))
                    }
                    lastUpdatedTime = System.currentTimeMillis()

                    page.waitForSelector("[aria-label=\"Home\"]").click()
                    navigateToAccount(accountId, page)

                    val newResponse = page.waitForResponseAsync("https://authib.anz.com/ib/bff/accounts/v1/transactions")
                    if (!newResponse.isSuccess()) {
                        allocator.release(context)
                        throw ServerException("Unknown ANZ Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
                    }

                    val newResults = parseTransactionsFromJsonString(newResponse.text()).filter { !idSet.contains(it.id) }
                    newResults.forEach { emit(it) }
                    idSet.addAll(newResults.map { it.id })
                }
            }
        }
    }
}
