package com.bc.wechat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public void setData(List<DeviceInfo> dataList) {
        this.mLoginDeviceList = dataList;
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
        DeviceInfo deviceInfo = mLoginDeviceList.get(position);

        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices, null);
            viewHolder.mPhoneModelTv = convertView.findViewById(R.id.tv_phone_model);
            viewHolder.mLoginTimeTv = convertView.findViewById(R.id.tv_login_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mPhoneModelTv.setText(deviceInfo.getPhoneBrand() + "-" + deviceInfo.getPhoneModel());
        if (!TextUtils.isEmpty(deviceInfo.getLoginTime())) {
            viewHolder.mLoginTimeTv.setText(deviceInfo.getLoginTime().substring(2));
        }
        return convertView;
    }

    class ViewHolder {
        TextView mPhoneModelTv;
        TextView mLoginTimeTv;
    }
}
