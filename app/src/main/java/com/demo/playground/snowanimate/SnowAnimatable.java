package com.demo.playground.snowanimate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.demo.playground.R;
import com.demo.playground.utils.DimensionUtils;

import java.util.Random;

/**
 * Created by lizongjun on 2017/11/28.
 */


public class SnowAnimatable {
    public static final int SNOW_NUM = 42;

    private static final int TYPE_CIRCLE = 1;
    private static final int TYPE_PARTICLE = 2;

    private static final int SCREEN_WIDTH = DimensionUtils.getScreenWidth();
    private static int SCREEN_HEIGHT;
    private static final int VERTICAL_SLOT = 10;
    private static final int HORIZONTAL_SLOT = 8;
    private static final int SPEED_MAX = DimensionUtils.dpToPx(2.33f);
    private static final int SPEED_MIN = DimensionUtils.dpToPx(1.33f);
    private static final int ANGLE_MAX = 6;

    private Context mContext;
    private int mType = 0, mHeight;
    private double mXSpeed, mYSpeed, mXoffset, mYoffset;
    private float mSize, mRotation, mRotationSpeed = 0, mAlpha, mAlpahSpeed;
    private boolean mStopped = false;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private Paint mPaint;

    private AnimationListener mAnimationListener;

    public interface AnimationListener {
        boolean OnAnimationEnd();
    }

    public SnowAnimatable(Context context) {
        mContext = context;
        init();
    }

    private void generateParams(boolean first) {
        int tmp;
        // 随机雪花的类型
        tmp = Randomizer.randomInt(12) >= 1 ? TYPE_CIRCLE : TYPE_PARTICLE;
        if (tmp != mType) {
            mType = tmp;
            BitmapDrawable drawable = (BitmapDrawable) mContext.getResources().getDrawable(mType == TYPE_CIRCLE ? R.mipmap.mainpage_animation_snow_1 : R.mipmap.mainpage_animation_snow_2);
            mBitmap = drawable.getBitmap();
            mHeight = mBitmap.getHeight();
        }
        // 随机雪花的初始位置
        tmp = Randomizer.randomInt(HORIZONTAL_SLOT);
        mXoffset = Randomizer.randomInt(SCREEN_WIDTH / HORIZONTAL_SLOT * tmp, SCREEN_WIDTH / HORIZONTAL_SLOT * (tmp + 1), false);
        if (first) {
            tmp = Randomizer.randomInt(VERTICAL_SLOT);
            mYoffset = -Randomizer.randomInt(SCREEN_HEIGHT / VERTICAL_SLOT * tmp, SCREEN_HEIGHT / VERTICAL_SLOT * (tmp + 1), false) - mHeight;
        } else {
            mYoffset = -mHeight;
        }
        // 随机雪花的大小
        mSize = (float) Randomizer.randomInt(2, 10, true) / 10f;
        // 随机雪花的速度和角度
        double angle = Math.toRadians(Randomizer.randomDouble(ANGLE_MAX) * Randomizer.randomSignum());
        double speed = Randomizer.randomInt(SPEED_MIN, SPEED_MAX, true);
        mXSpeed = speed * Math.sin(angle);
        mYSpeed = speed * Math.cos(angle);
        mYoffset -= mYSpeed;
        // 随机雪花alpha消失的距离
        mAlpahSpeed = 0f;
        mAlpha = 1f;
        if (mType == TYPE_CIRCLE) {
            boolean needAlpha = Randomizer.randomInt(10) >= 6;
            if (needAlpha) {
                mAlpahSpeed = ((float) Randomizer.randomInt(10) / 3f) / ((float) SCREEN_HEIGHT / (float) mYSpeed);
            }
        } else {
            mXSpeed = mXSpeed / 1.3;
            mYSpeed = mYSpeed / 1.3;
        }
        // 大片雪花自转
        mRotationSpeed = mType == TYPE_PARTICLE ? 720f / ((float) SCREEN_HEIGHT / (float) mYSpeed) : 0;
        mRotation = 0;
        // 重置matrix和paint
        resetProperties();
    }

    private void resetProperties() {
        mMatrix.reset();
        mPaint.setAlpha(255);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
    }

    public void setAnimationListener(AnimationListener listener) {
        mAnimationListener = listener;
    }

    private void init() {
        mMatrix = new Matrix();
        mPaint = new Paint();
        SCREEN_HEIGHT = DimensionUtils.getScreenHeight();
        generateParams(true);
    }

    public void resetAnimation() {
        if (mStopped) {
            generateParams(true);
        }
        mStopped = false;
    }

    public void updateAnimation() {
        if (mStopped) {
            return;
        }
        SCREEN_HEIGHT = DimensionUtils.getScreenHeight();
        mXoffset += mXSpeed;
        mYoffset += mYSpeed;

        mMatrix.setTranslate((float) mXoffset, (float) mYoffset);
        if (mRotationSpeed != 0) {
            mRotation += mRotationSpeed;
        }
        if (mRotation != 0) {
            mMatrix.preRotate(mRotation, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        } else {
            mMatrix.preScale(mSize, mSize, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        }
        if (mAlpahSpeed != 0) {
            mAlpha -= mAlpahSpeed;
            if (mAlpha < 0) {
                mAlpha = 0;
            }
            mPaint.setAlpha((int) (mAlpha * 255));
        }
        if (mYoffset > SCREEN_HEIGHT + mHeight) {
            generateParams(false);
            updateAnimation();
            if (mAnimationListener != null) {
                mStopped = mAnimationListener.OnAnimationEnd();
            }
        }
    }

    private static class Randomizer {
        private static Random random = new Random();

        static double randomDouble(int max) {
            return random.nextDouble() * max;
        }

        static int randomInt(int max) {
            return randomInt(max, false);
        }

        static int randomInt(int min, int max, boolean gaussian) {
            return randomInt(max - min, gaussian) + min;
        }

        static int randomInt(int max, boolean gaussian) {
            if (gaussian) {
                return (int) (Math.abs(randomGaussian()) * max);
            } else {
                return random.nextInt(max);
            }
        }

        static double randomGaussian() {
            double gaussian = random.nextGaussian() / 3; // more 99% of instances in range (-1, 1)
            if (gaussian > -1 && gaussian < 1) {
                return gaussian;
            } else {
                return randomGaussian();
            }
        }

        static int randomSignum() {
            return (random.nextBoolean()) ? 1 : -1;
        }
    }
}