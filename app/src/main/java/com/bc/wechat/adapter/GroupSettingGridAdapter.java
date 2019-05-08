package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class GroupSettingGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> userList;

    public GroupSettingGridAdapter(Context context, List<User> userList) {
        this.mContext = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size() + 2;
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_setting_gridview, null);
        SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
        TextView mNickNameTv = convertView.findViewById(R.id.tv_username);
        if (position == getCount() - 2) {
            // 加人
            mAvatarSdv.setImageResource(R.mipmap.jy_drltsz_btn_addperson);
            mNickNameTv.setVisibility(View.INVISIBLE);
        } else if (position == getCount() - 1) {
            // 减人
            mAvatarSdv.setImageResource(R.mipmap.icon_btn_deleteperson);
            mNickNameTv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
