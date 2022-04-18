package openpablo.tiktokinjector

import openpablo.tiktokinjector.BrowseTo
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class TikTokUploader(baseUrl: String, pathToDriver: String, chromeProfile: String) :
    BrowseTo(baseUrl = baseUrl, pathToDriver = pathToDriver, chromeProfile = chromeProfile) {
    fun upload(video_path: String, tags: String) {
        driver[baseUrl]
        selectTiktokIframe()
        sendTextByDivXpath(video_path, "//input[@type='file']")
        sendTextByDivXpath(tags, "//html/body/div[1]/div/div/div/div/div[2]/div[2]/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div/div/div")
        clickByXpath("/html/body/div[1]/div/div/div/div/div[2]/div[2]/div[7]/div[2]/button")
        waitUntilXpath("/html/body/div[3]/div/div/div[1]/div[2]")
    }
    private fun selectTiktokIframe(){
        val timout = Duration.ofSeconds(10)
        val iframe = WebDriverWait(driver, timout).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div/div/div[2]/div/iframe")))
        driver.switchTo().frame(iframe)
    }
}