package openpablo.tiktokinjector

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

// json settings
private val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}
//Time format setting
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

class RedditScraper(id: String, secret: String) {
    private var client = initReddit(id, secret)

    suspend fun login(username: String, password: String) {
        val response: RedditSession = client.post("https://www.reddit.com/api/v1/access_token") {
            body = FormDataContent(Parameters.build {
                append("username", username)
                append("password", password)
                append("grant_type", "password")
            })
        }
        client = authReddit("bearer " + response.access_token)
        println("${time()} Successfully logged in to Reddit.")
    }

    suspend fun getTopThreads(subReddit: String, limitThreads: Int = 10, limitPosts: Int =10): MutableList<RedditThread> {
        var threads: MutableList<RedditThread> = ArrayList()
        val payload = mapOf("limit" to limitThreads.toString())
        val rawHttp = getReddit("/r/$subReddit/top/?t=day", payload, client)
        if(rawHttp.status.toString() == "200 OK") {
            println("${time()} API call for threads of $subReddit ...: ${rawHttp.status}")
            threads = parseThreadJson(rawHttp.receive())
            threads.removeAll{it.stickied == true}
            threads.removeAll{ it.ups!! < 2000}
            threads.forEach{thread ->
                val params = mapOf("limit" to limitPosts.toString())
                val posts = getPosts(thread,params, client)
                posts.removeAll{it.depth != 0}
                posts.removeAll{it.body == null}
                posts.removeAll{it.stickied == true}
                posts.removeAll{it.removed_by != null}
                thread.posts = posts
            }
        } else {
            println("${time()} Failed getting threads from $subReddit, status code: ${rawHttp.status}")
        }
        return threads
    }

    fun close() {
        client.close()
    }
}

suspend fun getReddit(
    path: String,
    payload: Map<String, String>?,
    client: HttpClient,
    baseUrl: String = "https://oauth.reddit.com"
): HttpResponse {
    return client.get(baseUrl + path) {
        method = HttpMethod.Get
        payload?.forEach {k, v ->
            parameter(k, v)
        }

    }
}

suspend fun getPosts(thread: RedditThread, payload: Map<String, String>?, client: HttpClient): MutableList<RedditPost>{
    val rawPostHttp = getReddit("/r/${thread.subreddit}/comments/${thread._id}.json", payload, client, "https://reddit.com")
    val posts: MutableList<RedditPost> = ArrayList()
    if(rawPostHttp.status.toString() == "200 OK") {
        posts.addAll(parsePostsJson(rawPostHttp.receive()))
    } else {
        println("${time()} Failed getting the posts of thread ${thread.title}")
    }
    return posts
}

fun parseThreadJson(rawJson: String): MutableList<RedditThread>{
    return json.decodeFromString<ThreadResponse>(rawJson).getThreads()
}

fun parsePostsJson(rawJson: String): MutableList<RedditPost>{
    val postsObj: List<PostsResponse> = json.decodeFromString(rawJson)
    val posts: MutableList<RedditPost> = ArrayList()
    postsObj.forEach{
        posts.addAll(it.getPosts())
    }
    return posts
}

fun initReddit(auth: String, secret: String = ""): HttpClient {
    return HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }
        install(Logging)
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = auth, password = secret)
                }
            }
        }
    }
}

private fun authReddit(auth: String): HttpClient {
    return HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }
        install(Logging)
        defaultRequest {
            header("User-Agent", "koddit by koddit123")
            header("Authorization", auth)
        }
    }
}

fun time(): String{
    return LocalDateTime.now().format(formatter)
}