package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendApply;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NewFriendsMsgAdapter extends BaseAdapter {
    Context mContext;
    List<FriendApply> friendApplyList;
    int total = 0;

    public NewFriendsMsgAdapter(Context context, List<FriendApply> friendApplyList) {
        this.mContext = context;
        this.friendApplyList = friendApplyList;
        total = friendApplyList.size();
    }

    public void setData(List<FriendApply> friendApplyList) {
        this.friendApplyList = friendApplyList;
        total = friendApplyList.size();
    }

    @Override
    public int getCount() {
        return friendApplyList.size();
    }

    @Override
    public FriendApply getItem(int position) {
        return friendApplyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendApply friendApply = friendApplyList.get(position);
        convertView = View.inflate(mContext, R.layout.item_new_friends_msg, null);

        TextView mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
        TextView mApplyReasonTv = convertView.findViewById(R.id.tv_apply_remark);
        SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);

        mNickNameTv.setText(friendApply.getFromUserNickName());
        mApplyReasonTv.setText(friendApply.getApplyRemark());
        if (null != friendApply.getFromUserAvatar() && !"".equals(friendApply.getFromUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friendApply.getFromUserAvatar()));
        }
        return convertView;
    }
}
