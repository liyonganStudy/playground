package com.demo.playground.nestedscroll;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.playground.R;
import com.demo.playground.utils.DimensionUtils;

import java.util.ArrayList;

public class NestedScrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_scroll);
        final Button button = findViewById(R.id.button);
        final AdjustableHeaderLinearLayout nestedScrollParentLinearLayout = findViewById(R.id.parent);
        button.setOnClickListener(new View.OnClickListener() {
            boolean useDragOver = true;
            @Override
            public void onClick(View v) {
                if (useDragOver) {
                    useDragOver = false;
                    button.setText("use dragover");
                    nestedScrollParentLinearLayout.setNeedDragOver(useDragOver);
                } else {
                    useDragOver = true;
                    button.setText("stop dragover");
                    nestedScrollParentLinearLayout.setNeedDragOver(useDragOver);
                }
            }
        });
        final ImageView bg = findViewById(R.id.imageBg);
        nestedScrollParentLinearLayout.setHeaderScrollListener(new AdjustableHeaderLinearLayout.HeaderScrollListener() {
            @Override
            public void onScroll(int dy) {
                bg.setTranslationY(dy / 2f);
            }

            @Override
            public void onHeaderTotalHide() {
                Toast.makeText(NestedScrollActivity.this, "header hide", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHeaderTotalShow() {
                Toast.makeText(NestedScrollActivity.this, "header show", Toast.LENGTH_SHORT).show();
            }
        });
        nestedScrollParentLinearLayout.setMaxHeaderHeight(DimensionUtils.dpToPx(400));
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NestedScrollActivity.this, "I am textView", Toast.LENGTH_SHORT).show();
            }
        });
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewpager);

        String[] titles = new String[]{"最新", "热门", "我的"};
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(new TabFragment());
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setupWithViewPager(viewPager, false);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(titles[i]);
        }
    }

    private static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;

        public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}
