package com.demo.playground.scrolllyric;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.demo.playground.R;

/**
 * Created by hzliyongan on 2018/1/25.
 */

public class LyricContainer extends LinearLayout {

    private LayoutParams layoutParams;
    private ValueAnimator valueAnimator;
    private int mDeltaHeight;
    private int mOriginTransY;

    public LyricContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);


        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);

        TextView textView = new TextView(context);
        textView.setText("text1");
        textView.setTextSize(30);
        addView(textView);

        addView(new Space(context), layoutParams);

        TextView textView2 = new TextView(context);
        textView2.setText("text2");
        textView2.setTextSize(30);
        addView(textView2);
        addView(new Space(context), layoutParams);

        TextView textView3 = new TextView(context);
        textView3.setText("text3");
        textView3.setTextSize(30);
        addView(textView3);
        addView(new Space(context), layoutParams);


        TextView textView4 = new TextView(context);
        textView4.setTextSize(30);
        addView(textView4);
    }

    public void setLyrics(String... texts) {
        getFirstLineView().setText(texts[0]);
        getSecondLineView().setText(texts[1]);
        getThirdLineView().setText(texts[2]);
        getLastLineView().setText(texts[3]);

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeViewAt(1);
                removeViewAt(0);
                setLyricsTransY(0);
                Space space = new Space(LyricContainer.this.getContext());
                addView(space, layoutParams);
                TextView textView4 = new TextView(LyricContainer.this.getContext());
                textView4.setTextSize(30);
                addView(textView4);
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                float tranY = -fraction * 1f * mDeltaHeight;
                setLyricsTransY(mOriginTransY + tranY);
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        int lastChildHeight = getLastLineView().getMeasuredHeight();
        int firstChildHeight = getFirstLineView().getMeasuredHeight();
        if (firstChildHeight > lastChildHeight) {
            height = height - lastChildHeight - 30;
        } else {
            height = height - firstChildHeight - 30;
        }
        mDeltaHeight = lastChildHeight + 30;
        setMeasuredDimension(getMeasuredWidth(), height);
        mOriginTransY = firstChildHeight > lastChildHeight ? 0 : lastChildHeight - firstChildHeight;
        setLyricsTransY(mOriginTransY);

    }

    private void setLyricsTransY(float transY) {
        getFirstLineView().setTranslationY(transY);
        getSecondLineView().setTranslationY(transY);
        getThirdLineView().setTranslationY(transY);
        getLastLineView().setTranslationY(transY);
    }

    private TextView getFirstLineView() {
        return (TextView) getChildAt(0);
    }

    private TextView getSecondLineView() {
        return (TextView) getChildAt(2);
    }

    private TextView getThirdLineView() {
        return (TextView) getChildAt(4);
    }

    private TextView getLastLineView() {
        return (TextView) getChildAt(getChildCount() - 1);
    }

}
