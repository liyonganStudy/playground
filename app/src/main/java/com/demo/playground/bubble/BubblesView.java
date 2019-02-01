package com.demo.playground.bubble;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by liyongan on 18/8/12.
 *
 */

public class BubblesView extends View implements ValueAnimator.AnimatorUpdateListener {
    private Paint mPaint;
    private ValueAnimator mAnimator;
    private BubbleWorlds mBubbleWorlds;

    public BubblesView(Context context) {
        super(context);
        init();
    }

    public BubblesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void startAnimator() {
        stopAnimator();
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    public void stopAnimator() {
        if (mAnimator.isStarted()) {
            mAnimator.end();
            mAnimator.removeAllUpdateListeners();
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mBubbleWorlds = new BubbleWorlds();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBubbleWorlds.setBounds(0, 300, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBubbleWorlds.renderBubbles(canvas, mPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mBubbleWorlds.updateBubbles();
        invalidate();
    }
}
