/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.playground.volley;

/**
 * Base class for all network requests.
 *
 * @param <T> The type of parsed response this request expects.
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * Callback to notify when the network request returns.
     */
    /* package */ interface NetworkRequestCompleteListener {

        /** Callback when a network response has been received. */
        void onResponseReceived(Request<?> request, Response<?> response);

        /** Callback when request returns from network without valid response. */
        void onNoUsableResponseReceived(Request<?> request);
    }

    /** An event log tracing the lifetime of this request; for debugging. */
    private final VolleyLog.MarkerLog mEventLog = VolleyLog.MarkerLog.ENABLED ? new VolleyLog.MarkerLog() : null;

    /**
     * Request method of this request.  Currently supports GET, POST, PUT, DELETE, HEAD, OPTIONS,
     * TRACE, and PATCH.
     */
//    private final int mMethod;
//
//    /** URL of this request. */
//    private final String mUrl;
//
//    /** Default tag for {@link TrafficStats}. */
//    private final int mDefaultTrafficStatsTag;

    /** Lock to guard state which can be mutated after a request is added to the queue. */
    private final Object mLock = new Object();

    /** Listener interface for errors. */
    // @GuardedBy("mLock")
    private Response.ErrorListener mErrorListener;

    /** Sequence number of this request, used to enforce FIFO ordering. */
    private Integer mSequence;

    /** The request queue this request is associated with. */
    private RequestQueue mRequestQueue;

    /** Whether or not responses to this request should be cached. */
    private boolean mShouldCache = true;

    /** Whether or not this request has been canceled. */
    // @GuardedBy("mLock")
    private boolean mCanceled = false;

    /** Whether or not a response has been delivered for this request yet. */
    // @GuardedBy("mLock")
    private boolean mResponseDelivered = false;

    /** Whether the request should be retried in the event of an HTTP 5xx (server) error. */
    private boolean mShouldRetryServerErrors = false;

//    /** The retry policy for this request. */
//    private RetryPolicy mRetryPolicy;

    /**
     * When a request can be retrieved from cache but must be refreshed from
     * the network, the cache entry will be stored here so that in the event of
     * a "Not Modified" response, we can be sure it hasn't been evicted from cache.
     */
    private Cache.Entry mCacheEntry = null;

    /** An opaque token tagging this request; used for bulk cancellation. */
    private Object mTag;

    /** Listener that will be notified when a response has been delivered. */
    // @GuardedBy("mLock")
    private NetworkRequestCompleteListener mRequestCompleteListener;

    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    abstract protected void deliverResponse(T response);

    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    public void addMarker(String tag) {
        if (VolleyLog.MarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getId());
        }
    }

    public final Request<?> setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    public final boolean shouldCache() {
        return mShouldCache;
    }
}
