package com.demo.playground.coordinate;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.demo.playground.R;

public class CoordinatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);

        final TextView childA = findViewById(R.id.childA);
        childA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.offsetLeftAndRight(childA, 100);
            }
        });
//        SwipeDismissBehavior swipeDismissBehavior = new SwipeDismissBehavior();
//        swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
//        ((CoordinatorLayout.LayoutParams) childA.getLayoutParams()).setBehavior(swipeDismissBehavior);
//        swipeDismissBehavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
//            @Override
//            public void onDismiss(View view) {
//                childA.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onDragStateChanged(int state) {
//
//            }
//        });

        final TextView childB = findViewById(R.id.childB);
        childB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestLayout();
            }
        });
    }

}
