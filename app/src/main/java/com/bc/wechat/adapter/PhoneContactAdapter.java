package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;

import java.util.List;

public class PhoneContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> mUserList;

    public PhoneContactAdapter(Context context, List<User> userList) {
        this.mContext = context;
        this.mUserList = userList;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public User getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_phone_contact, null);
    }
}
