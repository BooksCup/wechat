package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baidu.mapapi.search.core.PoiInfo;
import com.bc.wechat.R;

import java.util.List;

public class MapPickerAdapter extends BaseAdapter {

    private Context mContext;
    private List<PoiInfo> poiInfoList;


    public MapPickerAdapter(Context context, List<PoiInfo> poiInfoList) {
        this.mContext = context;
        this.poiInfoList = poiInfoList;
    }

    @Override
    public int getCount() {
        return poiInfoList.size();
    }

    @Override
    public PoiInfo getItem(int position) {
        return poiInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.picker_item_place, null);
        return convertView;
    }

    class ViewHolder {

    }
}
