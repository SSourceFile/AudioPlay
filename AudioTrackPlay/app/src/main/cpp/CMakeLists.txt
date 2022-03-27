
cmake_minimum_required(VERSION 3.18.1)

project("audiotrackplay")
# 支持gnu++11
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
set(OpenSL ${CMAKE_SOURCE_DIR}/openGlAudio)
message("卧槽hello world")
message("人才1: ${CMAKE_SOURCE_DIR}")
message("人才2:${CMAKE_ANDROID_ARCH_ABI}")

#找到包含所有的cpp文件
file(GLOB allCpp *.cpp)
#自定义ffmpeg目录
set(ffmpeg_lib_dir ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI})
set(ffmpeg_head_dir ${CMAKE_SOURCE_DIR}/ffmpeg)
message("最后的${ffmpeg_lib_dir}")

#添加头文件
include_directories(${ffmpeg_head_dir}/include)
#
add_library( avutil
        SHARED
        IMPORTED )
set_target_properties( avutil
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavutil.so )
#
add_library(
        swscale
        SHARED
        IMPORTED
)
set_target_properties(
        swscale
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswscale.so
)
add_library(
        format
        SHARED
        IMPORTED
)
set_target_properties(
        format
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavformat.so
)
add_library(
        avdevice
        SHARED
        IMPORTED
)
set_target_properties(
        avdevice
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libpostproc.so
)
add_library(
        avcodec
        SHARED
        IMPORTED
)
set_target_properties(
        avcodec
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavcodec.so
)
add_library(
        avfilter
        SHARED
        IMPORTED
)
set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavfilter.so
)
add_library(
        resample
        SHARED
        IMPORTED
)
set_target_properties(
        resample
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswresample.so
)

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
        avutil
        swscale
        format
        avcodec
        avfilter
        avdevice
        resample
        #添加android本地库
        android
        ${log-lib})