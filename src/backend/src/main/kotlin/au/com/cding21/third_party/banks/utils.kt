package au.com.cding21.third_party.banks

import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.WaitForResponseOptions
import com.microsoft.playwright.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.security.MessageDigest
import java.util.concurrent.locks.Lock

/**
 * List of shared utility functions
 */
fun String?.throwIfNull() = this ?: throw KotlinNullPointerException("Null string")
// Converts string to MD5 Hash for ID purposes (insecure)
fun String.md5Hash(): String {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(this.toByteArray()).joinToString("") { String.format("%02x", it) }
}

/**
 * Check if response code is 2XX
 */
fun Response.isSuccess(): Boolean = this.status() - 200 in 0..99

/**
 * Check if response code is 4XX
 */
fun Response.isClientError(): Boolean = this.status() - 400 in 0 .. 99

fun JsonObject.throwIfNullKey(key: String): JsonElement = if (this[key] != null) this[key]!! else throw KotlinNullPointerException("$key doesn't exist on $this")

/**
 * Async variants of blocking webdriver operations
 */

suspend fun Page.navigateAsync(url: String): Response = withContext(Dispatchers.IO) {
    return@withContext this@navigateAsync.navigate(url)
}

suspend fun Page.waitForSelectorAsync(selector: String, options: Page.WaitForSelectorOptions): ElementHandle = withContext(Dispatchers.IO) {
    return@withContext this@waitForSelectorAsync.waitForSelector(selector, options)
}

suspend fun Page.waitForResponseAsync(url: String): Response = withContext(Dispatchers.IO) {
    return@withContext this@waitForResponseAsync.waitForResponse(url) {}
}

suspend fun Page.waitForResponseAsync(url: String, options: WaitForResponseOptions): Response = withContext(Dispatchers.IO) {
    return@withContext this@waitForResponseAsync.waitForResponse(url, options) {}
}

fun Lock.withLock(callback: () -> Unit) {
    this.lock()
    callback()
    this.unlock()
}