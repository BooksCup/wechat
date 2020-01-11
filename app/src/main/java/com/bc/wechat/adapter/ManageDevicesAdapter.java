package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.DeviceInfo;

import java.util.List;

/**
 * 登录设备管理
 *
 * @author zhou
 */
public class ManageDevicesAdapter extends BaseAdapter {
    private Context mContext;
    private List<DeviceInfo> mLoginDeviceList;

    public ManageDevicesAdapter(Context context, List<DeviceInfo> loginDeviceList) {
        this.mContext = context;
        this.mLoginDeviceList = loginDeviceList;
    }

    @Override
    public int getCount() {
        return mLoginDeviceList.size();
    }

    @Override
    public DeviceInfo getItem(int position) {
        return mLoginDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder {
    }
}
