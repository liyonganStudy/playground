package com.demo.playground.volley.toolbox;

import android.content.Context;

import com.demo.playground.volley.Network;
import com.demo.playground.volley.RequestQueue;

import java.io.File;

/**
 * Created by liyongan on 18/3/30.
 */

public class Volley {
    private static final String DEFAULT_CACHE_DIR = "volley";

    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        BasicNetwork network;
        if (stack == null) {
            network = new BasicNetwork(new HurlStack());
        } else {
            network = new BasicNetwork(stack);
        }
        return newRequestQueue(context, network);
    }

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (HttpStack) null);
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }
}
