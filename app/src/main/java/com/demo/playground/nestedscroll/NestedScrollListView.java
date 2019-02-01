package com.demo.playground.nestedscroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by liyongan on 19/1/29.
 */

public class NestedScrollListView extends ListView {
    private int[] mNestedOffsets = new int[2];
    private int[] mScrollConsumed = new int[2];
    private int[] mScrollOffset = new int[2];
    private int mScrollPointerId;
    private int mLastTouchX;
    private int mLastTouchY;
    private boolean isFirst = true;//DOWN事件没执行暂时
    private int lastDy;//暂时解决第一次MOVE与后序符号相反，导致的抖动问题

    public NestedScrollListView(Context context) {
        this(context, null);
    }

    public NestedScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        //下述代码主要复制于RecyclerView
//        final MotionEvent vtev = MotionEvent.obtain(e);
//        final int action = MotionEventCompat.getActionMasked(e);
//        final int actionIndex = MotionEventCompat.getActionIndex(e);
//        if (action == MotionEvent.ACTION_DOWN) {
//            mNestedOffsets[0] = mNestedOffsets[1] = 0;
//        }
//        vtev.offsetLocation(mNestedOffsets[0], mNestedOffsets[1]);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN: {
//                //不知道为啥没有执行
//                resetScroll(e);
//            }
//            break;
//
//            case MotionEventCompat.ACTION_POINTER_DOWN: {
//                mScrollPointerId = MotionEventCompat.getPointerId(e, actionIndex);
//                mLastTouchX = (int) (MotionEventCompat.getX(e, actionIndex) + 0.5f);
//                mLastTouchY = (int) (MotionEventCompat.getY(e, actionIndex) + 0.5f);
//            }
//            break;
//
//            case MotionEvent.ACTION_MOVE: {
//                final int index = MotionEventCompat.findPointerIndex(e, mScrollPointerId);
//                if (index < 0) {
//                    return false;
//                }
//
//                final int x = (int) (MotionEventCompat.getX(e, index) + 0.5f);
//                final int y = (int) (MotionEventCompat.getY(e, index) + 0.5f);
//                int dx = mLastTouchX - x;
//                int dy = mLastTouchY - y;
//                if (isFirst) {//暂时解决第次dy与后序符号相反导致的闪动问题
//                    Log.i("pyt", "FIRST");
//                    isFirst = false;
//                    resetScroll(e);
//                    return true;
//                }
//                if (!isSignOpposite(lastDy, dy)) {//解决手机触摸在屏幕上不松开一直抖动的问题
//                    lastDy = dy;
//                    Log.i("pyt", "move lastY" + mLastTouchY + ",y=" + y + ",dy=" + dy);
//                    if (dispatchNestedPreScroll(dx, dy, mScrollConsumed, mScrollOffset)) {
//                        vtev.offsetLocation(mScrollOffset[0], mScrollOffset[1]);
//                        // Updated the nested offsets
//                        mNestedOffsets[0] += mScrollOffset[0];
//                        mNestedOffsets[1] += mScrollOffset[1];
//                    }
//                    mLastTouchX = x - mScrollOffset[0];
//                    mLastTouchY = y - mScrollOffset[1];
//                }
//            }
//            break;
//            case MotionEvent.ACTION_UP: {
//                stopNestedScroll();
////                resetTouch();
//                isFirst = true;
//            }
//            break;
//
//            case MotionEvent.ACTION_CANCEL: {
////                cancelTouch();
//            }
//            break;
//        }
//        super.onTouchEvent(e);
//        return true;
//    }

    private void resetScroll(MotionEvent e) {
        lastDy = 0;
        mNestedOffsets[0] = mNestedOffsets[1] = 0;
        mScrollPointerId = MotionEventCompat.getPointerId(e, 0);
        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);
        int nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE;
        nestedScrollAxis |= ViewCompat.SCROLL_AXIS_VERTICAL;
        startNestedScroll(nestedScrollAxis);
    }

    private boolean isSignOpposite(int f, int s) {
        if (f > 0 && s < 0 || f < 0 && s > 0) {
            return true;
        }
        return false;
    }
}
