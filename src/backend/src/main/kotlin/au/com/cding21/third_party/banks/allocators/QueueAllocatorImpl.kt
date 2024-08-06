package au.com.cding21.third_party.banks.allocators

import au.com.cding21.third_party.banks.util.withLock
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import java.util.ArrayList
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * This thread-safe WebDriver allocator operates on a FIFO basis, requests are served on a first-in basis on the next available instance.
 * There is a pool of instances serving incoming requests which can be increased or decreased.
 * An instance is defined as a browser context within a particular browser instance. Total instance size = browserNumber * instanceInBrowser
 *
 * @param poolSize Number of browser instances in the pool
 * @param instanceSize Number of contexts within an individual browser instance
 */
class QueueAllocatorImpl(poolSize: Int, instanceSize: Int) : SynchronousAllocator {
    private val instancePool: MutableList<Instance> = ArrayList()
    private val playwright: Playwright = Playwright.create()
    private val taskQueue: BlockingQueue<Action> = LinkedBlockingQueue()
    private val poolLock: Lock = ReentrantLock()

    init {
        for (i in 0 until poolSize) {
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(false).setArgs(listOf("--incognito")),
                )
            for (j in 0 until instanceSize) {
                val context = browser.newContext()
                instancePool.add(Instance(browser, context, InstanceState.FREE))
            }
        }
    }

    private fun processNext() {
        poolLock.withLock {
            val nextTask = taskQueue.peek() ?: return@withLock

            for (instance in instancePool) {
                if (instance.state == InstanceState.FREE) {
                    taskQueue.take()
                    instance.state = InstanceState.IN_USE
                    nextTask.callback(instance.context)
                }
            }
        }
    }

    override suspend fun acquire(): BrowserContext =
        suspendCoroutine {
            taskQueue.put(Action(callback = it::resume))
            processNext()
        }

    override fun release(context: BrowserContext) {
        poolLock.withLock {
            for (instance in instancePool) {
                if (instance.context == context) {
                    context.close()
                    instance.context = instance.browser.newContext()
                    instance.state = InstanceState.FREE
                }
            }
        }
        processNext()
    }

    override fun close() {
        for (instance in instancePool) {
            instance.browser.close()
        }
    }
}
