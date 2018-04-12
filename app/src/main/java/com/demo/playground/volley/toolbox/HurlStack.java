package com.demo.playground.volley.toolbox;

import com.demo.playground.volley.AuthFailureError;
import com.demo.playground.volley.Request;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liyongan on 18/3/30.
 */

public class HurlStack implements HttpStack {

    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        return null;
    }
}
