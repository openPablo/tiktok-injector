package openpablo.tiktokinjector.media

import kotlin.system.measureTimeMillis

//Uses FFmpeg --> high performance + gpu accelerated

class VideoComposer(val filename: String, val aspect_ratio: Double, var maxDuration: Double) {
    private var audioFiles: MutableList<String> = mutableListOf()
    private var images: MutableMap<String, Double> = mutableMapOf()
    var height = 0
    var width = 0
    var duration = 0.00
    var vidIsFull = false      //stop accepting vids if attempted to surpass maxDuration
    var skippedClips = 0

    fun addAudio(audioFile: String): Double {
        val clipLength = getLength(audioFile)
        if ((clipLength + duration) <= maxDuration) {
            audioFiles.add(audioFile)
            duration += clipLength
        }else {
            skipClip()
            return 0.00
        }
        return clipLength
    }
    fun getLength(audioFile: String): Double{
        return execute("ffprobe -i $audioFile -show_entries format=duration -v quiet -of csv=\"p=0\"").toDouble()
    }
    fun skipClip(){
        skippedClips += 1
        if (skippedClips >= 2){
            vidIsFull = true
        }
    }
    fun addImage(imageFile: String, Duration: Double) {
        images[imageFile] = Duration
    }

    private fun concatAudio(outputFile: String, gpuAccelerated: Boolean) {
        var cmd = "ffmpeg -y "
        if (gpuAccelerated) {
            cmd += "-hwaccel cuda "
        }
        audioFiles.forEach { audio ->
            cmd += "-i $audio "
        }
        cmd += "-filter_complex \" "            //start of complex filter
        for (i in 0 until audioFiles.size ) {
            cmd += "[$i:0]"
        }
        cmd += "concat=n=${audioFiles.size}:v=0:a=1[out]" +
                "\" " +                         // end of complex filter
                "-map '[out]' $outputFile"
        execute(cmd)
    }

    private fun cropPortrait(aspect_ratio: Double): String {
        val videoFileHeight =
            execute("ffprobe -v error -select_streams v:0 -show_entries stream=height -of csv=s=x:p=0 $filename").toDouble()
        height = videoFileHeight.toInt()
        width = (videoFileHeight * aspect_ratio).toInt()
        return "[0:v]crop=${width}:$height [vid2]; "
    }

    //looks hacky because this function has to generate a single FFMPEG command for efficiency
    fun renderClip(gpuAccelerated: Boolean, output: String) {
        var cmd = "ffmpeg -y "
        if (gpuAccelerated) {
            cmd += "-hwaccel cuda "
        }
        val tmpAudioFile = "/tmp/tmpConcatAudio.wav"
        concatAudio(tmpAudioFile, gpuAccelerated)

        cmd += "-i $filename -i $tmpAudioFile "      //sets input files,
        images.forEach { image ->
            cmd += "-i ${image.key} "
        }
        cmd += "-filter_complex \" "                 //Start setting the complex filters
        cmd += cropPortrait(aspect_ratio)            //See cropPortrait

        var imageNr = 2                                  //ImageNr starts at 2 because the base vid and audio file are 0 and 1
        var startTime = 0.00
        val length = images.size + imageNr - 1
        val widthMargin = 80
        val bottomMargin = 300
        images.forEach { image ->
            val imageEndTime = startTime + image.value
            cmd += " [$imageNr]scale=$width-$widthMargin:-1 [pic$imageNr]; " +  //Scales the image to the video width
                    "[vid$imageNr][pic$imageNr] overlay = " +
                    "(W-w)/3:(H-h)-$bottomMargin:enable='between(t,$startTime,${imageEndTime})' " +  //sets image in center of vid
                    "[vid${imageNr + 1}] "
            if (imageNr < length) {       //check because the last item can't have a ';'
                cmd += ";"
            }
            imageNr += 1
            startTime = imageEndTime
        }

        cmd += "\" " +                                          //end of filter_complex
                "-map \"[vid$imageNr]:v\" -map \"1:a\" " +      //Overlay audio file over video
                "-t $duration " +                               //sets video length
                " $output"
        println("Rendering: \n$cmd")
        val elapsed = measureTimeMillis {
        execute(cmd)
        }
        println("${elapsed / 1000} seconds spent")
    }

    private fun execute(cmd: String): String {
        val pb = ProcessBuilder("sh", "-c", cmd)
        val process = pb.start()
        return String(process.inputStream.readAllBytes())
    }

}