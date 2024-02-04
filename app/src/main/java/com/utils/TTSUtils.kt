package com.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.google.android.exoplayer2.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class TTSUtils(private val context: Context) {

    private lateinit var textToSpeech: TextToSpeech
    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var handler = Handler(Looper.getMainLooper())

    private var isPrepared = false
    private var playbackPosition: Long = 0

    private var ttsContent: String = "1"
    private var splitLength: Int = 50
    private var mp3List: MutableList<String> = mutableListOf("1")
    private var filePathList: MutableList<String> = mutableListOf("1")

    private var pitch = 1.0f
    private var speed = 1.0f

    /**
     * 构造函数，textToSpeech初始化
     */
    init {
        textToSpeech = TextToSpeech(this.context,TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val voices = textToSpeech.voices
                for (voice in voices) {
                    textToSpeech.voice = voice
                }
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 当前语言不可用
                    Log.e("", "Language not supported")
                }
            } else {
                // 初始化失败
                Log.e("", "TextToSpeech initialization failed")
            }
        })
    }

    //控制台警告
    private fun logWarn(string: String) {
        Log.w("Warning","" + string)
    }

    /**
     * 音频合成并播放
     */
    @SuppressLint("SimpleDateFormat")
    fun play() {
        if (exoPlayer.isPlaying || isPrepared) {
            exoPlayer.stop()
            isPrepared = false
        }
        val totalCount = mp3List.size

        //播放监听
        val exoplayerListener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                        for (path in filePathList) {
                            val dfile = File(path)
                            if (dfile.exists()) {
                                dfile.delete()
                            }
                        }
                        exoPlayer.release()
                        isPrepared = false
                    }
                }
            }
        }

        //音频合成监听
        val ttsListener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if ((utteranceId != null) && (utteranceId.toInt() == 0) && (utteranceId.toInt() < (totalCount - 1))) {
                    val count = utteranceId.toInt() + 1
                    thread {
                        val currentDateTime = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                        val file = File(context.getExternalFilesDir(null), "m$currentDateTime$utteranceId")
                        filePathList.add(count, file.path)
                        textToSpeech.setOnUtteranceProgressListener(this)
                        textToSpeech.synthesizeToFile(mp3List[count],null, file, (count).toString())
                    }
                    handler.post {
                        val mfile = File(filePathList[0])
                        val mediaItem = MediaItem.fromUri(Uri.fromFile(mfile))
                        exoPlayer.addListener(exoplayerListener)
                        exoPlayer.addMediaItem(mediaItem)
                        exoPlayer.playbackParameters = PlaybackParameters(speed, pitch)
                        exoPlayer.prepare()
                        isPrepared = true
                        exoPlayer.play()
                    }
                } else if ((utteranceId != null) && (utteranceId.toInt() < (totalCount - 1))) {
                    val count = utteranceId.toInt() + 1
                    handler.post {
                        exoPlayer.addMediaItem(MediaItem.fromUri(Uri.fromFile(File(filePathList[count - 1]))))
                    }

                    thread {
                        val currentDateTime = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                        val file = File(context.getExternalFilesDir(null), "m$currentDateTime$utteranceId")
                        filePathList.add(count, file.path)
                        textToSpeech.setOnUtteranceProgressListener(this)
                        textToSpeech.synthesizeToFile(mp3List[count], null, file, count.toString())
                    }
                } else if ((utteranceId != null) && (utteranceId.toInt() >= (totalCount - 1))){
                    handler.post {
                        exoPlayer.addMediaItem(MediaItem.fromUri(Uri.fromFile(File(filePathList[utteranceId.toInt()]))))
                    }
                    textToSpeech.stop()
                    textToSpeech.shutdown()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                logWarn("语音合成出错")
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                logWarn("语音合成中止")
            }
        }

        val currentDateTime = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val file = File(context.getExternalFilesDir(null), "m$currentDateTime")
        filePathList[0] = file.path.toString()
        textToSpeech.setOnUtteranceProgressListener(ttsListener)
        textToSpeech.synthesizeToFile(mp3List[0], null, file, "0")

    }

    /**
     * 设置朗读文本
     */
    fun setText(str: String) {
        if (!isPrepared && !exoPlayer.isPlaying) {
            this.ttsContent = str
            splitText()
        }
    }

    /**
     * 从URI地址读取文本
     */
    fun setTextFromUri(uri: Uri) {
        if (!isPrepared && !exoPlayer.isPlaying) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val stringBuilder = StringBuilder()

            inputStream?.use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                var line: String? = reader.readLine()

                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
            val str = stringBuilder.toString()

            this.ttsContent = str
            splitText()
        }
    }

    /**
     * 分割文本
     */
    private fun splitText( ) {
        val result = mutableListOf<String>()
        var startIndex = 0
        while (startIndex < this.ttsContent.length) {
            val endIndex = startIndex + splitLength.coerceAtMost(this.ttsContent.length - startIndex)
            val segment = this.ttsContent.substring(startIndex, endIndex)
            result.add(segment)
            startIndex = endIndex
        }
        this.mp3List = result
    }

    /**
     * 设置分割文本时的最大间隔
     */
    fun setSplitLength(length: Int) {
        this.splitLength = length
    }

    /**
     * 设置音调
     */
    fun setPitch(pitch: Float) {
        if (pitch > 2.0f) {
            logWarn("最大音调为2.0f")
        } else if (pitch < 0f) {
            logWarn("最小音调为0f")
        } else {
            this.pitch = pitch
            exoPlayer.playbackParameters = PlaybackParameters(this.speed, this.pitch)
        }
    }

    /**
     * 设置语速
     */
    fun setSpeed(speed: Float) {
        if (speed > 2.0f) {
            logWarn("最大语速为2.0f")
        }else if (speed < 0f) {
            logWarn("最小语速为0f")
        } else {
            this.speed = speed
            exoPlayer.playbackParameters = PlaybackParameters(this.speed, this.pitch)
        }
    }


    /**
     * 设置Voice
     */
//    fun setVoice() {
//
//    }

    /**
     * 暂停
     */
    fun pause() {
        if (isPrepared && exoPlayer.isPlaying) {
            exoPlayer.pause()
            playbackPosition = exoPlayer.currentPosition
        }
    }

    /**
     * 结束播放
     */
    fun stop() {
        exoPlayer.stop()
        isPrepared = false
        playbackPosition = 0
    }

    /**
     * 继续播放
     */
    fun resume() {
        if (isPrepared) {
            exoPlayer.seekTo(playbackPosition)
            exoPlayer.play()
        }
    }

}