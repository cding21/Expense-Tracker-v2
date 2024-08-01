package au.com.cding21.third_party.banks.allocators

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext

/**
 * Represents the state of a WebDriver instance
 */
enum class InstanceState {
    FREE,
    IN_USE,
}

data class Instance(
    val browser: Browser,
    var context: BrowserContext,
    var state: InstanceState,
)
