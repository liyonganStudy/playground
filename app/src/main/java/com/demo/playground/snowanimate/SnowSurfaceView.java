package com.demo.playground.snowanimate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizongjun on 2017/11/29.
 */

public class SnowSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SnowAnimatable.AnimationListener {
    private HandlerThread mRenderThread;
    private RenderHandler mHandler;
    private SurfaceHolder mHolder;
    // 这里是双线程的，所以下面的变量都要在锁里操作
    private List<SnowAnimatable> mSnows = new ArrayList<>();
    private int mStoppedCount;
    private boolean mIsSnowStopped;
    private ValueAnimator mSnowAnimator;
    private AnimationListener mAnimationListener;

    public interface AnimationListener {
        void OnSnowAnimationEnd();
    }

    public SnowSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);
    }

    private void updateAnimation() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(RenderHandler.MSG_INVALIDATE);
        }
    }

    private synchronized void doDrawInner() {
        if (mHolder == null || mHolder.isCreating()) {
            return;
        }
        Canvas canvas;
        if (!mHolder.getSurface().isValid()) {
            return;
        }
        try {
            canvas = mHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                for (SnowAnimatable snow : mSnows) {
                    snow.updateAnimation();
                    snow.draw(canvas);
                }
                mHolder.unlockCanvasAndPost(canvas);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        mRenderThread = new HandlerThread("SnowRender", android.os.Process.THREAD_PRIORITY_FOREGROUND);
        mRenderThread.start();
        mHandler = new RenderHandler(mRenderThread.getLooper());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHandler.removeCallbacksAndMessages(null);
        mRenderThread.quit();
        synchronized (this) {
            mSnowAnimator.end();
            mSnows.clear();
        }
    }

    private class RenderHandler extends Handler {
        private static final int MSG_INVALIDATE = 0;

        private RenderHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVALIDATE:
                    doDrawInner();
                    break;
            }
        }
    }

    public synchronized void startSnow() {
        if (mSnowAnimator == null) {
            mSnowAnimator = ValueAnimator.ofFloat(0, 1f);
            mSnowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateAnimation();
                }
            });
            mSnowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        if (mSnowAnimator.isStarted() && !mIsSnowStopped) {
            return;
        }
        mIsSnowStopped = false;
        mStoppedCount = 0;
        if (mSnowAnimator.isStarted()) {
            for (SnowAnimatable snow : mSnows) {
                snow.resetAnimation();
            }
            return;
        }
        for (int n = 0; n < SnowAnimatable.SNOW_NUM; n ++) {
            SnowAnimatable snow = new SnowAnimatable(getContext());
            snow.setAnimationListener(this);
            mSnows.add(snow);
        }
        mSnowAnimator.start();
    }

    public synchronized void endSnow() {
        mStoppedCount = 0;
        mIsSnowStopped = true;
    }

    @Override
    public synchronized boolean OnAnimationEnd() {
        if (mIsSnowStopped) {
            mStoppedCount++;
            if (mStoppedCount + 1 >= SnowAnimatable.SNOW_NUM) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        OnAnimationRealEnd();
                    }
                });
            }
        }
        return mIsSnowStopped;
    }

    private synchronized void OnAnimationRealEnd() {
        mSnowAnimator.end();
        mSnows.clear();
        if (mAnimationListener != null) {
            mAnimationListener.OnSnowAnimationEnd();
        }
    }

    public synchronized boolean isSnowing() {
        return mSnowAnimator.isStarted();
    }

    public void setAnimationListener(AnimationListener listener) {
        mAnimationListener = listener;
    }
}
