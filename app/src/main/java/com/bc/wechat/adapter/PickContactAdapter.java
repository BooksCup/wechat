package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.entity.Friend;

import java.util.List;

public class PickContactAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private int resource;

    private List<Friend> friendList;

    public PickContactAdapter(Context context, int resource, List<Friend> friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.friendList = friendList;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(resource, null);
        return convertView;
    }
}
