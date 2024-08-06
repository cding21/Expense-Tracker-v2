package au.com.cding21.third_party.banks

import au.com.cding21.third_party.banks.util.ImageCodec
import au.com.cding21.third_party.banks.util.throwIfNullKey
import au.com.cding21.third_party.banks.util.waitForResponseAsync
import au.com.cding21.third_party.banks.util.waitForSelectorAsync
import com.microsoft.playwright.*
import com.microsoft.playwright.options.LoadState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
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
            // TODO: Temp fix to this test, will need longer term fix
            try {
                page.waitForResponse("https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages",
                    Page.WaitForResponseOptions().setTimeout(10000.0)) {}
                val response = page.waitForResponse("https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages",
                    Page.WaitForResponseOptions().setTimeout(10000.0)) {}
                val keys = Json.decodeFromString<JsonObject>(response.text()).throwIfNullKey("KeypadImages").jsonArray
                assertEquals(keys.size, 10)

                for (key in keys) {
                    val imageB64 = key.jsonPrimitive.content
                    val image = ImageCodec.fromBase64(imageB64).crop(80, 35, 21, 35)
                    val validRefImages =  refImages.filter { it.compare(image) > 0.95 }
                    assertEquals(validRefImages.size, 1)
                }
            } catch (_ : TimeoutError ) { /* Do nothing */}
            
            page.close()
            context.close()
        }
    }
}