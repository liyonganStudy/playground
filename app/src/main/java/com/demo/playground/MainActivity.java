package com.demo.playground;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listView);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            List<String> activityNames = new ArrayList<>();
            for (ActivityInfo activityInfo : packageInfo.activities) {
                if (!activityInfo.name.contains(getClass().getName())) {
                    activityNames.add(activityInfo.name);
                }
            }
            listView.setAdapter(new ActivityAdapter(activityNames, MainActivity.this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    class ActivityAdapter extends BaseAdapter {

        private List<String> mActivityNames;
        private Context mContext;

        ActivityAdapter(List<String> activityNames, Context context) {
            mActivityNames = activityNames;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mActivityNames.size();
        }

        @Override
        public String getItem(int position) {
            return mActivityNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_item, parent, false);
            }
            final String activityName = getItem(position);
            Button button = ((Button) convertView.findViewById(R.id.openActivity));
            button.setText(mContext.getString(R.string.openActivity, activityName.substring(activityName.lastIndexOf(".") + 1)));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(mContext, activityName));
                    mContext.startActivity(intent);
                }
            });
            return convertView;
        }
    }
}
