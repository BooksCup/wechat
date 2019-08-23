package com.bc.wechat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;

import java.util.List;

public class FriendsAdapter extends ArrayAdapter<User> {

    List<User> userList;
    int resource;
    private LayoutInflater inflater;

    public FriendsAdapter(Context context, int resource, List<User> userList) {
        super(context, resource, userList);
        this.resource = resource;
        this.userList = userList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView){
            convertView = inflater.inflate(resource, null);
        }
        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
