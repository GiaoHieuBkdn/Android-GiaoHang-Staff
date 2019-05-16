package com.bys.sangngoc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.bys.sangngoc.R;

/**
 * Created by Admin on 3/5/2018.
 */

public class SpinnerAdapter extends BaseAdapter {
    private ArrayList<String> mData;
    private Context mContext;
    private LayoutInflater inflter;

    public SpinnerAdapter(Context context, ArrayList<String> data) {
        this.mContext = context;
        this.mData = data;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.spinner_item, null);
        String item = mData.get(position);
        TextView names = (TextView) convertView.findViewById(R.id.tv_name);
        names.setText(item);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.spinner_item, null);
        String item = mData.get(position);
        TextView names = (TextView) convertView.findViewById(R.id.tv_name);
        names.setText(item);
        return convertView;
    }
}
