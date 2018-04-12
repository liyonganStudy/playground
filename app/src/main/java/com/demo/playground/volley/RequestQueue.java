package com.demo.playground.volley;

import android.os.Handler;
import android.os.Looper;

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

//    public interface RequestFinishedListener<T> {
//        void onRequestFinished(Request<T> request);
//    }

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    private final AtomicInteger mSequenceGenerator = new AtomicInteger();
    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();
    private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<>();
    private final NetworkDispatcher[] mDispatchers;
//    private CacheDispatcher mCacheDispatcher;
//    private final List<RequestFinishedListener> mFinishedListeners = new ArrayList<>();

    private final Cache mCache;
    private final Network mNetwork;
    private final ResponseDelivery mDelivery;


    public RequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
        mDispatchers = new NetworkDispatcher[threadPoolSize];
        mCache = cache;
        mNetwork = network;
        mDelivery = delivery;
    }

    public RequestQueue(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public RequestQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public void start() {
        stop();
//        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
//        mCacheDispatcher.start();

        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork, mCache, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    public void stop() {
//        if (mCacheDispatcher != null) {
//            mCacheDispatcher.quit();
//        }
        for (final NetworkDispatcher mDispatcher : mDispatchers) {
            if (mDispatcher != null) {
                mDispatcher.quit();
            }
        }
    }

    public <T> Request<T> add(Request<T> request) {
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue");
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

    public interface RequestFilter {
        boolean apply(Request<?> request);
    }

    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getTag() == tag;
            }
        });
    }

    <T> void finish(Request<T> request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }
}
