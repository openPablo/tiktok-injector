package openpablo.tiktokinjector.Reddit

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import openpablo.tiktokinjector.RedditThread
import kotlin.system.exitProcess
import org.litote.kmongo.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

class RedditDataHandler(mongoConn: String) {
    var mongoClient: MongoClient? = null
    var database: MongoDatabase? = null
    var collection: MongoCollection<RedditThread>? = null
    var res = "{\"batchSize\":null}"

    fun writeThreads(threads: MutableList<RedditThread>) {
        threads.forEach {
            collection?.save(it)
        }
        println("Wrote threads to database :^)")
    }

    fun sendThreads(threads: MutableList<RedditThread>, outputFile: String) {
        threads.forEach {
            saveId(outputFile, it._id)
        }
        println("send to queue! :)")
    }

    fun getThread(id: String): RedditThread? {
        return collection?.findOne(RedditThread::_id eq id)
    }

    fun existsInDb(thread: RedditThread): Boolean {
        val mongoResult = collection?.countDocuments(RedditThread::_id eq thread._id)
        return if (mongoResult is Long) {
            mongoResult > 0
        } else {
            false
        }
    }

    init {
        try {
            mongoClient = KMongo.createClient(mongoConn)
            database = mongoClient?.getDatabase("myFirstDatabase")
            collection = database?.getCollection()
        } catch (e: Throwable) {
            println("Failed initializing database, connection string: $mongoConn")
            exitProcess(1)
        }
    }
}

fun saveId(filename: String, id: String) {
    val pw = PrintWriter(
        FileOutputStream(
            File(filename),
            true
        )
    )
    pw.append("$id\n")
    pw.close()
}