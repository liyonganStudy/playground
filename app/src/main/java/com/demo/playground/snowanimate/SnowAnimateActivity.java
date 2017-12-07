package com.demo.playground.snowanimate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.demo.playground.R;

public class SnowAnimateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snow_animate);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SnowAnimateActivity.class);
        context.startActivity(intent);
    }
}
