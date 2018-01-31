package com.demo.playground.scrolllyric;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.demo.playground.MainActivity;
import com.demo.playground.R;
import com.demo.playground.lockscreen.LockScreenActivity;
import com.netease.cloudmusic.module.walle.ChannelUtil;

public class ScrollLyricActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_lyric);
        final LyricContainer lyricContainer = (LyricContainer) findViewById(R.id.lyricContainer);
        lyricContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyricContainer.setLyrics("text1", "text2", "text3", "newtest4fadsfadsfadsfasdfdfadfasdfasdfadsfadsfasdfadsfadsfasdf");
            }
        });
        TextView channelInfo = (TextView) findViewById(R.id.channel);
        channelInfo.setText(String.format("渠道：%s", ChannelUtil.getChannel(this)));
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, ScrollLyricActivity.class);
        context.startActivity(intent);
    }
}
