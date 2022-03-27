package com.hmh.audiotrackplay.ffmpegPlayer

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.hmh.audiotrackplay.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityPlayerBinding.inflate(layoutInflater);
        setContentView(bind.root)

        bind.ffmpegInfo.text = getFFmpegInfo().toString()
    }


    private external fun getFFmpegInfo(): String

    companion object {
        init {
            System.loadLibrary("audiotrackplay")
        }
    }

}