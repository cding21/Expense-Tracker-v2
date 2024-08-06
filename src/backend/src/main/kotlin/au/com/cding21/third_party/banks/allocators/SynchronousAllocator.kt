package au.com.cding21.third_party.banks.allocators

import com.microsoft.playwright.BrowserContext

/**
 * Generic interface for a synchronous allocator. These allocators are based on the Locking mechanism. A consumer
 * would request access to a WebDriver instance context, wait until one is available, and release the instance context once used.
 * Only 1 consumer should have access to a given instance context once acquired. Note that each browser instance can have multiple browser (instance) contexts
 */
interface SynchronousAllocator {
    suspend fun acquire(): BrowserContext

    fun release(context: BrowserContext)

    fun close()
}
