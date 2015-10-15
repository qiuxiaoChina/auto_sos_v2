package com.autosos.yd.task;

import org.apache.http.client.methods.HttpPost;

public class HttpPatch extends HttpPost {

    public HttpPatch(String url) {
        super(url);
    }

    @Override
    public String getMethod() {
        return "PATCH";
    }
}