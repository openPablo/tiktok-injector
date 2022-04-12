import openpablo.tiktokinjector.TikTokUploader

fun main() {
    val username = System.getenv("tiktokusername")
    val password = System.getenv("tiktokpassword")

    val tiktok = TikTokUploader(
        "https://www.tiktok.com/upload",
        "/usr/bin/geckodriver",
        "/home/pablo/.mozilla/firefox/jg72zd8v.default"
    )
    tiktok.login(username, password)
    tiktok.upload("/tmp/test", "#fyp #reddit #relationship #redditreadings #redditstories #reddit_tiktok")

}