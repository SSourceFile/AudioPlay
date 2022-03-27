//
// Created by Administrator on 2022/3/27.
//

#ifndef AUDIOTRACKPLAY_DECODESTATE_H
#define AUDIOTRACKPLAY_DECODESTATE_H
//定义解码状态
enum DecordState {
    STOP,      //停止
    PREPARE,   //准备
    START,      //开始
    ENCODING,   //解码中
    PAUSE,      //暂停
    FINISH      //完成
};

#endif //AUDIOTRACKPLAY_DECODESTATE_H
