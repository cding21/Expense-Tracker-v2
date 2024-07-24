package au.com.cding21.third_party.banks.allocators

import com.microsoft.playwright.BrowserContext

data class Action(
    val callback: ((browser: BrowserContext) -> Unit)
)