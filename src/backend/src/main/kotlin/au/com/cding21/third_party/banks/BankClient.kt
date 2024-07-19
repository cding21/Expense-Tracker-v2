package au.com.cding21.third_party.banks

import au.com.cding21.third_party.banks.types.Account
import au.com.cding21.third_party.banks.types.Transaction
import kotlinx.coroutines.flow.Flow

interface BankClient {
    suspend fun getAccounts(): List<Account>

    suspend fun getTransactions(accountId: String, limit: Int): List<Transaction>

    suspend fun getRealTimeTransactions(accountId: String): Flow<Transaction>
}