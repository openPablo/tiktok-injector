package openpablo.tiktokinjector

import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration


class TikTokUploader(baseUrl: String, geckoDriverPath1: String) :
    BrowseTo(baseUrl = baseUrl, geckoDriverPath1 = geckoDriverPath1) {
    fun login(username: String,password: String) {
        Thread.sleep(1000)
        clickText("Use phone / email / username")
        clickText("Log in with email or username")
        sendText(username, "email")
        sendText(password, "password")
        clickLogin()
    }

    fun upload(filename: String, tags: String) {
        Thread.sleep(2_000)
        driver["https://www.tiktok.com/upload"]
        sendTextByDivClass(tags, "public-DraftStyleDefault-ltr")
    }

    fun clickLogin() {
        if (checkIfExists("Log in")) {
            val element = driver.findElement(By.className("login-button-31D24"))
            driver.executeScript("arguments[0].click();", element)
        }
    }
}

open class BrowseTo(baseUrl: String, geckoDriverPath1: String) {
    var driver = FirefoxDriver()
    val baseUrl = "https://www.reddit.com"
    val time = Duration.ofSeconds(3)
    var wait = WebDriverWait(driver, time)

    init {
        System.setProperty("webdriver.gecko.driver", geckoDriverPath1)
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")
        driver[baseUrl]
    }

    fun sendText(text: String, selector: String) {
        if (checkIfExists(selector)) {
            driver.findElement(By.name(selector)).sendKeys(text)
        }
    }

    fun sendTextByDivClass(text: String, selector: String) {
        val ele = driver.findElements(By.className(selector)).isNotEmpty()
        if (ele) {
            driver.findElement(By.className(selector)).sendKeys(text)
        }
    }

    fun clickText(selector: String) {
        if (checkIfExists(selector)) {
            val element = driver.findElement(By.xpath("//*[text()[contains(., '$selector')]]"))
            driver.executeScript("arguments[0].click();", element)
        }
    }

    fun checkIfExists(selector: String): Boolean {
        Thread.sleep(300)
        return driver.findElements(By.xpath("//*[text()[contains(., '$selector')]]")).isNotEmpty()
    }

    fun close() {
        driver.close()
    }
}
