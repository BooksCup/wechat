package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircle;

import java.util.List;

public class UserFriendsCircleAdapter extends BaseAdapter {
    private List<FriendsCircle> friendsCircleList;
    private Context mContext;

    public UserFriendsCircleAdapter(List<FriendsCircle> friendsCircleList, Context context) {
        this.friendsCircleList = friendsCircleList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return friendsCircleList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsCircleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_friends_circle, null);
        return convertView;
    }
}

