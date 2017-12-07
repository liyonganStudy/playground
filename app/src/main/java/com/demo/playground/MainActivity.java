package com.demo.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.playground.widget.PrinterShimmerTextView;
import com.demo.playground.widget.ShimmerFrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PrinterShimmerTextView textView = (PrinterShimmerTextView) findViewById(R.id.text);
        textView.setNeedAnimation();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.startAnimation();
            }
        });

//        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerContent);
//        shimmerFrameLayout.startShimmerAnimation();
        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmerAnimation();
    }
}
