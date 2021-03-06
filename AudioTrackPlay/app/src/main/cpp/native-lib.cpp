//
// Created by hmh on 2022/3/13.
//
#include <jni.h>
#include <string>
#include "openGlAudio/OpenslAudioPlay.h"
#include <pthread.h>
#include "logUtils.h"
#include "openSLRecord/OpenRecordPlay.h"



extern "C" JNIEXPORT jstring JNICALL
Java_com_hmh_audiotrackplay_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


/*
 * 播放pcm流媒体
 * */
FILE *pcmFile = 0;

OpenslAudioPlay *slAudioPlay = nullptr;
//是否正在播放
bool isPlaying = false;

void *playThreadFunc(void *arg);

void *playThreadFunc(void *arg){
    const int bufferSize = 2048;
    LOGE("到这里了");
    short buffer[bufferSize];
    if(pcmFile)
    while (isPlaying && !feof(pcmFile)){
        LOGE("到这里了11");
        fread(buffer, 1, bufferSize, pcmFile);
        slAudioPlay->enqueueSample(buffer, bufferSize);
    }
    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_hmh_audiotrackplay_MainActivity_nativeStartMusic(JNIEnv *env, jobject thiz, jstring pcmPath) {
    //将java传递过来的String转为C中的char*
    const char *_pcmPath = env->GetStringUTFChars(pcmPath, NULL);
    LOGE("开始了音乐播放%s", pcmPath);
    //如果已经实例化就释放资源
    if(slAudioPlay){
        LOGE("222222222");
        slAudioPlay->release();
        delete slAudioPlay;
        slAudioPlay = nullptr;
    }
    //实例化play
    LOGE("33333333");
    slAudioPlay = new OpenslAudioPlay(44100, SAMPLE_FORMAT_16, 1);
    //初始化
    slAudioPlay->init();
    //读取的方式打开pcm文件
    pcmFile = fopen(_pcmPath, "r");
    //修改播放状态
    isPlaying = true;
    //开启子线程进行播放音频
    LOGE("开启现线程");
    pthread_t playThread;
    pthread_create(&playThread, nullptr, playThreadFunc, 0);

    env->ReleaseStringUTFChars(pcmPath, _pcmPath);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_hmh_audiotrackplay_MainActivity_nativeStopMusic(JNIEnv *env, jobject thiz) {
    isPlaying = false;
    //停止播放
    if(slAudioPlay){
        //如果资源已经加载，直接释放
        slAudioPlay->release();
        delete slAudioPlay;
        slAudioPlay = nullptr;
    }
    if(pcmFile){
        fclose(pcmFile);
        pcmFile = nullptr;
    }
}
OpenRecordPlay *audioRecorder = nullptr;
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_hmh_audiotrackplay_MainActivity_nativeRecordStart(JNIEnv *env, jobject thiz,
                                                           jstring save_path) {
    const char *recordFileSavePath = env->GetStringUTFChars(save_path, nullptr);
    if (!audioRecorder) {
        audioRecorder = new OpenRecordPlay(recordFileSavePath);
    }
//
    env->ReleaseStringUTFChars(save_path, recordFileSavePath);
    audioRecorder->start();
    return true;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_hmh_audiotrackplay_MainActivity_nativeRecordStop(JNIEnv *env, jobject thiz) {
    if (audioRecorder) {
        audioRecorder->stop();
        delete audioRecorder;
        audioRecorder = nullptr;
    }
}

extern "C"{
#include <libavcodec/version.h>
#include <libavcodec/avcodec.h>
#include <libavformat/version.h>
#include <libavutil/version.h>
#include <libavfilter/version.h>
#include <libswresample/version.h>
#include <libswscale/version.h>
#include <libavutil/avutil.h>
}
const char *getFFmpegVersion(){
    return av_version_info();
}


//获取ffmpeg成功案例
extern "C"
#include "../include/libavcodec/avcodec.h"


JNIEXPORT jstring JNICALL
Java_com_hmh_audiotrackplay_ffmpegPlayer_PlayerActivity_getFFmpegInfo(JNIEnv *env, jobject thiz) {

    char strBuffer[1024 * 4] = {0};
    strcat(strBuffer, "libavcodec : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVCODEC_VERSION));
    strcat(strBuffer, "\nlibavformat : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFORMAT_VERSION));
    strcat(strBuffer, "\nlibavutil : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVUTIL_VERSION));
    strcat(strBuffer, "\nlibavfilter : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFILTER_VERSION));
    strcat(strBuffer, "\nlibswresample : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWRESAMPLE_VERSION));
    strcat(strBuffer, "\nlibswscale : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWSCALE_VERSION));
    strcat(strBuffer, "\navcodec_configure : \n");
    strcat(strBuffer, avcodec_configuration());
    strcat(strBuffer, "\navcodec_license : ");
    strcat(strBuffer, avcodec_license());
    LOGE("GetFFmpegVersion\n%s", strBuffer);
    return env->NewStringUTF(strBuffer);
}