package com.demo.playground.volley.toolbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.playground.R;
import com.demo.playground.volley.RequestQueue;
import com.demo.playground.volley.Response;
import com.demo.playground.volley.VolleyError;

public class VolleyTestActivity extends AppCompatActivity {
    TextView serverData;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_test);
        mQueue = Volley.newRequestQueue(this);

        Button fetchFromServer = (Button) findViewById(R.id.fetchData);
        fetchFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest("http://apis.juhe.cn/mobile/get?phone=18270837821&key=9a4329bdf84fa69d193ce601c22b949d",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                serverData.setText(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
                mQueue.add(stringRequest.setShouldCache(false));
            }
        });

        serverData = (TextView) findViewById(R.id.serverData);
    }
}
