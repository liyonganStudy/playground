package com.demo.playground.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.demo.playground.R;
import com.demo.playground.utils.DimensionUtils;

public class SimpleAnimationActivity extends AppCompatActivity {

    private FollowButton mFollowButton;
    private boolean mFollowed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_animation);
        mFollowButton = (FollowButton) findViewById(R.id.followButton);
        toggleFollowButton();
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFollowed) {
                    mFollowButton.startFollowAnimator();
                } else {
                    mFollowButton.startUnFollowAnimator();
                }
                mFollowed = !mFollowed;
            }
        });
    }



    private void toggleFollowButton() {
        if (mFollowed) {
            mFollowButton.setText("");
            mFollowButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vd_profile_btn_icn_followed, 0, 0);
            mFollowButton.setBackground(new ColorDrawable(ColorUtils.compositeColors(getResources().getColor(R.color.buttonBg), Color.BLACK)));
            mFollowButton.getLayoutParams().width = DimensionUtils.dimen2px(this, R.dimen.followButtonHeight);
        } else {
            mFollowButton.setText(getResources().getString(R.string.follow));
            mFollowButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mFollowButton.setBackground(new ColorDrawable(getResources().getColor(R.color.themeRed)));
            mFollowButton.getLayoutParams().width = DimensionUtils.dimen2px(this, R.dimen.followButtonWidth);
        }
    }
}
