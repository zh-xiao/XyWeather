package com.xy.weather.adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xy.weather.bean.Weather;
import com.xy.xyweather.R;

import java.util.ArrayList;

public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Weather> weathers;
    WindowManager wm;
    Display display;

    public WeatherAdapter(Context context, ArrayList<Weather> weathers) {
        this.context = context;
        this.weathers = weathers;
    }
    @Override
    public int getCount() {
        return weathers.size();
    }

    @Override
    public Object getItem(int position) {
        return weathers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.weather_item, null);
            holder = new Holder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.weather = (TextView) convertView.findViewById(R.id.weather);
            holder.temperature = (TextView) convertView.findViewById(R.id.temperature);
            setWidth(holder.time);
            setWidth(holder.weather);
            setWidth(holder.temperature);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.time.setText(weathers.get(position).getTime());
        holder.weather.setText(weathers.get(position).getWeather());
        holder.temperature.setText(weathers.get(position).getTemperature());
        return convertView;
    }

    public void setWidth(TextView textView) {
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.width = display.getWidth() / 4;
        textView.setLayoutParams(layoutParams);
    }

    class Holder {
        private TextView time;
        private TextView weather;
        private TextView temperature;
    }

}
