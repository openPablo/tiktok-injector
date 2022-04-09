package openpablo.tiktokinjector.media

import com.google.cloud.texttospeech.v1.*
import com.google.protobuf.ByteString
import java.io.File
import java.io.FileOutputStream

fun textToSpeech(text: String, filename: String, randomizeVoice: Boolean) {
    val file = File(filename)
    if (!isFileExists(file)) {
        val sanitized = removeUrl(text)
        TextToSpeechClient.create().use { textToSpeechClient ->
            val input = SynthesisInput.newBuilder().setText(sanitized).build()
            var voice =
                VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.MALE)
                    .build()
            if (randomizeVoice) {
                val voiceName = pickRandomVoice(voiceList)
                voice =
                    VoiceSelectionParams.newBuilder()
                        .setLanguageCode(voiceName[0])
                        .setName(voiceName[1])
                        .build()
            }

            val audioConfig =
                AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16).build() //wav file

            val response =
                textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)

            // Get the audio contents from the response
            val audioContents: ByteString = response.audioContent
            FileOutputStream(filename).use { out ->
                out.write(audioContents.toByteArray())
                println("Audio content written to file \"$filename\"")
            }
        }
    } else {
        println("Speech file was already generated!")
    }

}

fun isFileExists(file: File): Boolean {
    return file.exists() && !file.isDirectory
}

fun pickRandomVoice(voices: List<String>): List<String> {
    return voices.random().split(":")
}
private fun removeUrl(commentstr: String): String {
    return commentstr.replace("http.*?(\\Z|\\s)".toRegex(), "")
}

var voiceList = listOf(
    "en-AU:Wavenet-A",
    "en-AU:Wavenet-B",
    "en-AU:Wavenet-C",
    "en-AU:Wavenet-D",
    "en-US:Wavenet-A",
    "en-US:Wavenet-B",
    "en-US:Wavenet-C",
    "en-US:Wavenet-D",
    "en-US:Wavenet-E",
    "en-US:Wavenet-F",
    "en-US:Wavenet-G",
    "en-US:Wavenet-H",
    "en-US:Wavenet-I",
    "en-US:Wavenet-J",
    "en-US:Wavenet-A",
    "en-US:Wavenet-B",
    "en-US:Wavenet-C",
    "en-US:Wavenet-D",
    "en-US:Wavenet-E",
    "en-US:Wavenet-F",
    "en-US:Wavenet-G",
    "en-US:Wavenet-H",
    "en-US:Wavenet-I",
    "en-US:Wavenet-J",
    "en-GB:Wavenet-A",
    "en-GB:Wavenet-B",
    "en-GB:Wavenet-C",
    "en-GB:Wavenet-D",
    "en-GB:Wavenet-F",
    "en-US:Wavenet-A",
    "en-US:Wavenet-B",
    "en-US:Wavenet-C",
    "en-US:Wavenet-D",
    "en-US:Wavenet-E",
    "en-US:Wavenet-F",
    "en-US:Wavenet-G",
    "en-US:Wavenet-H",
    "en-US:Wavenet-I",
    "en-US:Wavenet-J"
)