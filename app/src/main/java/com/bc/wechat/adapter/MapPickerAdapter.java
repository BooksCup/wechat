package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.bc.wechat.R;

import java.util.List;

public class MapPickerAdapter extends BaseAdapter {

    private Context mContext;
    private List<PoiInfo> poiInfoList;

    private int notifyTip;

    public MapPickerAdapter(Context context, List<PoiInfo> poiInfoList) {
        this.mContext = context;
        this.poiInfoList = poiInfoList;
        // 默认第一个
        this.notifyTip = 0;
    }

    public void setNotifyTip(int notifyTip) {
        this.notifyTip = notifyTip;
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
            viewHolder.mNotifyTipIv = convertView.findViewById(R.id.iv_notify_tip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PoiInfo poiInfo = getItem(position);

        viewHolder.mNameTv.setText(poiInfo.getName());
        viewHolder.mAddressTv.setText(poiInfo.getAddress());
        if (position == notifyTip) {
            viewHolder.mNotifyTipIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mNotifyTipIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView mNameTv;
        TextView mAddressTv;
        ImageView mNotifyTipIv;
    }
}
