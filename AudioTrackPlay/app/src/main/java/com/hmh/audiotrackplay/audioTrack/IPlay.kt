package com.hmh.audiotrackplay.audioTrack

interface IPlay {

    //开始
    fun start(filePath:String)

    //停止
    fun stop()

    //释放
    fun release()
}