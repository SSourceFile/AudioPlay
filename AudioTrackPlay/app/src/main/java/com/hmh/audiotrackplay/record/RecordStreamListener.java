package com.hmh.audiotrackplay.record;

public interface RecordStreamListener {

    void recordOfByte(byte[] data, int begin, int end);
}
