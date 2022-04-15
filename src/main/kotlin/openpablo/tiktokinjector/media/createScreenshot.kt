package openpablo.tiktokinjector.media

import openpablo.tiktokinjector.BrowseTo
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import java.io.File


//Requires gecko driver... 'which geckodriver'
class createScreenshot(baseUrl: String, geckoDriverPath1: String, firefoxProfile: String) :
    BrowseTo(baseUrl = baseUrl, geckoDriverPath1 = geckoDriverPath1, firefoxProfile = firefoxProfile) {
    init {
        System.setProperty("webdriver.gecko.driver", geckoDriverPath1)
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")
        driver[baseUrl]
        clickXpath("//*[text()[contains(., 'Accept all')]]")
    }

    fun snap(id: String, url: String, path: String, smallScreenshots: Boolean): Int {
        val dest = File("$path/$id.png")
        if (!isFileExists(dest) and !smallScreenshots) {
            driver[baseUrl + url]
            val is18Plus =
                driver.findElements(By.xpath("//*[text()[contains(., 'You must be at least eighteen years old to view this content. Are you over eighteen and willing to see adult content?')]]"))
                    .isNotEmpty()
            if (is18Plus) {
                clickXpath("//*[text()[contains(., 'Yes')]]")
                clickXpath("//*[text()[contains(., 'Click to see nsfw')]]")
            }

            val htmlElement = driver.findElements(By.id(id))
            val screenshot = screenshot(htmlElement[0])
            screenshot.copyTo(dest)

            if(smallScreenshots){
                val paragraphs = htmlElement[0].findElements(By.className("_1qeIAgB0cPwnLhDF9XSiJM"))
                paragraphs.forEachIndexed{ i, paragraph->
                    val dest = File("$path/$id-$i.png")
                    if(!isFileExists(dest)){
                        (paragraph as TakesScreenshot).getScreenshotAs(OutputType.FILE).copyTo(dest)
                    }
                }
                return paragraphs.size
            }
            return 1
        }
        return 0
    }

    fun screenshot(element: WebElement): File{
        return (element as TakesScreenshot).getScreenshotAs(OutputType.FILE)
    }
}
