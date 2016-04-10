package com.xy.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xy.xyweather.R;


public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent=new Intent(WelcomeActivity.this,WeatherActivity.class);
                intent.putExtra("msg","locate");
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}
