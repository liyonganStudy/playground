package com.demo.playground.immersivemode;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.demo.playground.R;
import com.demo.playground.utils.CompatibleUtils;

public class ImmersiveModeActivity extends AppCompatActivity {

    private View mDecorView, mContainer;
    private TextView mToggleStatusBarVisi, mToggleBehindStatusBar, mTextView, mToggleImmersion;
    private int mStatusBarColor;
    private boolean mTransparentStatusBar;
    private boolean mStatusBarHide;
    private boolean mBehindStatusBar;
    private boolean mUseImmersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immersive_mode);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mDecorView = getWindow().getDecorView();
        mToggleStatusBarVisi = (TextView) findViewById(R.id.toggleStatusBarVisi);
        mToggleStatusBarVisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatusBarHide) {
                    showStatusBar();
                } else {
                    hideStatusBar();
                }
                mStatusBarHide = !mStatusBarHide;
                mToggleStatusBarVisi.setText(getString(R.string.toggleStatusbarVisi, (mStatusBarHide ? ": hide" : ": show")));
            }
        });
        mToggleStatusBarVisi.setText(getString(R.string.toggleStatusbarVisi, (mStatusBarHide ? ": hide" : ": show")));

        mContainer = findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText(getString(R.string.coveredTextView, (mContainer.getFitsSystemWindows() ? "fitsSystemWindows" : "unfitsSystemWindows")));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContainer.setFitsSystemWindows(!mContainer.getFitsSystemWindows());
                mTextView.setText(getString(R.string.coveredTextView, (mContainer.getFitsSystemWindows() ? "fitsSystemWindows" : "unfitsSystemWindows")));
                mContainer.getParent().requestLayout();
            }
        });

        findViewById(R.id.toggleStatusBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTransparentStatusBar) {
                    restoreStatusBarColor();
                } else {
                    transparentStatusBar();
                }
                mTransparentStatusBar = !mTransparentStatusBar;
            }
        });

        mToggleBehindStatusBar = (TextView) findViewById(R.id.toggleBehindStatusBar);
        mToggleBehindStatusBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBehindStatusBar) {
                    unBehindStatusBar();
                } else {
                    behindStatusBar();
                }
                mBehindStatusBar = !mBehindStatusBar;
                mToggleBehindStatusBar.setText(getString(R.string.toggleBehindStatusBar, (mBehindStatusBar ? ": behind" : ": not behind")));
            }
        });
        mToggleBehindStatusBar.setText(getString(R.string.toggleBehindStatusBar, (mBehindStatusBar ? ": behind" : ": not behind")));

        mToggleImmersion = (Button) findViewById(R.id.toggleImmersion);
        mToggleImmersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUseImmersion) {
                    unUseImmersion();
                } else {
                    useImmersion();
                }
                mUseImmersion = !mUseImmersion;
                mToggleImmersion.setText(getString(R.string.toggleImmersion, mUseImmersion + ""));
            }
        });
        mToggleImmersion.setText(getString(R.string.toggleImmersion, mUseImmersion + ""));
    }

    private void useImmersion() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void unUseImmersion() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void transparentStatusBar() {
        if (CompatibleUtils.isVersionLollipopAndUp()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mStatusBarColor = getWindow().getStatusBarColor();
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void restoreStatusBarColor() {
        if (CompatibleUtils.isVersionLollipopAndUp()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(mStatusBarColor);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void hideStatusBar() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void showStatusBar() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void behindStatusBar() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
//            mDecorView.setSystemUiVisibility(mDecorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN); // need test
        }
    }

    private void unBehindStatusBar() {
        if (CompatibleUtils.isVersionKitkatAndUp()) {
            mDecorView.setSystemUiVisibility((mDecorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN));
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN); // need test
        }
    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            mDecorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
}
