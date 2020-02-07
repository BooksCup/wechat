package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Address;

import java.util.List;

public class MyAddressAdapter extends BaseAdapter {

    private Context mContext;
    private List<Address> mAddressList;

    public MyAddressAdapter(Context context, List<Address> addressList) {
        this.mContext = context;
        this.mAddressList = addressList;
    }

    public void setData(List<Address> dataList) {
        this.mAddressList = dataList;
    }

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public Address getItem(int position) {
        return mAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Address address = mAddressList.get(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_address, null);
            viewHolder.mUserInfoTv = convertView.findViewById(R.id.tv_user_info);
            viewHolder.mAddressInfoTv = convertView.findViewById(R.id.tv_address_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StringBuffer userInfoBuffer = new StringBuffer();
        userInfoBuffer.append(address.getAddressName()).append("，").append(address.getAddressPhone());
        StringBuffer addressInfoBuffer = new StringBuffer();
        addressInfoBuffer.append(address.getAddressProvince()).append(" ")
                .append(address.getAddressCity()).append(" ")
                .append(address.getAddressDistrict()).append(" ")
                .append(address.getAddressDetail());
        viewHolder.mUserInfoTv.setText(userInfoBuffer.toString());
        viewHolder.mAddressInfoTv.setText(addressInfoBuffer.toString());

        return convertView;
    }

    class ViewHolder {
        TextView mUserInfoTv;
        TextView mAddressInfoTv;
    }
}
