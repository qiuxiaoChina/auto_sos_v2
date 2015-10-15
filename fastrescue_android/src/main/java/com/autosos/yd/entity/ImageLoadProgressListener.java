package com.autosos.yd.entity;

public interface ImageLoadProgressListener {
    void transferred(int transferedBytes, String url);

    void setContentLength(long contentLength, String url);
}
