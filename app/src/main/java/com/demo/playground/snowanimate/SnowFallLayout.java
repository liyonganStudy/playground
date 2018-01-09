package com.demo.playground.snowanimate;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.demo.playground.utils.DimensionUtils;

/**
 * Created by liyongan on 17/12/7.
 */

public class SnowFallLayout extends ViewGroup implements SnowSurfaceView.AnimationListener {
    private SnowSurfaceView mSnowSurface;

    public SnowFallLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
    }

    public SnowFallLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mSnowSurface != null) {
            mSnowSurface.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSnowSurface != null) {
            mSnowSurface.measure(MeasureSpec.makeMeasureSpec(DimensionUtils.getScreenWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(DimensionUtils.getScreenHeight(), MeasureSpec.EXACTLY));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void startSnow() {
        if (mSnowSurface == null) {
            mSnowSurface = new SnowSurfaceView(getContext());
            addView(mSnowSurface);
            mSnowSurface.setAnimationListener(this);
        }
        mSnowSurface.startSnow();
    }

    public void endSnow() {
        if (mSnowSurface != null) {
            mSnowSurface.endSnow();
        }
    }

    // 下雪真正停止（最后一片雪花落下）会回调
    @Override
    public void OnSnowAnimationEnd() {
        if (mSnowSurface != null) {
            removeView(mSnowSurface);
            mSnowSurface = null;
        }
    }

}
