package au.com.cding21.third_party.banks

import au.com.cding21.third_party.banks.util.ImageCodec
import au.com.cding21.third_party.banks.util.waitForResponseAsync
import au.com.cding21.third_party.banks.util.waitForSelectorAsync
import com.microsoft.playwright.*
import com.microsoft.playwright.options.LoadState
import kotlin.test.Test
import kotlin.test.assertEquals

class INGTest {
    @Test
    fun ensureKeypadImagesAreValid() {
        val refImages = INGClientImpl.IMAGE_REFERENCES
        val playwright = Playwright.create()
        val browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true).setArgs(listOf("--incognito")))
        // Verify 3 random session all has valid image
        for (i in 0 until 3) {
            val context = browser.newContext()
            val page = context.newPage()
            page.navigate("https://www.ing.com.au/securebanking/")

            try{
                page.waitForResponse("https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages", Page.WaitForResponseOptions().setTimeout(10000.0)) {}
                page.waitForResponse("https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages", Page.WaitForResponseOptions().setTimeout(3000.0)) {}
            } catch (_: TimeoutError) {/* This wait is flakey sometimes */}
            page.waitForSelector("#cifField", Page.WaitForSelectorOptions().setTimeout(5000.0))

            // Use DOM parsing to emulate real client behaviour
            val keys = page.querySelector("#keypad > div").querySelectorAll("[role=\"button\"]").filter { it.getAttribute("position") != null }
            assertEquals(keys.size, 10)

            for (key in keys) {
                val imageB64 = key.querySelector("img").getAttribute("src").split(",")[1]
                val image = ImageCodec.fromBase64(imageB64).crop(80, 35, 21, 35)
                val validRefImages =  refImages.filter { it.compare(image) > 0.95 }
                assertEquals(validRefImages.size, 1)
            }
            page.close()
            context.close()
        }
    }
}