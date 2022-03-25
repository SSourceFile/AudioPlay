
cmake_minimum_required(VERSION 3.18.1)

project("audiotrackplay")

set(OpenSL ${CMAKE_SOURCE_DIR}/openGlAudio)

add_library( # Sets the name of the library.
        audiotrackplay

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        openGlAudio/audioengine.h
        openGlAudio/OpenslAudioPlay.cpp
        logUtils.h
        openSLRecord/OpenRecordPlay.cpp
        )


find_library( # Sets the name of the path variable.
        log-lib

        log)

target_link_libraries( # Specifies the target library.
        audiotrackplay

        # Links the target library to the log library
        # included in the NDK.
        #添加音频渲染
        OpenSLES
        #添加android本地库
        android
        ${log-lib})