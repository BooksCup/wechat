package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pick_place, null);

            viewHolder = new ViewHolder();
            viewHolder.mNameTv = convertView.findViewById(R.id.tv_name);
            viewHolder.mAddressTv = convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PoiInfo poiInfo = getItem(position);

        viewHolder.mNameTv.setText(poiInfo.getName());
        viewHolder.mAddressTv.setText(poiInfo.getAddress());
        return convertView;
    }

    class ViewHolder {
        TextView mNameTv;
        TextView mAddressTv;
    }
}
