package com.demo.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.playground.snowanimate.SnowAnimateActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.openSnowAnimate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnowAnimateActivity.launch(MainActivity.this);
            }
        });
    }
}
