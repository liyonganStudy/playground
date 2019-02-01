package com.demo.playground.bubble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.demo.playground.R;

public class BubbleActivity extends AppCompatActivity {

    private BubblesView mBubblesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        mBubblesView = (BubblesView) findViewById(R.id.bubblesView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBubblesView.startAnimator();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBubblesView.stopAnimator();
    }
}
