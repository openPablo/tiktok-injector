package openpablo.tiktokinjector.media

import openpablo.tiktokinjector.BrowseTo
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import java.io.File


//Requires gecko driver... 'which geckodriver'
class CreateScreenshot(baseUrl: String, geckoDriverPath1: String, firefoxProfile: String) :
    BrowseTo(baseUrl = baseUrl, geckoDriverPath1 = geckoDriverPath1, firefoxProfile = firefoxProfile) {
    init {
        clickXpath("//*[text()[contains(., 'Accept all')]]")
    }

    fun snap(id: String, url: String, path: String, paragraph: String = "") {
        val dest = File(path)
        if (!isFileExists(dest)) {
            driver[baseUrl + url]
            val is18Plus =
                driver.findElements(By.xpath("//*[text()[contains(., 'You must be at least eighteen years old to view this content. Are you over eighteen and willing to see adult content?')]]"))
                    .isNotEmpty()
            if (is18Plus) {
                clickXpath("//*[text()[contains(., 'Yes')]]")
                clickXpath("//*[text()[contains(., 'Click to see nsfw')]]")
            }

            val htmlElement = driver.findElements(By.id(id))
            var screenshot: File
            if (paragraph == "") {
                screenshot = screenshot(htmlElement[0])
                screenshot.copyTo(dest)
            } else {
                val pieces = paragraph.replace("'", "").chunked(10)
                var i = 0
                var found = false
                while (i < pieces.size && !found) {
                    val paragraphEles = htmlElement[0].findElements(By.xpath("//*[text()[contains(., '${pieces[i]}')]]"))
                    if (paragraphEles.size > 0) {
                        screenshot = screenshot(paragraphEles.last())
                        found = true
                        screenshot.copyTo(dest)
                    }
                    i += 1
                }
            }
        }
    }

    private fun screenshot(element: WebElement): File {
        return (element as TakesScreenshot).getScreenshotAs(OutputType.FILE)
    }
}
