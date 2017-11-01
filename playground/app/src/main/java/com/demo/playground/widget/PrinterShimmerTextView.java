package com.demo.playground.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.demo.playground.R;

/**
 * Created by liyongan on 17/10/31.
 */

public class PrinterShimmerTextView extends AppCompatTextView {

    private static final int MESSAGE_WHAT_PRINTER = 1;
    private static final int MESSAGE_WHAT_SHIMMER = 2;
    private static final int MESSAGE_WHAT_PRINTER_REVERT = 3;
    private static final long PRINTER_ANIMATION_INTERVAL = 200;
    private static final long SHIMMER_ANIMATION_INTERVAL = 16;
    private static final int SHIMMER_ANIMATION_DURATION = 500;
    private Paint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private int mWidth;
    private int mStartColor;
    private int mEndColor;
    private static final int DEFAULT_START_COLOR = 0xFFFF50ED;
    private static final int DEFAULT_END_COLOR = 0xFF3455FF;
    private String mWholeContent = "近期热评在这里哦~";
    private String mFinalContent;
    private float mOffset = 0;
    private int mCurrentCharIndex;
    private boolean mNeedShimmer;

    private Handler mHandler = new Handler() {
        private float OFFSET_ONE_TIME;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_WHAT_PRINTER:
                    if (mCurrentCharIndex < mWholeContent.length()) {
                        mCurrentCharIndex++;
                        setText(mWholeContent.substring(0, mCurrentCharIndex));
                        sendPrinterAnimationMessage();
                    } else if (mCurrentCharIndex == mWholeContent.length()) {
                        sendShimmerAnimationMessage();
                    }
                    break;
                case MESSAGE_WHAT_SHIMMER:
                    if (OFFSET_ONE_TIME == 0) {
                        OFFSET_ONE_TIME = mWidth * 1F * SHIMMER_ANIMATION_INTERVAL / SHIMMER_ANIMATION_DURATION;
                    }
                    if (mOffset < mWidth) {
                        mNeedShimmer = true;
                        mOffset += OFFSET_ONE_TIME;
                        invalidate();
                        sendShimmerAnimationMessage();
                    } else {
                        mNeedShimmer = false;
                        mOffset = 0;
                        sendRevertAnimationMessage();
                    }
                    break;
                case MESSAGE_WHAT_PRINTER_REVERT:
                    if (mCurrentCharIndex > mFinalContent.length()) {
                        mCurrentCharIndex--;
                        setText(mWholeContent.substring(0, mCurrentCharIndex));
                        sendRevertAnimationMessage();
                    } else {
                        mCurrentCharIndex = 0;
                    }
                    break;
            }
        }
    };

    public PrinterShimmerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = getPaint();
        mMatrix = new Matrix();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PrinterShimmerTextView);
        try {
            mStartColor = array.getColor(R.styleable.PrinterShimmerTextView_start_color, DEFAULT_START_COLOR);
            mEndColor = array.getColor(R.styleable.PrinterShimmerTextView_end_color, DEFAULT_END_COLOR);
        } finally {
            array.recycle();
        }
        mWidth = (int) mPaint.measureText(mWholeContent, 0, mWholeContent.length());
        mLinearGradient = new LinearGradient(-mWidth / 2, 0, mWidth / 2, 0, new int[]{mStartColor, mStartColor, mEndColor, mStartColor, mStartColor}, new float[]{0, 0.3f, 0.5f, 0.7f, 1.0f}, Shader.TileMode.CLAMP);
    }

    public void setNeedAnimation() {
        mFinalContent = getText().toString();
        setText("");
        setTextColor(mStartColor);
    }

    public void startAnimation() {
        stopAnimation();
        sendPrinterAnimationMessage();
    }

    private void sendPrinterAnimationMessage() {
        if (!mHandler.hasMessages(MESSAGE_WHAT_PRINTER)) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WHAT_PRINTER), PRINTER_ANIMATION_INTERVAL);
        }
    }

    private void sendShimmerAnimationMessage() {
        if (!mHandler.hasMessages(MESSAGE_WHAT_SHIMMER)) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WHAT_SHIMMER), SHIMMER_ANIMATION_INTERVAL);
        }
    }

    private void sendRevertAnimationMessage() {
        if (!mHandler.hasMessages(MESSAGE_WHAT_PRINTER_REVERT)) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WHAT_PRINTER_REVERT), PRINTER_ANIMATION_INTERVAL);
        }
    }

    public void stopAnimation() {
        mHandler.removeCallbacksAndMessages(null);
        mCurrentCharIndex = 0;
        mOffset = 0;
        mNeedShimmer = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mNeedShimmer) {
            mMatrix.setTranslate(mOffset, 0);
            mLinearGradient.setLocalMatrix(mMatrix);
            mPaint.setShader(mLinearGradient);
        } else {
            mPaint.setShader(null);
        }
        super.onDraw(canvas);
    }
}
