import openpablo.tiktokinjector.Reddit.PostScreenshotter
import openpablo.tiktokinjector.Reddit.RedditDataHandler
import openpablo.tiktokinjector.RedditScraper
import openpablo.tiktokinjector.RedditThread
import openpablo.tiktokinjector.TikTokUploader
import openpablo.tiktokinjector.composeVideo
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

suspend fun main() {
    val id = System.getenv("id")
    val secret = System.getenv("secret")
    val username = System.getenv("username")
    val password = System.getenv("password")
    val mongoConnStr = System.getenv("mongoConnStr")
    val subRedditList =
        "AskReddit relationship_advice amItheAsshole TrueOffMyChest AskRedditAfterDark".split(" ").toTypedArray()


    val db = RedditDataHandler(mongoConnStr)
    val reddit = RedditScraper(id, secret)
    reddit.login(username, password)

    scrapeSubreddits(reddit, subRedditList, db, 3, 300)
    createTiktoks(db)
    uploadTiktoks()
}

suspend fun scrapeSubreddits(
    reddit: RedditScraper,
    subRedditList: Array<String>,
    db: RedditDataHandler,
    limitThread: Int,
    limitPosts: Int
): MutableList<RedditThread> {
    val threads: MutableList<RedditThread> = ArrayList()
    subRedditList.forEach { subReddit ->
        val newThreads = reddit.getTopThreads(subReddit, limitThread, limitPosts)
        newThreads.removeAll { thread ->
            db.existsInDb(thread)
        }
        threads.addAll(newThreads)
    }
    db.sendThreads(threads)
    db.writeThreads(threads)
    reddit.close()
    return threads
}

fun createTiktoks(db: RedditDataHandler) {
    val snapper = PostScreenshotter(
        "https://www.reddit.com",
        "/usr/bin/chromedriver",
        "user-data-dir=/home/pablo/.config/google-chrome/Profile 1"
    )
    val todoVids = File("output.txt").readLines()
    val failedVids = mutableListOf<String>("")
    todoVids.forEach {
        val thread = db.getThread(it)
        if (thread != null) {
            try {
                composeVideo(thread, snapper)
            } catch (Ex: java.lang.Exception) {
                failedVids.add(thread._id)
                println("Failed for ${thread._id}")
            }
        }
    }
    failedVids.forEach {
        File("output.txt").writeText(it)
    }
    snapper.close()
}

fun uploadTiktoks() {
    val tiktok = TikTokUploader(
        "https://www.tiktok.com/upload",
        "/usr/bin/chromedriver",
        "user-data-dir=/home/pablo/.config/google-chrome/Profile 1"
    )
    val uploadDir = File("/home/pablo/tiktok/composed_videos")
    uploadDir.listFiles().forEach { video ->
        println("Uploading ${video.absolutePath}")
        try {
            tiktok.upload(
                video.absolutePath,
                "#fyp #reddit #relationship #redditreadings #redditstories #reddit_tiktok"
            )
            val sourcePath = Paths.get(video.absolutePath)
            val targetPath = Paths.get(video.absolutePath.replace("composed_videos", "uploaded"))
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (ex: Exception) {
            println("Failed uploading ${video.absolutePath}")
        }
    }
}