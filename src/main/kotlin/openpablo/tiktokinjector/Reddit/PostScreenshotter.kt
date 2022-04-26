package openpablo.tiktokinjector.Reddit

import openpablo.tiktokinjector.BrowseTo
import openpablo.tiktokinjector.media.isFileExists
import org.openqa.selenium.*
import java.io.File


//Requires gecko driver... 'which geckodriver'
class PostScreenshotter(baseUrl: String, pathToDriver: String, chromeProfile: String) :
    BrowseTo(baseUrl = baseUrl, pathToDriver = pathToDriver, chromeProfile = chromeProfile) {
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

            val post = driver.findElements(By.id(id))[0]
            if (paragraph != "") {
                val postBody = post.findElement(By.xpath("//div[@class='_292iotee39Lmt0MkQZ2hPV RichTextJSON-root']"))
                val htmlParagraph = "<p class=\"_1qeIAgB0cPwnLhDF9XSiJM\" > $paragraph</p>" +
                                    "..."
                editElement(postBody, htmlParagraph)
            }
            val screenshot = screenshot(post)
            screenshot.copyTo(dest)
        }
    }

    private fun editElement(element: WebElement, innerHtml: String) {
        val js: JavascriptExecutor = driver
        js.executeScript( "var ele=arguments[0]; ele.innerHTML = arguments[1];", element, innerHtml)
    }

    private fun screenshot(element: WebElement): File {
        element.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
        element.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
        val screenshotFile = (element as TakesScreenshot).getScreenshotAs(OutputType.FILE)
        element.sendKeys(Keys.chord(Keys.CONTROL, "0"));
        return screenshotFile
    }
}
