package com.hmh.audiotrackplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import com.hmh.audiotrackplay.audioTrack.AudioTracker
import com.hmh.audiotrackplay.databinding.ActivityMainBinding
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
        var path = Environment.getExternalStorageDirectory().absolutePath
        permission.request(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        ).subscribe { open ->
            run {
                if(open){
                    Log.e("++++", "权限一打开")
                }else{
                    Log.e("++++++", "权限未打开")
                }
            }

        }
        var at = AudioTracker();
        rootView.playAudio.setOnClickListener {
            at.createAudioTrack(cacheDir.path + "/" + "my_test.pcm")
            at.start()
        }
        rootView.stopAudio.setOnClickListener {
            at.stop()
        }
        rootView.playShow.text = stringFromJNI()

        rootView.nativePlay.setOnClickListener {


            var downPath = Environment.getDownloadCacheDirectory().absolutePath
//            Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.e("++++", "安排" + path + " // " + downPath)
            var file = File(path + "/" + "_test.pcm");
            if (file.exists()) {
                Log.e("++++", "文件存在")
            } else {
                Log.e("+++++", "文件不存在")
            }
//            nativeStartMusic(cacheDir.path+"/"+"myhmh.pcm");
            nativeStartMusic(cacheDir.path + "/" + "my_test.pcm");
        }

        rootView.nativeStop.setOnClickListener {
            nativeStopMusic()
        }

        //audioRecord采集音频
        rootView.audioRecord.setOnClickListener {
            nativeRecordStart(cacheDir.path + "/" + "my_test.pcm")
        }

        //audioOpenSL
        rootView.audioOpensl.setOnClickListener {
            nativeRecordStop()
        }

        var record = AudioRecorder.getInstance();
        rootView.recordStart.setOnClickListener {
            if (record.status == AudioRecorder.Status.STATUS_NO_READY) {
                record.createDefaultAudio("my_test", this)
                record.startRecord(null, cacheDir.path+"/"+"my_test.pcm")
            }
        }

        rootView.recordStop.setOnClickListener {
            record.stopRecord()
        }

    }

    external fun stringFromJNI(): String

    external fun nativeStartMusic(pcmPath: String)
    external fun nativeStopMusic()
    external fun nativeRecordStart(savePath: String): Boolean
    external fun nativeRecordStop()


    companion object {
        init {
            System.loadLibrary("audiotrackplay")
        }
    }
}