package com.xy.weather.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xy.weather.adapter.CityAdapter;
import com.xy.weather.adapter.SearchAdapter;
import com.xy.weather.bean.City;
import com.xy.weather.listener.RequestListener;
import com.xy.weather.utils.Request;
import com.xy.xyweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SelectCityActivity extends BaseActivity {
    private EditText city_name;
    private GridView city_list;
    private String[] citys;
    private LinearLayout hot_citys_layout;
    private ListView search_citys;
    long waitTime = 2000;
    long touchTime = 0;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 1:
                if (city_name.getText().length() > 1) {

                    // 隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(city_name.getWindowToken(), 0);
                }

                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    JSONArray cityArray = result.getJSONArray("retData");
                    final ArrayList<City> cities = new Gson().fromJson(cityArray.toString(),
                            new TypeToken<ArrayList<City>>() {
                            }.getType());
                    // final ArrayList<City> cities = new ArrayList<City>();
                    // for (int i = 0; i < cityArray.length(); i++) {
                    // City city = new City();
                    // JSONObject object = (JSONObject) cityArray.get(i);
                    // city.setProvince_cn(object.getString("province_cn"));
                    // city.setDistrict_cn(object.getString("district_cn"));
                    // city.setName_cn(object.getString("name_cn"));
                    // city.setArea_id(object.getString("area_id"));
                    // cities.add(city);
                    // }
                        search_citys.setAdapter(new SearchAdapter(SelectCityActivity.this, cities));
                        hot_citys_layout.setVisibility(View.GONE);
                        search_citys.setVisibility(View.VISIBLE);
                        search_citys.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Intent intent = new Intent(SelectCityActivity.this, WeatherActivity.class);
                                intent.putExtra("id", cities.get(arg2).getArea_id());
                                startActivity(intent);
                                city_name.setText("");
                            }
                        });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        city_name = (EditText) findViewById(R.id.city_name);
        city_list = (GridView) findViewById(R.id.city_list);
        hot_citys_layout = (LinearLayout) findViewById(R.id.hot_citys_layout);
        search_citys = (ListView) findViewById(R.id.search_citys);
        city_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Request.request("http://apis.baidu.com/apistore/weatherservice/citylist",
                            "cityname=" + URLEncoder.encode(s.toString(), "UTF-8"), new RequestListener() {

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
        });
        citys = getResources().getStringArray(R.array.citys);
        city_list.setAdapter(new CityAdapter(SelectCityActivity.this, citys));
        city_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(SelectCityActivity.this, WeatherActivity.class);
                if (citys[arg2].equals("自动定位")) {
                    intent.putExtra("msg", "locate");
                    startActivity(intent);
                } else {
                    intent.putExtra("city", citys[arg2]);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hot_citys_layout.setVisibility(View.VISIBLE);
        search_citys.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
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
}
