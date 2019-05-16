package com.demo.playground.coordinate;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liyongan on 19/3/12.
 */

public class TouchBehavior extends CoordinatorLayout.Behavior {
    private static final String TAG = "LayoutBehavior";

    public TouchBehavior() {
    }

    public TouchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        Log.i(TAG, "onInterceptTouchEvent: ");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        Log.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(parent, child, ev);
    }
}
