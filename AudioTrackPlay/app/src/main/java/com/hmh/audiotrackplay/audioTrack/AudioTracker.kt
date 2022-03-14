package com.hmh.audiotrackplay.audioTrack

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.hmh.audiotrackplay.R
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.util.concurrent.Executors

/**
 * audioTrack播放pcm音频
 * */
class AudioTracker : IPlay {

    private val TAG: String = "++++++"

    //采样率 44100HZ 支持所有设备
    private val SAMPLE_RATE = 44100

    //单声道，支持所有设备
    private val CHANNEL = AudioFormat.CHANNEL_OUT_MONO

    //位深  16位，支持所有设备
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    //缓冲区大小
    private var mBufferSizeInBytes: Int = 0

    //播放对象
    private var mAudioTrack: AudioTrack? = null

    //文件名字
    private var mFileName: String? = ""

    //状态
    private var mStatus = Status.STATUS_NO_READY

    //任务线程池
    private val mExecutorService = Executors.newSingleThreadExecutor()

    private var mContext: Context? = null

    constructor(context: Context?) {
        this.mContext = context
    }

    public fun createAudioTrack(filePath: String) {
        mFileName = filePath
        try {
            if (TextUtils.isEmpty(filePath)) {
                throw IllegalArgumentException("文件名不能为空")
            }

            //获取最小的缓存字节
            mBufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)

            if (mBufferSizeInBytes < 0) {
                throw java.lang.IllegalArgumentException("AudioTrack is not available")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAudioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC).build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setChannelMask(CHANNEL)
                            .setSampleRate(SAMPLE_RATE)
                            .build()
                    ).setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mBufferSizeInBytes)
                    .build()
            } else {
                mAudioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    CHANNEL,
                    AUDIO_FORMAT,
                    mBufferSizeInBytes,
                    AudioTrack.MODE_STREAM
                )
            }
            //进入预备状态
            mStatus = Status.STATUS_READY

        } catch (e: Exception) {
            e.printStackTrace()
            mStatus = Status.STATUS_NO_READY
        }
    }

    /**
     * 开始播放
     * */
    override fun start() {
        if (mStatus == Status.STATUS_NO_READY || mAudioTrack == null) {
            throw java.lang.IllegalArgumentException("播放器尚未初始化")
        }
        if (mStatus == Status.STATUS_START) {
            throw java.lang.IllegalArgumentException("正在播放音频")
        }
        mExecutorService.execute {
            try {
                audioTrackData()
            } catch (e: Exception) {

            }
        }
        mStatus = Status.STATUS_START
    }

    private fun audioTrackData() {
        var inputStream = mContext?.resources?.openRawResource(R.raw._test);
        var dis: DataInputStream? = null
        try {
            Log.e(TAG, "正在播放音频")
            dis = DataInputStream(BufferedInputStream(inputStream));
            val buffer = ByteArray(mBufferSizeInBytes)
//            var buffer = byteArrayOf()
            if(null != mAudioTrack && mAudioTrack?.state != AudioTrack.STATE_UNINITIALIZED
                && mAudioTrack?.playState != AudioTrack.PLAYSTATE_PLAYING){
                mAudioTrack?.play()
            }
            while (dis.read(buffer) != -1 && mStatus == Status.STATUS_START){
                mAudioTrack?.write(buffer, 0, dis.read(buffer))
            }

            Log.e(TAG, "播放结束了")

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            dis?.close()
        }
    }

    /**
     * 停止播放
     * */
    override fun stop() {

        if(mStatus == Status.STATUS_NO_READY || mStatus == Status.STATUS_READY){
            //播放器未开始
        }else{
            mAudioTrack?.stop()
            mStatus = Status.STATUS_STOP
            release()
        }
    }

    /**
     * 释放资源
     * */
    override fun release() {
        if (mAudioTrack != null) {
            mAudioTrack?.release()
            mAudioTrack = null
        }
    }
}