package com.bc.wechat.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.utils.WechatBeanUtil;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends Activity {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private ImageView mSexIv;

    // 操作按钮  根据是否好友关系分为如下两种
    // 是好友: 发送消息
    // 非好友: 添加到通讯录
    private RelativeLayout mOperateRl;

    // 朋友圈图片
    private SimpleDraweeView mCirclePhoto1Sdv;
    private SimpleDraweeView mCirclePhoto2Sdv;
    private SimpleDraweeView mCirclePhoto3Sdv;
    private SimpleDraweeView mCirclePhoto4Sdv;

    private VolleyUtil volleyUtil;
    private FriendDao friendDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        volleyUtil = VolleyUtil.getInstance(this);
        friendDao = new FriendDao();
        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);
        mOperateRl = findViewById(R.id.rl_operate);

        mCirclePhoto1Sdv = findViewById(R.id.sdv_circle_photo_1);
        mCirclePhoto2Sdv = findViewById(R.id.sdv_circle_photo_2);
        mCirclePhoto3Sdv = findViewById(R.id.sdv_circle_photo_3);
        mCirclePhoto4Sdv = findViewById(R.id.sdv_circle_photo_4);

        final String userId = getIntent().getStringExtra("userId");

        final Friend friend = friendDao.getFriendById(userId);
        loadData(friend);

        getUserFromServer(userId);

        mOperateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("fromUserId", userId);
                intent.putExtra("fromUserNickName", friend.getUserNickName());
                intent.putExtra("fromUserAvatar", friend.getUserAvatar());
                startActivity(intent);
            }
        });

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", friend.getUserAvatar());
                startActivity(intent);
            }
        });
    }

    public void back(View view) {
        finish();
    }

    // 渲染数据
    private void loadData(Friend friend) {
        if (!TextUtils.isEmpty(friend.getUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(friend.getUserLastestCirclePhotos(), String.class);
                if (null == circlePhotoList) {
                    circlePhotoList = new ArrayList<>();
                }
            } catch (Exception e) {
                circlePhotoList = new ArrayList<>();
            }
            switch (circlePhotoList.size()) {
                case 1:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    break;
                case 2:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    break;
                case 3:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    break;
                case 4:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    mCirclePhoto4Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto4Sdv.setImageURI(Uri.parse(circlePhotoList.get(3)));
                    break;
                default:
                    break;
            }
        }

        mNickNameTv.setText(friend.getUserNickName());
        if (null != friend.getUserAvatar() && !"".equals(friend.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(friend.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(friend.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }
    }

    public void getUserFromServer(final String userId) {
        String url = Constant.BASE_URL + "users/" + userId;

        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = JSON.parseObject(response, User.class);
                friendDao.saveFriendByUserInfo(user);
                Friend friend = WechatBeanUtil.transferUserToFriend(user);
                loadData(friend);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

}
