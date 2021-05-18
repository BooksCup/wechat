package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.NewFriendsAcceptConfirmActivity;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.FriendApply;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 好友申请消息
 *
 * @author zhou
 */
public class NewFriendsMsgAdapter extends BaseAdapter {

    Context mContext;
    List<FriendApply> mFriendApplyList;
    int mTotal;

    public NewFriendsMsgAdapter(Context context, List<FriendApply> friendApplyList) {
        this.mContext = context;
        this.mFriendApplyList = friendApplyList;
        mTotal = friendApplyList.size();
    }

    public void setData(List<FriendApply> friendApplyList) {
        this.mFriendApplyList = friendApplyList;
        mTotal = friendApplyList.size();
    }

    @Override
    public int getCount() {
        return mFriendApplyList.size();
    }

    @Override
    public FriendApply getItem(int position) {
        return mFriendApplyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendApply friendApply = mFriendApplyList.get(position);
        convertView = View.inflate(mContext, R.layout.item_new_friends_msg, null);

        TextView mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
        TextView mApplyReasonTv = convertView.findViewById(R.id.tv_apply_remark);
        SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
        Button mAddBtn = convertView.findViewById(R.id.btn_add);
        TextView mAddTv = convertView.findViewById(R.id.tv_added);

        mNickNameTv.setText(friendApply.getFromUserNickName());
        mApplyReasonTv.setText(friendApply.getApplyRemark());
        if (!TextUtils.isEmpty(friendApply.getFromUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friendApply.getFromUserAvatar()));
        }
        if (Constant.FRIEND_APPLY_STATUS_ACCEPT.equals(friendApply.getStatus())) {
            mAddTv.setVisibility(View.VISIBLE);
            mAddBtn.setVisibility(View.GONE);
        } else {
            mAddTv.setVisibility(View.GONE);
            mAddBtn.setVisibility(View.VISIBLE);
        }

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, NewFriendsAcceptConfirmActivity.class).
                        putExtra("applyId", friendApply.getApplyId()));
            }
        });
        return convertView;
    }

}
