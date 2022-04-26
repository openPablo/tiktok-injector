package openpablo.tiktokinjector

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeDriver
import java.time.Duration
import java.util.logging.Level

open class BrowseTo(val baseUrl: String, pathToDriver: String, chromeProfile: String) {
    var driver: ChromeDriver
    var options = ChromeOptions()
    init {
        options = ChromeOptions()
        //options.addArguments("start-maximized")
        options.setExperimentalOption("excludeSwitches", listOf("enable-automation"))
        options.setExperimentalOption("useAutomationExtension", false)
        options.addArguments(chromeProfile)
        options.addArguments("--disable-blink-features=AutomationControlled")

        options.addArguments("user-agent=Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:98.0) Gecko/20100101 Firefox/98.0");
        options.addArguments("--window-size=1280,1440")
        System.setProperty("webdriver.chrome.driver",pathToDriver);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        driver = ChromeDriver(options)
        driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})")
        driver[baseUrl]
    }

    fun sendTextByDivXpath(text: String, xpath: String) {
        sleep()
        val timout = Duration.ofSeconds(10)
        WebDriverWait(driver, timout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))).sendKeys(text)
    }
    fun clickByXpath(xpath: String){
        sleep()
        val timout = Duration.ofSeconds(150)
        WebDriverWait(driver, timout).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click()
    }
    fun waitUntilXpath(xpath: String){
        val timout = Duration.ofSeconds(15)
        WebDriverWait(driver, timout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
    }
    fun clickXpath(selector: String) {
        sleep()
        if (checkifExistsXpath(selector)) {
            val element = driver.findElement(By.xpath(selector))
            driver.executeScript("arguments[0].click();", element)
        }
    }
    fun sleep(lower: Int = 500, upper: Int = 2000 ){
        val rand = (lower..upper).random()
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
