package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Region;

import java.util.List;

public class RegionAdapter extends BaseAdapter {
    private Context mContext;
    private List<Region> mRegionList;

    public RegionAdapter(Context context, List<Region> regionList) {
        this.mContext = context;
        this.mRegionList = regionList;
    }

    @Override
    public int getCount() {
        return mRegionList.size();
    }

    @Override
    public Region getItem(int position) {
        return mRegionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Region region = mRegionList.get(position);

        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_region, null);
            viewHolder.mRegionTv = convertView.findViewById(R.id.tv_region);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mRegionTv.setText(region.getName());
        return convertView;
    }

    class ViewHolder {
        TextView mRegionTv;
    }
}
