package com.demo.playground.nestedscroll;

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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.demo.playground.R;

/**
 * Created by liyongan on 19/1/29.
 */

public class AdjustableHeaderLinearLayout extends LinearLayout implements NestedScrollingParent2 {
    private static final String TAG = "Adjustable";
    private View mHeaderView;
    private NestedScrollingParentHelper mScrollingParentHelper;
    private boolean mNeedHackDispatchTouch;
    private boolean mTouchDownOnHeader;
    private ValueAnimator mRevertAnimation;
    private int mMinHeaderHeight;

    private HeaderScrollListener mHeaderScrollListener;
    private boolean mNeedDragOver;
    private int mStickHeaderHeight;
    private int mMaxHeaderHeight;

    public AdjustableHeaderLinearLayout(Context context) {
        this(context, null);
    }

    public AdjustableHeaderLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdjustableHeaderLinearLayout, 0, 0);
        mNeedDragOver = a.getBoolean(R.styleable.AdjustableHeaderLinearLayout_needDragOver, false);
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

    public void setMaxHeaderHeight(int maxHeaderHeight) {
        mMaxHeaderHeight = maxHeaderHeight;
        if (mMinHeaderHeight != 0) {
            onMaxHeaderHeightChange(maxHeaderHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mNeedHackDispatchTouch = false;
            if (mNeedDragOver && getScrollY() < 0) {
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
        animator = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), 0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
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
            pointerCoords[i].y = pointerCoords[i].y + mMinHeaderHeight;
        }
        return MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getPointerCount(), pointerProperties, pointerCoords, event.getMetaState(), event.getButtonState(), event.getXPrecision(), event.getYPrecision(), event.getDeviceId(), event.getEdgeFlags(), event.getSource(), event.getFlags());
    }

    private void onMaxHeaderHeightChange(int maxHeaderHeight) {
        mHeaderView.getLayoutParams().height = maxHeaderHeight;
        ((MarginLayoutParams) mHeaderView.getLayoutParams()).topMargin = mMinHeaderHeight - maxHeaderHeight;
        setHeaderViewPaddingTop(getMaxDragOverHeight());
    }

    private int getMaxNeedHideHeight() {
        return mMinHeaderHeight - mStickHeaderHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMinHeaderHeight == 0) {
            mHeaderView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mMinHeaderHeight = mHeaderView.getLayoutParams().height;
            if (mMaxHeaderHeight != 0 && mNeedDragOver) {
                onMaxHeaderHeightChange(mMaxHeaderHeight);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + getMaxNeedHideHeight());
    }

    @Override
    public void scrollTo(int x, int y) {
        int maxDragOverHeight = getMaxDragOverHeight();
        if (y < -maxDragOverHeight) {
            y = -maxDragOverHeight;
        }
        int needHideHeight = getMaxNeedHideHeight();
        if (y > needHideHeight) {
            y = needHideHeight;
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
    public int getNestedScrollAxes() {
        return getScrollingParentHelper().getNestedScrollAxes();
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
    public void onNestedPreScroll(@NonNull final View target, int dx, int dy, int[] consumed, int type) {
        if (dy > 0) {
            if (getScrollY() < getMaxNeedHideHeight()) {
                consumed[1] = dy;
                scrollBy(0, dy);
            }
        } else {
            boolean canScrollDown = target.canScrollVertically(-1);
            if (!canScrollDown) {
                consumed[1] = dy;
                if (getScrollY() > 0) {
                    scrollBy(0, getScrollY() + dy < 0 ? -getScrollY() : dy);
                } else if (mNeedDragOver && type == ViewCompat.TYPE_TOUCH){
                    if (getScrollY() > -getMaxDragOverHeight()) {
                        scrollBy(0, dy);
                    }
                }
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mHeaderScrollListener != null) {
            if (oldt > 0 && t <=0) {
                mHeaderScrollListener.onHeaderTotalShow();
            } else if (t == getMaxNeedHideHeight()) {
                mHeaderScrollListener.onHeaderTotalHide();
            }
            mHeaderScrollListener.onScroll(t > 0 ? t : 0);
        }
        if (t <= 0) {
            setHeaderViewPaddingTop(getMaxDragOverHeight() + t);
        }
    }

    private void setHeaderViewPaddingTop(int top) {
        mHeaderView.setPadding(mHeaderView.getPaddingLeft(), top, mHeaderView.getPaddingRight(), mHeaderView.getPaddingBottom());
    }

    private int getMaxDragOverHeight() {
        return mMaxHeaderHeight - mMinHeaderHeight;
    }

    private NestedScrollingParentHelper getScrollingParentHelper() {
        if (mScrollingParentHelper == null) {
            mScrollingParentHelper = new NestedScrollingParentHelper(this);
        }
        return mScrollingParentHelper;
    }

    public interface HeaderScrollListener {
        void onScroll(int dy);

        void onHeaderTotalHide();

        void onHeaderTotalShow();
    }

    public class SimpleHeaderScrollListener implements HeaderScrollListener {

        @Override
        public void onScroll(int dy) {
        }

        @Override
        public void onHeaderTotalHide() {
        }

        @Override
        public void onHeaderTotalShow() {
        }
    }

}
