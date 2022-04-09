import openpablo.koddit.TikTokUploader

fun main () {
    val username = System.getenv("tiktokusername")
    val password = System.getenv("tiktokpassword")

    var tiktok = TikTokUploader("https://www.tiktok.com/upload", "/usr/bin/geckodriver")
    tiktok.login(username, password)
    tiktok.upload("/tmp/test","#fyp #reddit #relationship #redditreadings #redditstories #reddit_tiktok #redditstoriestts")

}