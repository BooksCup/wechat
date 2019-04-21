package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Friend;
import com.facebook.drawee.view.SimpleDraweeView;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = friendList.get(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(resource, null);
            viewHolder.mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(friend.getUserAvatar())) {
            viewHolder.mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
        }
        viewHolder.mNickNameTv.setText(friend.getUserNickName());

        return convertView;
    }

    class ViewHolder {
        TextView mNickNameTv;
        SimpleDraweeView mAvatarSdv;
    }
}
