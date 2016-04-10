package com.xy.weather.adapter;

import java.util.zip.Inflater;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xy.xyweather.R;

public class CityAdapter extends BaseAdapter {
    private Context context;
    private String[] citys;

    public CityAdapter(Context context, String[] citys) {
        this.context = context;
        this.citys = citys;
    }
    @Override
    public int getCount() {
        return citys.length;
    }

    @Override
    public Object getItem(int position) {
        return citys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.city_item, null);
            holder = new Holder();
            holder.city = (TextView) convertView.findViewById(R.id.city);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.city.setText(citys[position]);
        return convertView;
    }

    class Holder {
        private TextView city;
    }

}
