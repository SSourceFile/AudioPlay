package com.hmh.audiotrackplay.audioTrack

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.Executors

/**
 * audioTrack播放pcm音频
 * */
class AudioTracker : IPlay {

        // 缓冲区字节大小
        private var mBufferSizeInBytes = 0

        // 播放对象
        private var mAudioTrack: AudioTrack? = null

        // 文件名
        private var mFilePath: String? = null

        // 状态
        @Volatile
        private var mStatus = Status.STATUS_NO_READY

        // 单任务线程池
        private val mExecutorService = Executors.newSingleThreadExecutor()
        private var mAudioPlayListener: AudioPlayListener? = null
        @Throws(IllegalStateException::class)
        fun createAudioTrack(filePath: String?) {
            mFilePath = filePath
            mBufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)
            check(mBufferSizeInBytes > 0) { "AudioTrack is not available $mBufferSizeInBytes" }
            mAudioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(CHANNEL)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mBufferSizeInBytes)
                    .build()
            } else {
                AudioTrack(
                    AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT,
                    mBufferSizeInBytes, AudioTrack.MODE_STREAM
                )
            }
            mStatus = Status.STATUS_READY
        }

        /**
         * 开始播放
         *
         * @throws IllegalStateException
         */
        @Throws(IllegalStateException::class)
        fun start() {
            check(!(mStatus == Status.STATUS_NO_READY || mAudioTrack == null)) { "播放器尚未初始化" }
            check(mStatus != Status.STATUS_START) { "正在播放..." }
            Log.d(TAG, "===start===")
            mStatus = Status.STATUS_START
            mExecutorService.execute {
                try {
                    playAudioData()
                } catch (e: IOException) {
                    Log.e(TAG, "playAudioData: ", e)
                    if (mAudioPlayListener != null) {
                        mAudioPlayListener!!.onError(e.message)
                    }
                }
            }
        }

        /**
         * 播放 PCM 音频
         *
         * @throws IOException
         */
        @Throws(IOException::class)
        private fun playAudioData() {
            try {
                BufferedInputStream(FileInputStream(mFilePath)).use { bis ->
                    if (mAudioPlayListener != null) {
                        mAudioPlayListener!!.onStart()
                    }
                    val bytes = ByteArray(mBufferSizeInBytes)
                    var length: Int = 0
                    mAudioTrack!!.play()
                    // write 是阻塞方法
                    while (mStatus == Status.STATUS_START && bis.read(bytes).also {
                            length = it
                        } != -1) {
                        mAudioTrack!!.write(bytes, 0, length)
                    }
                }
            } finally {
                mAudioTrack!!.stop()
                if (mAudioPlayListener != null) {
                    mAudioPlayListener!!.onStop()
                }
            }
        }

    override fun start(filePath: String) {
        TODO("Not yet implemented")
    }

    /**
         * 停止播放
         *
         * @throws IllegalStateException
         */
        @Throws(IllegalStateException::class)
        override fun stop() {
            Log.d(TAG, "===stop===")
            mStatus = if (mStatus == Status.STATUS_NO_READY || mStatus == Status.STATUS_READY) {
                throw IllegalStateException("播放尚未开始")
            } else {
                Status.STATUS_STOP
            }
        }

        /**
         * 释放资源
         */
        override fun release() {
            Log.d(TAG, "==release===")
            mStatus = Status.STATUS_NO_READY
            if (mAudioTrack != null) {
                mAudioTrack!!.release()
                mAudioTrack = null
            }
        }

        fun setAudioPlayListener(audioPlayListener: AudioPlayListener?) {
            mAudioPlayListener = audioPlayListener
        }

        /**
         * 播放对象的状态
         */
        enum class Status {
            //未开始
            STATUS_NO_READY,  //预备
            STATUS_READY,  //播放
            STATUS_START,  //停止
            STATUS_STOP
        }

        /**
         * invoked on work thread
         */
        interface AudioPlayListener {
            /**
             * 开始
             */
            fun onStart()

            /**
             * 结束
             */
            fun onStop()

            /**
             * 发生错误
             *
             * @param message
             */
            fun onError(message: String?)
        }

        companion object {
            private const val TAG = "AudioRecorder"

            // 采样率 44100Hz，所有设备都支持
            private const val SAMPLE_RATE = 44100

            // 单声道，所有设备都支持
            private const val CHANNEL = AudioFormat.CHANNEL_OUT_MONO

            // 位深 16 位，所有设备都支持
            private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        }

}