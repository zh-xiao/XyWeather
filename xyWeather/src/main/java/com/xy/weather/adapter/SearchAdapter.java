package com.xy.weather.adapter;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.xy.weather.bean.City;
import com.xy.xyweather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<City> citys;

    public SearchAdapter(Context context, ArrayList<City> citys) {
        this.context = context;
        this.citys = citys;
    }
    @Override
    public int getCount() {
        return citys.size();
    }

    @Override
    public Object getItem(int position) {
        return citys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
            holder = new Holder();
            holder.city = (TextView) convertView.findViewById(R.id.city);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.city.setText(citys.get(position).getProvince_cn() + "    " + citys.get(position).getDistrict_cn()
                + "    " + citys.get(position).getName_cn());
        return convertView;
    }

    class Holder {
        private TextView city;
    }

}
