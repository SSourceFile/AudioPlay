//
// Created by hmh on 2022/3/13.
//


#ifndef AUDIOTRACKPLAY_AUDIOENGINE_H
#define AUDIOTRACKPLAY_AUDIOENGINE_H
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <android/log.h>
#include <stdio.h>
#include <assert.h>
#include "../logUtils.h"



class AudioEngine {
public:
    SLObjectItf engineObj;
    SLEngineItf engine;
    SLObjectItf outputMixObj;

private:
    //创建引擎并获取引擎接口
    void createEngine() {
//        SLObjectItf engineObj;
        //音频的播放，设计到openslEs
        //1、创建引擎对象
        SLresult result = slCreateEngine(&engineObj, 0, NULL, 0, NULL, NULL);

        if (SL_RESULT_SUCCESS != result) {
            LOGE("引擎创建失败：%d", result);
            return;
        }

        //2初始化引擎
        result = (*engineObj)->Realize(engineObj, SL_BOOLEAN_FALSE);
        if (SL_BOOLEAN_FALSE != result) {
            LOGE("引擎初始化失败：%d", result);
            return;
        }

        //获取引擎接口
        result = (*engineObj)->GetInterface(engineObj, SL_IID_ENGINE, &engine);

        if (SL_RESULT_SUCCESS != result) {
            LOGE("获取引擎接口失败：%d", result);
            return;
        }

        result = (*engine)->CreateOutputMix(engine, &outputMixObj, 0, 0, 0);
        if(result != SL_RESULT_SUCCESS){
            LOGE("创建混音失败：%d", result);
            return;
        }
        //设置混音
        result = (*outputMixObj)->Realize(outputMixObj, SL_BOOLEAN_FALSE);
        if (result != SL_BOOLEAN_FALSE) {
            LOGE("初始化混音失败：%d", result);
            return;
        }
    }

    virtual void release() {
        if (outputMixObj) {
            (*outputMixObj)->Destroy(outputMixObj);
            outputMixObj = nullptr;
        }
        if (engineObj) {
            (*engineObj)->Destroy(engineObj);
            engineObj = nullptr;
            engine = nullptr;
        }
    }

public:
    AudioEngine() : engineObj(nullptr), engine(nullptr), outputMixObj(nullptr) {
        createEngine();
    }

    virtual ~AudioEngine() {
        release();
    }
};
#endif //AUDIOTRACKPLAY_AUDIOENGINE_H
