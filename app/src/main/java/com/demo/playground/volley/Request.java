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

import android.os.Handler;
import android.os.Looper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public abstract class Request<T> implements Comparable<Request<T>> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Supported request methods.
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /* package */ interface NetworkRequestCompleteListener {

        void onResponseReceived(Request<?> request, Response<?> response);

        void onNoUsableResponseReceived(Request<?> request);
    }

    private final VolleyLog.MarkerLog mEventLog = VolleyLog.MarkerLog.ENABLED ? new VolleyLog.MarkerLog() : null;

    private final int mMethod;
    private final String mUrl;
    private final Object mLock = new Object();

    // @GuardedBy("mLock")
    private Response.ErrorListener mErrorListener;
    private Integer mSequence;
    private RequestQueue mRequestQueue;
    private boolean mShouldCache = true;
    // @GuardedBy("mLock")
    private boolean mCanceled = false;
    // @GuardedBy("mLock")
    private boolean mResponseDelivered = false;
    private boolean mShouldRetryServerErrors = false;
    private RetryPolicy mRetryPolicy;
    private Cache.Entry mCacheEntry = null;
    /** An opaque token tagging this request; used for bulk cancellation. */
    private Object mTag;
    /** Listener that will be notified when a response has been delivered. */
    // @GuardedBy("mLock")
    private NetworkRequestCompleteListener mRequestCompleteListener;

    public Request(int method, String url, Response.ErrorListener listener) {
        mMethod = method;
        mUrl = url;
        mErrorListener = listener;
        setRetryPolicy(new DefaultRetryPolicy());
    }

    public int getMethod() {
        return mMethod;
    }

    public Request<?> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }

    public Response.ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }

    public void addMarker(String tag) {
        if (VolleyLog.MarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getId());
        }
    }

    void finish(final String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
        if (VolleyLog.MarkerLog.ENABLED) {
            final long threadId = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // If we finish marking off of the main thread, we need to
                // actually do it on the main thread to ensure correct ordering.
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mEventLog.add(tag, threadId);
                        mEventLog.finish(Request.this.toString());
                    }
                });
                return;
            }

            mEventLog.add(tag, threadId);
            mEventLog.finish(this.toString());
        }
    }

    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getCacheKey() {
        return getUrl();
    }

    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    // @CallSuper
    public void cancel() {
        synchronized (mLock) {
            mCanceled = true;
            mErrorListener = null;
        }
    }

    public boolean isCanceled() {
        synchronized (mLock) {
            return mCanceled;
        }
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return Collections.emptyMap();
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     *
     * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     */
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public final Request<?> setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    public final boolean shouldCache() {
        return mShouldCache;
    }

    public final Request<?> setShouldRetryServerErrors(boolean shouldRetryServerErrors) {
        mShouldRetryServerErrors = shouldRetryServerErrors;
        return this;
    }

    public final boolean shouldRetryServerErrors() {
        return mShouldRetryServerErrors;
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    public void markDelivered() {
        synchronized (mLock) {
            mResponseDelivered = true;
        }
    }

    public boolean hasHadResponseDelivered() {
        synchronized (mLock) {
            return mResponseDelivered;
        }
    }

    /**
     * Subclasses must implement this to parse the raw network response
     * and return an appropriate response type. This method will be
     * called from a worker thread.  The response will not be delivered
     * if you return null.
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * Subclasses can override this method to parse 'networkError' and return a more specific error.
     *
     * <p>The default implementation just returns the passed 'networkError'.</p>
     *
     * @param volleyError the error retrieved from the network
     * @return an NetworkError augmented with additional information
     */
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed
     * response to their listeners.  The given response is guaranteed to
     * be non-null; responses that fail to parse are not delivered.
     * @param response The parsed response returned by
     * {@link #parseNetworkResponse(NetworkResponse)}
     */
    abstract protected void deliverResponse(T response);

    public void deliverError(VolleyError error) {
        Response.ErrorListener listener;
        synchronized (mLock) {
            listener = mErrorListener;
        }
        if (listener != null) {
            listener.onErrorResponse(error);
        }
    }

    /**
     * {@link NetworkRequestCompleteListener} that will receive callbacks when the request
     * returns from the network.
     */
    /* package */ void setNetworkRequestCompleteListener(
            NetworkRequestCompleteListener requestCompleteListener) {
        synchronized (mLock) {
            mRequestCompleteListener = requestCompleteListener;
        }
    }

    /**
     * Notify NetworkRequestCompleteListener that a valid response has been received
     * which can be used for other, waiting requests.
     * @param response received from the network
     */
    /* package */ void notifyListenerResponseReceived(Response<?> response) {
        NetworkRequestCompleteListener listener;
        synchronized (mLock) {
            listener = mRequestCompleteListener;
        }
        if (listener != null) {
            listener.onResponseReceived(this, response);
        }
    }

    /**
     * Notify NetworkRequestCompleteListener that the network request did not result in
     * a response which can be used for other, waiting requests.
     */
    /* package */ void notifyListenerResponseNotUsable() {
        NetworkRequestCompleteListener listener;
        synchronized (mLock) {
            listener = mRequestCompleteListener;
        }
        if (listener != null) {
            listener.onNoUsableResponseReceived(this);
        }
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @Override
    public int compareTo(Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " "
                + getPriority() + " " + mSequence;
    }
}
