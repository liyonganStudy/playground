package com.demo.playground.snowanimate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.demo.playground.R;

public class SnowAnimateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snow_animate);
        final SnowFallLayout snowFallView = (SnowFallLayout) findViewById(R.id.snowFallView);
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snowFallView.startSnow();
            }
        });

        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snowFallView.endSnow();
            }
        });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SnowAnimateActivity.class);
        context.startActivity(intent);
    }
}
