package openpablo.tiktokinjector

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import java.io.File
import java.time.Duration

open class BrowseTo(val baseUrl: String, pathToDriver: String, chromeProfile: String) {
    var driver: ChromeDriver
    var options = ChromeOptions()
    init {
        options = ChromeOptions()
        options.addArguments("start-maximized")
        options.setExperimentalOption("excludeSwitches", listOf("enable-automation"))
        options.setExperimentalOption("useAutomationExtension", false)
        options.addArguments(chromeProfile)
        options.addArguments("--window-size=1280,1440")
        System.setProperty("webdriver.chrome.driver",pathToDriver);
        driver = ChromeDriver(options)
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
        sleep()
        val timout = Duration.ofSeconds(15)
        WebDriverWait(driver, timout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))).click()
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
