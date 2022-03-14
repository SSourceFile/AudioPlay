//
// Created by hmh on 2022/3/13.
//

#ifndef AUDIOTRACKPLAY_OPENSLAUDIOPLAY_H
#define AUDIOTRACKPLAY_OPENSLAUDIOPLAY_H


#include "audioengine.h"
#include <stdio.h>
#define SAMPLE_FORMAT_16 16

class OpenslAudioPlay {

private:
    AudioEngine *audioEngine;
    SLObjectItf mPlayObj;
    SLPlayItf mPlayer;
    SLAndroidSimpleBufferQueueItf mBufferQueue; //缓冲队列
    SLEffectSendItf mEffectSend;
    SLVolumeItf mVolume;  //声道
    SLmilliHertz mSampleRate;  //采样率
    int mSampleFormat;    //采样格式
    int mChannels;  //

    uint8_t *mBuffers[2];
    SLuint32 mBufSize;
    int mIndex;
    pthread_mutex_t mMutex;

public:
    OpenslAudioPlay(int sampleRate, int sampleFormat, int channels);
    bool init();

    void enqueueSample(void *data, size_t length);

    void release();

    ~OpenslAudioPlay();
    friend void playerCallback(SLAndroidSimpleBufferQueueItf qb, void *context);

};


#endif //AUDIOTRACKPLAY_OPENSLAUDIOPLAY_H
