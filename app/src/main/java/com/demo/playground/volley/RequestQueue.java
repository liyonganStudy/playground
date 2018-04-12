package com.demo.playground.volley;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liyongan on 18/3/30.
 */

public class RequestQueue {
    /** Callback interface for completed requests. */
    public interface RequestFinishedListener<T> {
        /** Called when a request has finished processing. */
        void onRequestFinished(Request<T> request);
    }

    /** Used for generating monotonically-increasing sequence numbers for requests. */
    private final AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();

    /** The cache triage queue. */
    private final PriorityBlockingQueue<Request<?>> mCacheQueue =
            new PriorityBlockingQueue<>();

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue =
            new PriorityBlockingQueue<>();

    /** Number of network request dispatcher threads to start. */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    /** Cache interface for retrieving and storing responses. */
//    private final Cache mCache;

    /** Network interface for performing requests. */
//    private final Network mNetwork;

    /** Response delivery mechanism. */
//    private final ResponseDelivery mDelivery;

    /** The network dispatchers. */
//    private final NetworkDispatcher[] mDispatchers;

    /** The cache dispatcher. */
//    private CacheDispatcher mCacheDispatcher;

    private final List<RequestFinishedListener> mFinishedListeners = new ArrayList<>();

//    public RequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
//        mCache = cache;
//        mNetwork = network;
//        mDispatchers = new NetworkDispatcher[threadPoolSize];
//        mDelivery = delivery;
//    }

    public RequestQueue(Cache cache, Network network, int threadPoolSize) {
//        this(cache, network, threadPoolSize,
//                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public RequestQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public void start() {
        stop();
        // Make sure any currently running dispatchers are stopped.
        // Create the cache dispatcher and start it.
//        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
//        mCacheDispatcher.start();

        // Create network dispatchers (and corresponding threads) up to the pool size.
//        for (int i = 0; i < mDispatchers.length; i++) {
//            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork,
//                    mCache, mDelivery);
//            mDispatchers[i] = networkDispatcher;
//            networkDispatcher.start();
//        }
    }

    public void stop() {
//        if (mCacheDispatcher != null) {
//            mCacheDispatcher.quit();
//        }
//        for (final NetworkDispatcher mDispatcher : mDispatchers) {
//            if (mDispatcher != null) {
//                mDispatcher.quit();
//            }
//        }
    }

    public <T> Request<T> add(Request<T> request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue");

        // If the request is uncacheable, skip the cache queue and go straight to the network.
        if (!request.shouldCache()) {
            mNetworkQueue.add(request);
            return request;
        }
        mCacheQueue.add(request);
        return request;
    }

    private int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }
}
