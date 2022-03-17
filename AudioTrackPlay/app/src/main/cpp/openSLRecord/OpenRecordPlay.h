//
// Created by hmh on 2022/3/17.
//

#ifndef AUDIOTRACKPLAY_OPENRECORDPLAY_H
#define AUDIOTRACKPLAY_OPENRECORDPLAY_H
//#include <SLES/>
#include <stdio.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

class OpenRecordPlay {

private:
    int mIndex;
    short *mRecordBuffs[2];
    unsigned mRecordBufferSize;

    //状态
    bool mIsRecording;

    FILE *mFile;
    SLObjectItf                   mEngineObj;
    SLEngineItf                   mEngineInterface;
    SLObjectItf                   mRecorderObj;
    SLRecordItf                   mRecorderInterface;
    SLAndroidSimpleBufferQueueItf mBufferQueue;
public:
    OpenRecordPlay(const char *fileSavePath);

    /** Call this method to start audio recording */
    bool start();

    /** Call this method to stop audio recording */
    void stop();

    ~OpenRecordPlay();

private:

    bool initEngine();

    /** Call this method to initialize an audio recorder */
    bool initRecorder();

    /** Call this method to release the resources related to recording */
    void release();

    /** This method is called every time an audio frame is recorded*/
    static void recorderCallback(SLAndroidSimpleBufferQueueItf bufferQueue, void *pContext);

};



#endif //AUDIOTRACKPLAY_OPENRECORDPLAY_H
