import openpablo.tiktokinjector.TikTokUploader

fun main() {
    val username = System.getenv("tiktokusername")
    val password = System.getenv("tiktokpassword")

    val tiktok = TikTokUploader(
        "https://www.tiktok.com/upload",
        "/usr/bin/chromedriver",
        "user-data-dir=/home/pablo/.config/google-chrome/Profile 1"
    )
    tiktok.upload("/home/pablo/tiktok/composed_videos/t3_u67dli.mp4", "#fyp #reddit #relationship #redditreadings #redditstories #reddit_tiktok")

}