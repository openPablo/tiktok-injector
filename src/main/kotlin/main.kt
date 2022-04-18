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
    //reddit.login(username, password)
    //scrapeSubreddits(reddit, subRedditList, db, 6, 300)
    //reddit.close()
    //val snapper = PostScreenshotter(
    //    "https://www.reddit.com",
    //    "/usr/bin/chromedriver",
    //    "user-data-dir=/home/pablo/.config/google-chrome/Profile 1"
    //)
    //File("output.txt").readLines().forEach {
    //    val thread = db.getThread(it)
    //    if (thread != null) {
    //        try {
    //            composeVideo(thread, snapper)
    //        } catch (Ex: java.lang.Exception) {
    //            println("Failed for ${thread._id}")
    //        }
    //    }
    //}
    //snapper.close()
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
    return threads
}

fun uploadTiktoks() {
    val tiktok = TikTokUploader(
        "https://www.tiktok.com/upload",
        "/usr/bin/chromedriver",
        "user-data-dir=/home/pablo/.config/google-chrome/Profile 1"
    )
    val folder = File("/home/pablo/tiktok/composed_videos")
    val doneFolder = File("/home/pablo/tiktok/uploaded")
    folder.listFiles().forEach { video ->
        try {
            tiktok.upload(
                video.absolutePath,
                "#fyp #reddit #relationship #redditreadings #redditstories #reddit_tiktok"
            )
            val sourcePath = Paths.get(folder.absolutePath)
            val targetPath = Paths.get(doneFolder.absolutePath)
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (ex: Exception) {
            println("Failed uploading ${video.absolutePath}")
        }
    }
}