package openpablo.tiktokinjector.media

import com.google.cloud.texttospeech.v1.*
import com.google.protobuf.ByteString
import java.io.File
import java.io.FileOutputStream

class TextToSpeech(randomizeVoice: Boolean) {
    var voice: VoiceSelectionParams =
        VoiceSelectionParams.newBuilder()
            .setLanguageCode("en-US")
            .setSsmlGender(SsmlVoiceGender.MALE)
            .build()

    init {
        if (randomizeVoice) {
            voice = selectVoice()
        }
    }

    fun textToSpeech(text: String, filename: String) {
        val file = File(filename)
        if (!isFileExists(file)) {
            val sanitized = removeUrl(text)
            TextToSpeechClient.create().use { textToSpeechClient ->
                val input = SynthesisInput.newBuilder().setText(sanitized).build()
                val audioConfig =
                    AudioConfig.newBuilder()
                        .setAudioEncoding(AudioEncoding.LINEAR16).build() //wav file

                val response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)

                // Get the audio contents from the response
                val audioContents: ByteString = response.audioContent
                FileOutputStream(filename).use { out ->
                    out.write(audioContents.toByteArray())
                }
            }
        }
    }

    fun selectVoice(): VoiceSelectionParams {
        val voiceName = pickRandomVoice(voiceList)
        var gender = if (voiceName[2] == "MALE") SsmlVoiceGender.MALE else SsmlVoiceGender.FEMALE
        voice =
            VoiceSelectionParams.newBuilder()
                .setLanguageCode(voiceName[0])
                .setName(voiceName[1])
                .setSsmlGender(gender)
                .build()
        return voice
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
    "en-AU:en-AU-Wavenet-A:FEMALE",
    "en-AU:en-AU-Wavenet-B:MALE",
    "en-AU:en-AU-Wavenet-C:FEMALE",
    "en-AU:en-AU-Wavenet-D:MALE",
    "en-US:en-US-Wavenet-A:MALE",
    "en-US:en-US-Wavenet-B:MALE",
    "en-US:en-US-Wavenet-C:FEMALE",
    "en-US:en-US-Wavenet-D:MALE",
    "en-US:en-US-Wavenet-E:FEMALE",
    "en-US:en-US-Wavenet-F:FEMALE",
    "en-US:en-US-Wavenet-G:FEMALE",
    "en-US:en-US-Wavenet-H:FEMALE",
    "en-US:en-US-Wavenet-I:MALE",
    "en-US:en-US-Wavenet-J:MALE",
    "en-US:en-US-Wavenet-A:MALE",
    "en-US:en-US-Wavenet-B:MALE",
    "en-US:en-US-Wavenet-C:FEMALE",
    "en-US:en-US-Wavenet-D:MALE",
    "en-US:en-US-Wavenet-E:FEMALE",
    "en-US:en-US-Wavenet-F:FEMALE",
    "en-US:en-US-Wavenet-G:FEMALE",
    "en-US:en-US-Wavenet-H:FEMALE",
    "en-US:en-US-Wavenet-I:MALE",
    "en-US:en-US-Wavenet-J:MALE",
    "en-GB:en-GB-Wavenet-A:FEMALE",
    "en-GB:en-GB-Wavenet-B:MALE",
    "en-GB:en-GB-Wavenet-C:FEMALE",
    "en-GB:en-GB-Wavenet-D:MALE",
    "en-GB:en-GB-Wavenet-F:FEMALE",
    "en-US:en-US-Wavenet-A:MALE",
    "en-US:en-US-Wavenet-B:MALE",
    "en-US:en-US-Wavenet-C:FEMALE",
    "en-US:en-US-Wavenet-D:MALE",
    "en-US:en-US-Wavenet-E:FEMALE",
    "en-US:en-US-Wavenet-F:FEMALE",
    "en-US:en-US-Wavenet-G:FEMALE",
    "en-US:en-US-Wavenet-H:FEMALE",
    "en-US:en-US-Wavenet-I:MALE",
    "en-US:en-US-Wavenet-J:MALE"
)