//
// Created by Administrator on 2022/3/27.
//

#ifndef AUDIOTRACKPLAY_IDECODER_H
#define AUDIOTRACKPLAY_IDECODER_H

//定义解码器基础功能
class IDecoder {
public:
    virtual void GoOn() = 0;
    virtual void Pause() = 0;
    virtual void Stop() = 0;
    virtual void IsRunning() = 0;
};


#endif //AUDIOTRACKPLAY_IDECODER_H
