package openpablo.tiktokinjector

import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File


class TikTokUploader(baseUrl: String, geckoDriverPath1: String, firefoxProfile: String) :
    BrowseTo(baseUrl = baseUrl, geckoDriverPath1 = geckoDriverPath1, firefoxProfile = firefoxProfile) {
    fun upload(filename: String, tags: String) {
        Thread.sleep(2_000)
        driver["https://www.tiktok.com/upload"]
        sendTextByDivClass(tags, "public-DraftStyleDefault-ltr")
    }

}

open class BrowseTo(baseUrl: String, geckoDriverPath1: String, firefoxProfile: String) {
    var driver: FirefoxDriver
    var options = FirefoxOptions()

    init {
        val profile = FirefoxProfile()
        profile.setPreference(
            "profile",
            firefoxProfile
        )

        options.setCapability("browserName", "Firefox")
        options.setCapability("browserVersion", "95.0")
        val browserstackOptions = HashMap<String, Any>()
        browserstackOptions["os"] = "OS X"
        browserstackOptions["osVersion"] = "Monterey"
        browserstackOptions["buildName"] = "Selenium Java Firefox Profile"
        browserstackOptions["sessionName"] = "Selenium Java Firefox Profile"
        options.setCapability("bstack:options", browserstackOptions)

        System.setProperty("webdriver.gecko.driver", geckoDriverPath1)
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")

        profile.setPreference("dom.webdriver.enabled", false)
        profile.setPreference("useAutomationExtension", false)
        options.profile = profile
        options.addArguments("--width=1280")
        options.addArguments("--height=1440")
        options.setLogLevel(FirefoxDriverLogLevel.FATAL)
        driver = FirefoxDriver(options)
        driver[baseUrl]

    }

    fun sendTextByDivClass(text: String, selector: String) {
        sleep()
        val ele = driver.findElements(By.className(selector)).isNotEmpty()
        if (ele) {
            driver.findElement(By.className(selector)).sendKeys(text)
        }
    }

    fun clickXpath(selector: String) {
        if (checkifExistsXpath(selector)) {
            val element = driver.findElement(By.xpath(selector))
            driver.executeScript("arguments[0].click();", element)
        }
    }
    private fun sleep(){
        val rand = (500..2000).random()
        Thread.sleep(rand.toLong())
    }
    fun checkifExistsXpath(selector: String): Boolean {
        val time = java.time.Duration.ofSeconds(5)
        val webdriver = WebDriverWait(driver, time)
        return try{
            webdriver.until(ExpectedConditions.elementToBeClickable(By.xpath(selector)))
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun close() {
        driver.close()
    }
}
