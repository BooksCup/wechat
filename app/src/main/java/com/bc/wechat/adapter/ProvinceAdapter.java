package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.Area;

import java.util.List;

public class ProvinceAdapter extends BaseAdapter {
    private Context mContext;
    private List<Area> mProvinceList;

    public ProvinceAdapter(Context context, List<Area> provinceList) {
        this.mContext = context;
        this.mProvinceList = provinceList;
    }

    @Override
    public int getCount() {
        return mProvinceList.size();
    }

    @Override
    public Area getItem(int position) {
        return mProvinceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_province, null);
    }
}
