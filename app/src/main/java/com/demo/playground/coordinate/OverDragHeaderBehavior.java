package com.demo.playground.coordinate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liyongan on 19/3/12.
 */

public class OverDragHeaderBehavior extends HeaderBehavior {

    public OverDragHeaderBehavior() {
    }

    public OverDragHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    boolean canDragView(View view) {
        return true;
    }
}
