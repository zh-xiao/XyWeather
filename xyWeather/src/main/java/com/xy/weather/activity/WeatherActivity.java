package com.xy.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xy.weather.adapter.WeatherAdapter;
import com.xy.weather.bean.Weather;
import com.xy.weather.listener.RequestListener;
import com.xy.weather.utils.HorizontalListView;
import com.xy.weather.utils.Location;
import com.xy.weather.utils.NetSpeedWatcher;
import com.xy.weather.utils.Request;
import com.xy.xyweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {
    private TextView weather;
    private TextView temperature;
    private TextView time;
    //    private TextView place;
    private TextView netSpeed;
    private TextView title;
    private Toolbar toolbar;
    LinearLayout weatherLayout, lodingLayout;
    ProgressBar loding;
    private static final String TAG = "WeatherActivity";
    private HorizontalListView weather_list;
    WeatherAdapter weatherAdapter;
    NetSpeedWatcher netSpeedWatcher = new NetSpeedWatcher();
    long waitTime = 2000;
    long touchTime = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG, msg.obj.toString());
                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    JSONObject today = result.getJSONObject("retData").getJSONObject("today");
                    title.setText(result.getJSONObject("retData").getString("city"));
                    time.setText(today.getString("date") + "    " + today.getString("week"));
                    weather.setText(today.getString("type"));
                    temperature.setText(today.getString("lowtemp") + "~" + today.getString("hightemp"));
                    ArrayList<Weather> weathers = new ArrayList<Weather>();
                    JSONArray futureWeathers = result.getJSONObject("retData").getJSONArray("forecast");
                    JSONArray historyWeathers = result.getJSONObject("retData").getJSONArray("history");
                    for (int i = historyWeathers.length()-1; i < historyWeathers.length(); i++) {
                        Weather weather = new Weather();
                        JSONObject object = (JSONObject) historyWeathers.get(i);
                        weather.setTime(object.getString("date")
                                + "\n" + object.getString("week"));
                        weather.setWeather(object.getString("type"));
                        weather.setTemperature(object.getString("lowtemp") + "~" + object.getString("hightemp"));
                        weathers.add(weather);
                    }
                    {
                        Weather weather = new Weather();
                        weather.setTime("今天");
                        weather.setWeather(today.getString("type"));
                        weather.setTemperature(today.getString("lowtemp") + "~" + today.getString("hightemp"));
                        weathers.add(weather);
                    }
                    for (int i = 0; i < futureWeathers.length(); i++) {
                        Weather weather = new Weather();
                        JSONObject object = (JSONObject) futureWeathers.get(i);
                        weather.setTime(object.getString("date")
                                + "\n" + object.getString("week"));
                        weather.setWeather(object.getString("type"));
                        weather.setTemperature(object.getString("lowtemp") + "~" + object.getString("hightemp"));
                        weathers.add(weather);
                    }
                    weatherAdapter = new WeatherAdapter(WeatherActivity.this, weathers);
                    weather_list.setAdapter(weatherAdapter);
                    hideLoding();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weather = (TextView) findViewById(R.id.weather);
        temperature = (TextView) findViewById(R.id.temperature);
        weather_list = (HorizontalListView) findViewById(R.id.weather_list);
        time = (TextView) findViewById(R.id.time);
//        place = (TextView) findViewById(R.id.place);
        weatherLayout = (LinearLayout) findViewById(R.id.weather_layout);
        loding = (ProgressBar) findViewById(R.id.loding);
        lodingLayout = (LinearLayout) findViewById(R.id.loding_layout);
        netSpeed = (TextView) findViewById(R.id.net_speed);
        title = (TextView) findViewById(R.id.title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (this.isTaskRoot()) {
            toolbar.setNavigationIcon(R.drawable.locate_indicator);
        } else {
            toolbar.setNavigationIcon(R.drawable.title_bar_back);
        }
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WeatherActivity.this.isTaskRoot()) {
                    startActivity(new Intent(WeatherActivity.this, SelectCityActivity.class));
                } else {
                    onBackPressed();
                }
                finish();
            }
        });
        netSpeedWatcher.getNetSpeedByPeriod(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111) {
                    netSpeed.setText(msg.obj.toString());
                }
            }
        }, 0, 1000);
        showLoding();
        String city = getIntent().getStringExtra("city");
        String id = getIntent().getStringExtra("id");
        String msg = getIntent().getStringExtra("msg");
        if ("locate".equals(msg)) {
            new Location(this).locate();
        }
        if (city != null) questWeatherByCity(city);
        if (id != null) {
            Request.request("http://apis.baidu.com/apistore/weatherservice/recentweathers", "cityid=" + id,
                    new RequestListener() {

                        @Override
                        public void setUI(String result) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    });
        }
    }

    public void questWeatherByCity(String city) {
        try {
            Request.request("http://apis.baidu.com/apistore/weatherservice/recentweathers", "cityname="
                    + URLEncoder.encode(city, "UTF-8"), new RequestListener() {

                @Override
                public void setUI(String result) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            });
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        netSpeedWatcher.cancle();
        super.onDestroy();
    }

    public void showLoding() {
        weatherLayout.setVisibility(View.GONE);
        lodingLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoding() {
        weatherLayout.setVisibility(View.VISIBLE);
        lodingLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode && this.isTaskRoot()) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                // 让Toast的显示时间和等待时间相同
                Toast.makeText(this, "再按一次退出", (int) waitTime).show();
                touchTime = currentTime;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.first:
                startActivity(new Intent(this, AboutActivity.class));
                break;
//            case R.id.second:
//                startActivity(new Intent(this, AboutActivity.class));
//                break;
            default:
                break;
        }
        return true;
    }

}
