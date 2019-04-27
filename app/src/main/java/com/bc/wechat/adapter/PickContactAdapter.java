package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Friend;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class PickContactAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private int resource;

    private List<Friend> friendList;
    private List<String> checkedUserIdList;
    String userId;

    public PickContactAdapter(Context context, int resource, List<Friend> friendList, List<String> checkedUserIdList, String userId) {
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.friendList = friendList;
        this.checkedUserIdList = checkedUserIdList;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Friend getItem(int position) {
        return friendList.get(position);
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
            viewHolder.mHeaderTv = convertView.findViewById(R.id.tv_header);
            viewHolder.mTempView = convertView.findViewById(R.id.view_temp);
            viewHolder.mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mPickFriendCb = convertView.findViewById(R.id.cb_pick_friend);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String header = friend.getUserHeader();
        if (0 == position || null != header && !header.equals(getItem(position - 1).getUserHeader())) {
            if ("".equals(header)) {
                viewHolder.mHeaderTv.setVisibility(View.GONE);
                viewHolder.mTempView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mHeaderTv.setVisibility(View.VISIBLE);
                viewHolder.mHeaderTv.setText(header);
                viewHolder.mTempView.setVisibility(View.GONE);
            }
        } else {
            viewHolder.mHeaderTv.setVisibility(View.GONE);
            viewHolder.mTempView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(friend.getUserAvatar())) {
            viewHolder.mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
        }
        viewHolder.mNickNameTv.setText(friend.getUserNickName());

        if (userId.equals(friend.getUserId())) {
            viewHolder.mPickFriendCb.setEnabled(false);
        } else {
            if (checkedUserIdList.contains(friend.getUserId())) {
                viewHolder.mPickFriendCb.setChecked(true);
            } else {
                viewHolder.mPickFriendCb.setChecked(false);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView mHeaderTv;
        View mTempView;
        TextView mNickNameTv;
        SimpleDraweeView mAvatarSdv;
        CheckBox mPickFriendCb;
    }
}
