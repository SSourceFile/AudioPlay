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
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var rootView: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(rootView.root)

        var at = AudioTracker(this);
        rootView.playAudio.setOnClickListener {
            at.createAudioTrack("暂时用了raw的方式播放")
            at.start()
        }
        rootView.stopAudio.setOnClickListener {
            at.stop()
        }
        rootView.playShow.text = stringFromJNI()

        rootView.nativePlay.setOnClickListener {

            var path = Environment.getExternalStorageDirectory().absolutePath
            var downPath = Environment.getDownloadCacheDirectory().absolutePath

            Log.e("++++", "安排"+path+" // "+downPath)
            var file = File(path+"/"+"_test.pcm");
            if(file.exists()){
                Log.e("++++", "文件存在")
            }else{
                Log.e("+++++", "文件不存在")
            }
            nativeStartMusic(cacheDir.path+"/"+"_test.pcm");
        }

        rootView.nativeStop.setOnClickListener {
            nativeStopMusic()
        }

        //audioRecord采集音频
        rootView.audioRecord.setOnClickListener {

        }

        //audioOpenSL
        rootView.audioOpensl.setOnClickListener {

        }

    }
    external fun stringFromJNI(): String

    external fun nativeStartMusic(pcmPath: String)
    external fun nativeStopMusic()
    companion object{
        init {
            System.loadLibrary("audiotrackplay")
        }
    }
}