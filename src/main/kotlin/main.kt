
import openpablo.tiktokinjector.*
import openpablo.tiktokinjector.media.createScreenshot
import java.io.File

fun main() {
    val id = System.getenv("id")
    val secret = System.getenv("secret")
    val username = System.getenv("username")
    val password = System.getenv("password")
    val mongoConnStr = System.getenv("mongoConnStr")
    val subRedditList = "AskReddit relationship_advice amItheAsshole".split(" ").toTypedArray()


    val db  = RedditDataHandler(mongoConnStr)
    val reddit = RedditScraper(id, secret)
    //reddit.login(username, password)
    //scrapeSubreddits(reddit, subRedditList, db, 10, 300)
    //reddit.close()
    val snapper = createScreenshot("https://www.reddit.com","/usr/bin/geckodriver")

    File("output.txt").readLines().forEach {
        val thread = db.getThread(it)
        if (thread != null){
            composeVideo(thread, snapper)
        }
    }
    snapper.close()
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

