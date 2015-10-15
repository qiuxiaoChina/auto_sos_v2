package com.autosos.yd.entity;

public interface ProgressListener {
    void transferred(int transferedBytes);

    void setContentLength(long contentLength);
}
