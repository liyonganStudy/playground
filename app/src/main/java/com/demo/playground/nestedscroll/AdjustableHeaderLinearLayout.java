package com.demo.playground.nestedscroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.demo.playground.R;
import com.demo.playground.utils.DimensionUtils;

/**
 * Created by liyongan on 19/1/29.
 */

public class AdjustableHeaderLinearLayout extends LinearLayout implements NestedScrollingParent2 {
    private View mHeaderView, mSecondView;
    private NestedScrollingParentHelper mScrollingParentHelper;
    private boolean mNeedHackDispatchTouch;
    private boolean mTouchDownOnHeader;
    private ValueAnimator mRevertAnimation;
    private HeaderScrollListener mHeaderScrollListener;

    private boolean mNeedDragOver, mHeaderHasPicBg;
    private int mStickHeaderHeight;

    public AdjustableHeaderLinearLayout(Context context) {
        this(context, null);
    }

    public AdjustableHeaderLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdjustableHeaderLinearLayout, 0, 0);
        mNeedDragOver = a.getBoolean(R.styleable.AdjustableHeaderLinearLayout_needDragOver, false);
        mHeaderHasPicBg = a.getBoolean(R.styleable.AdjustableHeaderLinearLayout_headerHasPicBg, false);
        mStickHeaderHeight = a.getDimensionPixelSize(R.styleable.AdjustableHeaderLinearLayout_stickSectionHeight, 0);
        a.recycle();

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return mTouchDownOnHeader;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mTouchDownOnHeader) {
                    mNeedHackDispatchTouch = true;
                    AdjustableHeaderLinearLayout.this.dispatchTouchEvent(e1);
                    AdjustableHeaderLinearLayout.this.dispatchTouchEvent(e2);
                }
                return mTouchDownOnHeader;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void setNeedDragOver(boolean needDragOver) {
        mNeedDragOver = needDragOver;
    }

    public void setHeaderScrollListener(HeaderScrollListener headerScrollListener) {
        mHeaderScrollListener = headerScrollListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mNeedHackDispatchTouch = false;
            if (mNeedDragOver && mHeaderView.getLayoutParams().height > getMinHeaderHeight()) {
                if (mRevertAnimation == null || !mRevertAnimation.isRunning()) {
                    mRevertAnimation = getRevertAnimation();
                    mRevertAnimation.start();
                }
                return true;
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            mTouchDownOnHeader = ev.getY() < getMaxNeedHideHeight() - getScrollY();
        }
        if (mTouchDownOnHeader && mNeedHackDispatchTouch) {
            return super.dispatchTouchEvent(obtainNewMotionEvent(ev));
        }
        return super.dispatchTouchEvent(ev);
    }

    private ObjectAnimator getRevertAnimation() {
        ObjectAnimator animator;
        if (mHeaderHasPicBg) {
            animator = ObjectAnimator.ofInt(mHeaderView, new HeightProperty(), mHeaderView.getLayoutParams().height, getMinHeaderHeight());
        } else {
            animator = ObjectAnimator.ofFloat(mHeaderView, "translationY", 0, getMinHeaderHeight() - mHeaderView.getLayoutParams().height);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mSecondView.setTranslationY(value);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHeaderView.getLayoutParams().height = getMinHeaderHeight();
                    mHeaderView.setTranslationY(0);
                    mSecondView.setTranslationY(0);
                    mHeaderView.requestLayout();
                }
            });
        }
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private static class HeightProperty extends Property<View, Integer> {

        HeightProperty() {
            super(Integer.class, "height");
        }

        @Override
        public Integer get(View object) {
            return object.getHeight();
        }

        @Override
        public void set(View object, Integer value) {
            object.getLayoutParams().height = value;
            object.setLayoutParams(object.getLayoutParams());
        }
    }


    private MotionEvent obtainNewMotionEvent(MotionEvent event) {
        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[event.getPointerCount()];
        for (int i = 0; i < event.getPointerCount(); i++) {
            pointerProperties[i] = new MotionEvent.PointerProperties();
            event.getPointerProperties(i, pointerProperties[i]);
        }
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[event.getPointerCount()];
        for (int i = 0; i < event.getPointerCount(); i++) {
            pointerCoords[i] = new MotionEvent.PointerCoords();
            event.getPointerCoords(i, pointerCoords[i]);
            pointerCoords[i].y = pointerCoords[i].y + getMinHeaderHeight();
        }
        return MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getPointerCount(), pointerProperties, pointerCoords, event.getMetaState(), event.getButtonState(), event.getXPrecision(), event.getYPrecision(), event.getDeviceId(), event.getEdgeFlags(), event.getSource(), event.getFlags());
    }

    private int getMaxHeaderHeight() { // TODO: 19/1/31
        return DimensionUtils.dpToPx(400);
    }

    private int getMinHeaderHeight() { // TODO: 19/1/31
        return DimensionUtils.dpToPx(300);
    }

    private int getMaxNeedHideHeight() {
        return getMinHeaderHeight() - mStickHeaderHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = getChildAt(0);
        mSecondView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // TODO: 19/1/31
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + DimensionUtils.dpToPx(250));
//        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + mHeaderView.getMeasuredHeight());
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > getMaxNeedHideHeight()) {
            y = getMaxNeedHideHeight();
        }
        if (mHeaderScrollListener != null) {
            mHeaderScrollListener.onScroll(y);
        }
        super.scrollTo(x, y);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRevertAnimation != null) {
            mRevertAnimation.cancel();
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        getScrollingParentHelper().onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        getScrollingParentHelper().onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, int[] consumed, int type) {
        if (dy > 0) { // 向上
            if (mNeedDragOver && getScrollY() == 0 && mHeaderView.getLayoutParams().height > getMinHeaderHeight() && type == ViewCompat.TYPE_TOUCH) {
                consumed[1] = dy;
                final int finalDy = dy;
                post(new Runnable() {
                    @Override
                    public void run() {
                        mHeaderView.getLayoutParams().height = Math.max(mHeaderView.getLayoutParams().height - finalDy, getMinHeaderHeight());
                        mHeaderView.requestLayout();
                    }
                });
            } else if (getScrollY() < getMaxNeedHideHeight()) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        } else { // 向下
            boolean canScrollDown = target.canScrollVertically(-1);
            if (!canScrollDown) {
                if (getScrollY() > 0) {
                    scrollBy(0, dy);
                    consumed[1] = dy;
                } else if (mNeedDragOver && getScrollY() == 0 && type == ViewCompat.TYPE_TOUCH) {
                    if (mHeaderView.getLayoutParams().height <= getMaxHeaderHeight()) {
                        mHeaderView.getLayoutParams().height -= dy;
                        mHeaderView.requestLayout();
                    }
                }
            }
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return getScrollingParentHelper().getNestedScrollAxes();
    }

    private NestedScrollingParentHelper getScrollingParentHelper() {
        if (mScrollingParentHelper == null) {
            mScrollingParentHelper = new NestedScrollingParentHelper(this);
        }
        return mScrollingParentHelper;
    }

    public interface HeaderScrollListener {
        void onScroll(int dy);
    }

}
