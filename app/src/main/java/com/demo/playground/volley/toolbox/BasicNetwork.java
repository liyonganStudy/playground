package com.demo.playground.volley.toolbox;

import com.demo.playground.volley.Network;
import com.demo.playground.volley.NetworkResponse;
import com.demo.playground.volley.Request;
import com.demo.playground.volley.VolleyError;

/**
 * Created by liyongan on 18/3/30.
 */

public class BasicNetwork implements Network {
    private static final int DEFAULT_POOL_SIZE = 4096;

    public BasicNetwork(HttpStack httpStack) {
        // If a pool isn't passed in, then build a small default pool that will give us a lot of
        // benefit and not use too much memory.
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
//        mHttpStack = httpStack;
//        mBaseHttpStack = new AdaptedHttpStack(httpStack);
//        mPool = pool;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        return null;
    }
}
