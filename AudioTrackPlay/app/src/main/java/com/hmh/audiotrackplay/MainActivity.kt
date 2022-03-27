package com.hmh.audiotrackplay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hmh.audiotrackplay.audioTrack.AudioTracker
import com.hmh.audiotrackplay.databinding.ActivityMainBinding
import com.hmh.audiotrackplay.ffmpegPlayer.PlayerActivity
import com.hmh.audiotrackplay.hook.AcitvityHook
import com.hmh.audiotrackplay.hook.HookActivityHelper
import com.hmh.audiotrackplay.record.AudioRecorder
import com.taobao.android.dexposed.DexposedBridge
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var rootView: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(rootView.root)
        val permission = RxPermissions(this)
        HookActivityHelper.init(this)
//        val path = Environment.getExternalStorageDirectory().absolutePath
        permission.request(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        ).subscribe { open ->
            run {
                if (open) {
                    Log.e("++++", "权限一打开")
                } else {
                    Log.e("++++++", "权限未打开")
                }
            }

        }

        val at = AudioTracker()
        rootView.playAudio.setOnClickListener {
            //audioTrack播放音频
            at.createAudioTrack(cacheDir.path + "/" + "my_test.pcm")
            at.start()
        }
        rootView.stopAudio.setOnClickListener {
            //audioTrack停止播放音频
            at.stop()
        }

        rootView.nativePlay.setOnClickListener {
            //openSL播放音频
//            var downPath = Environment.getDownloadCacheDirectory().absolutePath
            val file = File("${cacheDir.path}/_test.pcm")
            if (file.exists()) {
                Log.e("++++", "文件存在")
            } else {
                Log.e("+++++", "文件不存在")
            }

            nativeStartMusic( "${cacheDir.path}/my_test.pcm")
        }

        rootView.nativeStop.setOnClickListener {
            //opensl停止播放音频
            nativeStopMusic()
        }

        //opensl录音
        rootView.audioRecord.setOnClickListener {
            nativeRecordStart( "${cacheDir.path}/my_test.pcm")
        }

        //opensl停止录音
        rootView.audioOpensl.setOnClickListener {
            nativeRecordStop()
        }

        val record = AudioRecorder.getInstance()
        rootView.recordStart.setOnClickListener {
            //audioRecord录音
            if (record.status == AudioRecorder.Status.STATUS_NO_READY) {
                record.createDefaultAudio("my_test", this)
                record.startRecord(null, "${cacheDir.path}/my_test.pcm")
            }
        }

        rootView.recordStop.setOnClickListener {
            //audioRecord停止录音
            record.stopRecord()
        }
        rootView.entryFfmpegInfo.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        rootView.startActivity.setOnClickListener {

//            Field mInstumentation = Activity.class.getDeclaredField("mInstrumentation");
//            mInstumentation.setAccessible(true);
////            mInstumentation.get
////            ByteHook.
//            Instrumentation instrumentation = (Instrumentation) mInstumentation.get(activity);
//
//            ProxyInstrumentation proxyInstrumentation = new ProxyInstrumentation(instrumentation);
//            mInstumentation.set(activity, proxyInstrumentation);
//            Instrumentation instrumentation = (Instrumentation) RefInvoke.getFieldObject(Activity.class,activity,"mInstrumentation");
//            ProxyInstrumentation instrumentation1 = new ProxyInstrumentation(instrumentation);
//            RefInvoke.setFieldObject(Activity.class,activity,"mInstrumentation",instrumentation1);
            DexposedBridge.findAndHookMethod(
                Activity::class.java,
                "execStartActivity",
                AcitvityHook()
            )
        }

    }

    private external fun stringFromJNI(): String
    private external fun nativeStartMusic(pcmPath: String)
    private external fun nativeStopMusic()
    private external fun nativeRecordStart(savePath: String): Boolean
    private external fun nativeRecordStop()
//    private external fun getFFmpegInfo(): String

    companion object {
        init {
            System.loadLibrary("audiotrackplay")
        }
    }
}