package com.demo.playground.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;

import com.demo.playground.R;
import com.demo.playground.utils.DimensionUtils;

/**
 * Created by hzliyongan on 2018/4/8.
 */

public class FollowButton extends android.support.v7.widget.AppCompatTextView {
    private int mCustomBackgroundColor;
    private ValueAnimator mFollowAnimator;
    private ValueAnimator mUnFollowAnimator;
    private boolean mShowFollowStatus = true;

    public FollowButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        mFollowAnimator = getAnimator(1f, 0f);
        mUnFollowAnimator = getAnimator(0f, 1f);
    }

    private ValueAnimator getAnimator(float from, float to) {
        final int unFollowWidth = DimensionUtils.dimen2px(getContext(), R.dimen.followButtonWidth);
        final int followedWith = DimensionUtils.dimen2px(getContext(), R.dimen.followButtonHeight);
        final int unFollowColor = getResources().getColor(R.color.themeRed);
        final int followedColor = ColorUtils.compositeColors(getResources().getColor(R.color.buttonBg), Color.BLACK);
        ValueAnimator animator = ValueAnimator.ofFloat(from, to).setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ArgbEvaluator argbEvaluator = new ArgbEvaluator();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();

                getLayoutParams().width = (int) (unFollowWidth - (unFollowWidth - followedWith) * fraction);
                requestLayout();
                setCustomBackgroundColor((Integer) argbEvaluator.evaluate(fraction, unFollowColor, followedColor));

                if (fraction < 0.3f) {
                    setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) ((1 - fraction / 0.3f) * 255)));
                    if (!mShowFollowStatus) {
                        mShowFollowStatus = true;
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        setText(getResources().getText(R.string.follow));
                    }
                } else {
                    if (mShowFollowStatus) {
                        mShowFollowStatus = false;
                        setText("");
                        setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vd_profile_btn_icn_followed, 0, 0);
                    }
                    int alpha = (int) ((fraction - 0.3f) / 0.7f * 255);
                    getCompoundDrawables()[1].setAlpha(alpha);
                    setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, alpha));

                }

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setClickable(false);
            }
        });
        return animator;
    }

    public FollowButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public int getCustomBackgroundColor() {
        return mCustomBackgroundColor;
    }

    public void setCustomBackgroundColor(int customBackgroundColor) {
        mCustomBackgroundColor = customBackgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mCustomBackgroundColor);
        super.onDraw(canvas);
    }

    public void startFollowAnimator() {
        mFollowAnimator.start();
    }

    public void startUnFollowAnimator() {
        mUnFollowAnimator.start();
    }

}
