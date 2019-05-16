package com.demo.playground.coordinate;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liyongan on 19/3/12.
 */

public class LayoutBehavior extends ViewOffsetBehavior<View> {

    public LayoutBehavior() {
    }

    public LayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        setLeftAndRightOffset(dependency.getLeft());
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        child.setVisibility(View.GONE);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        if (parent.findViewById(((CoordinatorLayout.LayoutParams) child.getLayoutParams()).getAnchorId()).getVisibility() == View.GONE) {
            child.setVisibility(View.GONE);
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }
}
