package com.hmh.audiotrackplay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import com.hmh.audiotrackplay.audioTrack.AudioTracker
import com.hmh.audiotrackplay.databinding.ActivityMainBinding
import com.hmh.audiotrackplay.hook.HookActivityHelper
import com.hmh.audiotrackplay.hook.HookOneActivity
import com.hmh.audiotrackplay.record.AudioRecorder
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.File
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var rootView: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(rootView.root)
        var permission = RxPermissions(this)
        HookActivityHelper.init(this)
        var path = Environment.getExternalStorageDirectory().absolutePath
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
        var at = AudioTracker();
        var map= HashMap<String, String>()
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
            var downPath = Environment.getDownloadCacheDirectory().absolutePath
            var file = File(path + "/" + "_test.pcm");
            if (file.exists()) {
                Log.e("++++", "文件存在")
            } else {
                Log.e("+++++", "文件不存在")
            }

            nativeStartMusic(cacheDir.path + "/" + "my_test.pcm");
        }

        rootView.nativeStop.setOnClickListener {
            //opensl停止播放音频
            nativeStopMusic()
        }

        //opensl录音
        rootView.audioRecord.setOnClickListener {
            nativeRecordStart(cacheDir.path + "/" + "my_test.pcm")
        }

        //opensl停止录音
        rootView.audioOpensl.setOnClickListener {
            nativeRecordStop()
        }

        var record = AudioRecorder.getInstance();
        rootView.recordStart.setOnClickListener {
            //audioRecord录音
            if (record.status == AudioRecorder.Status.STATUS_NO_READY) {
                record.createDefaultAudio("my_test", this)
                record.startRecord(null, cacheDir.path + "/" + "my_test.pcm")
            }
        }

        rootView.recordStop.setOnClickListener {
            //audioRecord停止录音
            record.stopRecord()
        }
        rootView.startActivity.setOnClickListener {
            var inten = Intent(this, HookOneActivity::class.java)
            startActivity(inten)
        }

    }

    private external fun stringFromJNI(): String
    private external fun nativeStartMusic(pcmPath: String)
    private external fun nativeStopMusic()
    private external fun nativeRecordStart(savePath: String): Boolean
    private external fun nativeRecordStop()

    companion object {
        init {
            System.loadLibrary("audiotrackplay")
        }
    }
}