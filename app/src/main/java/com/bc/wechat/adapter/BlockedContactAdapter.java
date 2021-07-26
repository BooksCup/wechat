package com.bc.wechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;

import java.util.List;

/**
 * 通讯录黑名单
 *
 * @author zhou
 */
public class BlockedContactAdapter extends BaseAdapter {

    Context mContext;
    List<User> mBlockedContactList;

    public BlockedContactAdapter(Context context, List<User> blockedContactList) {
        this.mContext = context;
        this.mBlockedContactList = blockedContactList;
    }

    public void setData(List<User> blockedContactList) {
        this.mBlockedContactList = blockedContactList;
    }

    @Override
    public int getCount() {
        return mBlockedContactList.size();
    }

    @Override
    public User getItem(int position) {
        return mBlockedContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.item_blocked_contact, null);
        return convertView;
    }

}