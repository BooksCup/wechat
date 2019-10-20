package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFriendsMsgAdapter extends BaseAdapter {
    Context mContext;
    List<FriendApply> friendApplyList;
    int total = 0;
    private VolleyUtil volleyUtil;

    public NewFriendsMsgAdapter(Context context, List<FriendApply> friendApplyList) {
        this.mContext = context;
        this.friendApplyList = friendApplyList;
        total = friendApplyList.size();
        volleyUtil = VolleyUtil.getInstance(context);
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
//                acceptFriendApply(friendApply);
            }
        });
        return convertView;
    }

    private void acceptFriendApply(final FriendApply friendApply) {
        String url = Constant.BASE_URL + "friendApplies";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyId", friendApply.getApplyId());

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mContext, "通过验证", Toast.LENGTH_SHORT).show();
                friendApply.setStatus(Constant.FRIEND_APPLY_STATUS_ACCEPT);
                FriendApply.save(friendApply);

                List<Friend> friendList = Friend.find(Friend.class, "user_id = ?", friendApply.getFromUserId());
                if (null != friendList && friendList.size() > 0) {
                    // 好友已存在，忽略
                } else {
                    // 不存在,插入sqlite
                    Friend friend = new Friend();
                    friend.setUserId(friendApply.getFromUserId());
                    friend.setUserNickName(friendApply.getFromUserNickName());
                    friend.setUserAvatar(friendApply.getFromUserAvatar());
                    friend.setUserHeader(CommonUtil.setUserHeader(friendApply.getFromUserNickName()));
                    friend.setUserSex(friendApply.getFromUserSex());
                    Friend.save(friend);
                }
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(mContext, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
