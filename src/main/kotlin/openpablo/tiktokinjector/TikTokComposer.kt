package openpablo.tiktokinjector

import openpablo.tiktokinjector.Reddit.saveId
import openpablo.tiktokinjector.media.TextToSpeech
import openpablo.tiktokinjector.media.VideoComposer
import openpablo.tiktokinjector.media.CreateScreenshot
import java.io.File


val workingDir = "/home/pablo/tiktok"

fun composeVideo(thread: RedditThread, snapper: CreateScreenshot) {
    if (thread.text.length < 200) {
        generateVid(59.00, thread, snapper)
    } else if (thread.text.length < 1500) {
        generateVid(179.00, thread, snapper)
    } else {
        println("Skipped, OP was too long for ${thread._id}\"")
    }
    saveId("done.txt", thread._id)
}

fun generateVid(maxDuration: Double, thread: RedditThread, snapper: CreateScreenshot) {
    val backGroundVid = pickRandomVideo("$workingDir/raw_videos")
    val tiktok = VideoComposer(backGroundVid, 9.00 / 16.00, maxDuration)
    println("Generating audio and screenshots for ${thread._id}")
    generateAttributes(thread, snapper, false, tiktok)
    var i = 0
    while (!tiktok.vidIsFull) {
        if (thread.posts[i].text.length < 1100) {
            generateAttributes(thread.posts[i], snapper, true, tiktok)
        } else {
            tiktok.skipClip()
        }
        i += 1
    }
    tiktok.renderClip(true, "$workingDir/composed_videos/${thread.name}.mp4")
}

fun generateAttributes(
    target: RedditObject,
    screenshotObject: CreateScreenshot,
    randomizeVoice: Boolean,
    tiktok: VideoComposer
) {
    if (target.text.length < 250) {
        val musicFile = "$workingDir/voice/${target.name}.wav"
        val imageFile = "$workingDir/screenshots/${target.name}.png"
        val tts = TextToSpeech(randomizeVoice)
        tts.textToSpeech(target.text, musicFile)
        screenshotObject.snap(target.name, target.permalink, imageFile)
        val length = tiktok.addAudio(musicFile)
        tiktok.addImage(imageFile, length)
    } else {
        val paragraphs = target.text.split("\n\n")
        val tts = TextToSpeech(randomizeVoice)
        paragraphs.forEachIndexed() { i, paragraph ->
            val musicFile = "$workingDir/voice/${target.name}_$i.wav"
            val imageFile = "$workingDir/screenshots/${target.name}_$i.png"
            tts.textToSpeech(paragraph, musicFile)
            screenshotObject.snap(target.name, target.permalink, imageFile, paragraph)
            val length = tiktok.addAudio(musicFile)
            tiktok.addImage(imageFile, length)
        }
    }
}

fun pickRandomVideo(path: String): String {
    val fileList = File(path).list().filter { s -> ".part" !in s }
    return "$path/${fileList.random()}"
}

