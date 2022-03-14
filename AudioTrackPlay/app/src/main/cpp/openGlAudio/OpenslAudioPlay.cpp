//
// Created by hmh on 2022/3/13.
//

#include "OpenslAudioPlay.h"
#include <pthread.h>
#include <android/log.h>
#include <jni.h>
#include <string.h>
#include "logUtils.h"


//#define LOGE()



void playerCallback(SLAndroidSimpleBufferQueueItf bq, void *context);

//播放音频
OpenslAudioPlay::OpenslAudioPlay(int sampleRate, int sampleFormat, int channels)
        : audioEngine(new AudioEngine()), mPlayObj(nullptr), mPlayer(nullptr),
          mBufferQueue(nullptr),
          mEffectSend(nullptr), mVolume(nullptr), mSampleRate((SLmilliHertz) sampleRate * 1000),
          mSampleFormat(sampleFormat), mChannels(channels), mBufSize(0), mIndex(0) {
    LOGE("初始化88888888");
    mMutex = PTHREAD_COND_INITIALIZER;
    mBuffers[0] = nullptr;
    mBuffers[1] = nullptr;
}

OpenslAudioPlay::~OpenslAudioPlay() {}

bool OpenslAudioPlay::init() {
    SLresult result;
    //创建播放器
//    SLDataLocator_AndroidBufferQueue  locBufq = {SL_DATALOCATOR_ANDROIDBUFFERQUEUE, 2};
    SLDataLocator_AndroidSimpleBufferQueue locBufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    /*
     * pcm数据格式
     * SL_DATAFORMAT_PCM: 数据格式为pcm格式
     * 2、双声道
     * SL_SAMPLINGRATE_44_1: 采样率为44100
     * SL_PCMSAMPLEFORMAT_FIXED_16: 采样格式
     * SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT:双声道，立体效果
     * SL_BYTEORDER_LITTLEENDIAN:小端模式
     * */

    SLDataFormat_PCM formatPcm = {
            SL_DATAFORMAT_PCM, (SLuint32) mChannels, mSampleRate, (SLuint32) mSampleFormat,
            (SLuint32) mSampleFormat, mChannels == 2 ? 0 : SL_SPEAKER_FRONT_CENTER,
            SL_BYTEORDER_LITTLEENDIAN
    };
    if (mSampleRate) {
        formatPcm.samplesPerSec = mSampleRate;
    }

    //数据源
    SLDataSource audioSrc = {&locBufq, &formatPcm};

    //配置音轨
    SLDataLocator_OutputMix locatorOutputMix = {
            SL_DATALOCATOR_OUTPUTMIX, audioEngine->outputMixObj
    };
    SLDataSink audioSink = {&locatorOutputMix, nullptr};
    LOGE("初始化中2222222。。。。。");
    //操作队列的接口
    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_EFFECTSEND};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

    //创建播放器
    result = (*audioEngine->engine)->CreateAudioPlayer(audioEngine->engine, &mPlayObj, &audioSrc,
                                                       &audioSink, mSampleRate ? 2 : 3, ids, req);

    if (result != SL_RESULT_SUCCESS) {
        LOGE("创建播放器失败", result);
        return false;
    }
    //初始化播放器
    result = (*mPlayObj)->Realize(mPlayObj, SL_BOOLEAN_FALSE);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("初始化播放器失败%d", result);
        return false;
    }

    //获取播放器接口
    result = (*mPlayObj)->GetInterface(mPlayObj, SL_IID_PLAY, &mPlayer);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("获取接口失败%d", result);
        return false;
    }

    //第四部：设置播放回调函数
    result = (*mPlayObj)->GetInterface(mPlayObj, SL_IID_BUFFERQUEUE, &mBufferQueue);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("接口回调失败: %d", result);
        return false;
    }
    //设置回调
    result = (*mBufferQueue)->RegisterCallback(mBufferQueue, playerCallback, this);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("回调失败: %d", result);
        return false;
    }
    mEffectSend = nullptr;

    if (mSampleRate == 0) {
        result = (*mPlayObj)->GetInterface(mPlayObj, SL_IID_EFFECTSEND, &mEffectSend);
        if (result != SL_RESULT_SUCCESS) {
            LOGE("接口effectsend失败：%d", result);
            return false;
        }
    }

    result = (*mPlayObj)->GetInterface(mPlayObj, SL_IID_VOLUME, &mVolume);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("接口volume失败：%d", result);
        return false;
    }
    //设置播放器播放状态为正在播放
    result = (*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_PLAYING);
    if (result != SL_RESULT_SUCCESS) {
        LOGE("接口播放状态失败：%d", result);
        return false;
    }

    return true;
}

//一帧音频播放完毕后回回调这个函数
void playerCallback(SLAndroidSimpleBufferQueueItf qb, void *context) {
    OpenslAudioPlay *player = (OpenslAudioPlay *) context;
    pthread_mutex_unlock(&player->mMutex);
}

//播放队列
void OpenslAudioPlay::enqueueSample(void *data, size_t length) {
    //必须等待一帧音频播放完毕后才可以enqueue第二帧音频

    pthread_mutex_lock(&mMutex);
    if (mBufSize < length) {
        mBufSize = length;
        if (mBuffers[0]) {
            delete[] mBuffers[0];
        }
        if (mBuffers[1]) {
            delete[] mBuffers[1];
        }
        mBuffers[0] = new uint8_t[mBufSize];
        mBuffers[1] = new uint8_t[mBufSize];
    }

    memcpy(mBuffers[mIndex], data, length);
    LOGE("卧槽手动调用");
    //手动激活回调函数

    (*mBufferQueue)->Enqueue(mBufferQueue, mBuffers[mIndex], length);
    LOGE("这里控制在");
    mIndex = 1 - mIndex;
}

void OpenslAudioPlay::release() {
    LOGE("释放了relse");
    pthread_mutex_lock(&mMutex);
    if (mPlayObj) {
        (*mPlayObj)->Destroy(mPlayObj);
        mPlayObj = nullptr;
        mPlayer = nullptr;
        mBufferQueue = nullptr;
        mEffectSend = nullptr;
        mVolume = nullptr;
    }
    if(audioEngine){
        delete audioEngine;
        audioEngine = nullptr;
    }
    if (mBuffers[0]) {
        delete[] mBuffers[0];
        mBuffers[0] = nullptr;
    }
    if (mBuffers[1]) {
        delete[] mBuffers[1];
        mBuffers[1] = nullptr;
    }
    pthread_mutex_unlock(&mMutex);
    pthread_mutex_destroy(&mMutex);

}










