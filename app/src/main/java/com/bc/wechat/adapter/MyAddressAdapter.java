package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_address, null);
        return convertView;
    }

}
