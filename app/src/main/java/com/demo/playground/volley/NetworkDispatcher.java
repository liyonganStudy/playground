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

import android.os.Process;

public class NetworkDispatcher extends Thread {

//    private final BlockingQueue<Request<?>> mQueue;
//    private final Network mNetwork;
//    private final Cache mCache;
//    private final ResponseDelivery mDelivery;
    private volatile boolean mQuit = false;

//    public NetworkDispatcher(BlockingQueue<Request<?>> queue,
//                             Network network, Cache cache, ResponseDelivery delivery) {
//        mQueue = queue;
//        mNetwork = network;
//        mCache = cache;
//        mDelivery = delivery;
//    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            try {
                processRequest();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
            }
        }
    }

    // Extracted to its own method to ensure locals have a constrained liveness scope by the GC.
    // This is needed to avoid keeping previous request references alive for an indeterminate amount
    // of time. Update consumer-proguard-rules.pro when modifying this. See also
    // https://github.com/google/volley/issues/114
    private void processRequest() throws InterruptedException {
        // Take a request from the queue.
//        Request<?> request = mQueue.take();
//
//        long startTimeMs = SystemClock.elapsedRealtime();
//        try {
//            request.addMarker("network-queue-take");
//
//            // If the request was cancelled already, do not perform the
//            // network request.
//            if (request.isCanceled()) {
//                request.finish("network-discard-cancelled");
//                request.notifyListenerResponseNotUsable();
//                return;
//            }
//
//            // Perform the network request.
//            NetworkResponse networkResponse = mNetwork.performRequest(request);
//            request.addMarker("network-http-complete");
//
//            // If the server returned 304 AND we delivered a response already,
//            // we're done -- don't deliver a second identical response.
//            if (networkResponse.notModified && request.hasHadResponseDelivered()) {
//                request.finish("not-modified");
//                request.notifyListenerResponseNotUsable();
//                return;
//            }
//
//            // Parse the response here on the worker thread.
//            Response<?> response = request.parseNetworkResponse(networkResponse);
//            request.addMarker("network-parse-complete");
//
//            // Write to cache if applicable.
//            // TODO: Only update cache metadata instead of entire record for 304s.
//            if (request.shouldCache() && response.cacheEntry != null) {
//                mCache.put(request.getCacheKey(), response.cacheEntry);
//                request.addMarker("network-cache-written");
//            }
//
//            // Post the response back.
//            request.markDelivered();
//            mDelivery.postResponse(request, response);
//            request.notifyListenerResponseReceived(response);
//        } catch (VolleyError volleyError) {
//            volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
//            parseAndDeliverNetworkError(request, volleyError);
//            request.notifyListenerResponseNotUsable();
//        } catch (Exception e) {
//            VolleyLog.e(e, "Unhandled exception %s", e.toString());
//            VolleyError volleyError = new VolleyError(e);
//            volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
//            mDelivery.postError(request, volleyError);
//            request.notifyListenerResponseNotUsable();
//        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
//        error = request.parseNetworkError(error);
//        mDelivery.postError(request, error);
    }
}
