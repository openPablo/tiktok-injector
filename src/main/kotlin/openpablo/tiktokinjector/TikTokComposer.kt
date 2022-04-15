package openpablo.tiktokinjector

import openpablo.tiktokinjector.Reddit.saveId
import openpablo.tiktokinjector.media.VideoComposer
import openpablo.tiktokinjector.media.createScreenshot
import openpablo.tiktokinjector.media.textToSpeech
import java.io.File


val workingDir = "/home/pablo/tiktok"

fun composeVideo(thread: RedditThread, snapper: createScreenshot) {
    if (thread.text.length < 200){
        generateVid(59.00,thread, snapper)
    }
    else if(thread.text.length < 1200){
        generateVid(179.00,thread, snapper)
    }
    else {
        println("Skipped, OP was too long for ${thread._id}\"")
    }
    saveId("done.txt", thread._id)
}

fun generateVid(maxDuration: Double, thread: RedditThread, snapper: createScreenshot){
    val backGroundVid = pickRandomVideo("$workingDir/raw_videos")
    val tiktok = VideoComposer(backGroundVid, 9.00 / 16.00, maxDuration)
    println("Generating audio and screenshots for ${thread._id}")
    val numberOfScreenshots = generateAttributes(thread, snapper, false)
    addRedditToVid(tiktok, thread, numberOfScreenshots)
    var i = 0
    while (!tiktok.vidIsFull) {
        if(thread.posts[i].text.length < 1100){
            val numberOfScreenshots = generateAttributes(thread.posts[i], snapper, true)
            addRedditToVid(tiktok, thread.posts[i], numberOfScreenshots)
        }
        else {
            tiktok.skipClip()
        }
        i += 1
    }
    tiktok.renderClip(true, "$workingDir/composed_videos/${thread.name}.mp4")
}

fun generateAttributes(target: RedditObject, screenshotObject: createScreenshot, randomizeVoice: Boolean): Int {
    val musicFile = "$workingDir/voice/${target.name}.wav"
    var numberOfScreenshots = 1
    if(target.text.length <  150){
        textToSpeech(target.text, musicFile, randomizeVoice)
        screenshotObject.snap(target.name, target.permalink, "$workingDir/screenshots", false)
    }
    else {
        textToSpeech(target.text, musicFile, randomizeVoice)
        numberOfScreenshots =  screenshotObject.snap(target.name, target.permalink, "$workingDir/screenshots", true)
    }
    return numberOfScreenshots
}

fun addRedditToVid(tiktok: VideoComposer, target: RedditObject, numberOfScreenshots: Int): Double {
    var length = tiktok.addAudio("$workingDir/voice/${target.name}.wav")
    if (length != 0.00) {
        tiktok.addImage("$workingDir/screenshots/${target.name}.png", length)
    }
    return length
}

fun pickRandomVideo(path: String): String {
    val fileList = File(path).list().filter { s -> ".part" !in s }
    return "$path/${fileList.random()}"
}

