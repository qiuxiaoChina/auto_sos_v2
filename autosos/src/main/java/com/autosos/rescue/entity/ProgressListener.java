package com.autosos.rescue.entity;

public interface ProgressListener {
    void transferred(int transferedBytes);

    void setContentLength(long contentLength);
}
