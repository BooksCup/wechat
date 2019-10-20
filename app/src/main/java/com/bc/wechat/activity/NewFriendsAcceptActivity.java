package com.bc.wechat.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendApplyDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFriendsAcceptActivity extends Activity {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private TextView mSignTv;
    private ImageView mSexIv;
    private RelativeLayout mAcceptRl;

    // 朋友圈图片
    private SimpleDraweeView mCirclePhoto1Sdv;
    private SimpleDraweeView mCirclePhoto2Sdv;
    private SimpleDraweeView mCirclePhoto3Sdv;
    private SimpleDraweeView mCirclePhoto4Sdv;

    private VolleyUtil volleyUtil;
    private FriendApplyDao friendApplyDao;
    private FriendApply friendApply;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept);
        volleyUtil = VolleyUtil.getInstance(this);
        friendApplyDao = new FriendApplyDao();
        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);
        mSignTv = findViewById(R.id.tv_sign);

        mCirclePhoto1Sdv = findViewById(R.id.sdv_circle_photo_1);
        mCirclePhoto2Sdv = findViewById(R.id.sdv_circle_photo_2);
        mCirclePhoto3Sdv = findViewById(R.id.sdv_circle_photo_3);
        mCirclePhoto4Sdv = findViewById(R.id.sdv_circle_photo_4);

        mAcceptRl = findViewById(R.id.rl_accept);

        final String applyId = getIntent().getStringExtra("applyId");
        friendApply = friendApplyDao.getFriendApplyByApplyId(applyId);

        mNickNameTv.setText(friendApply.getFromUserNickName());
        if (!TextUtils.isEmpty(friendApply.getFromUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friendApply.getFromUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(friendApply.getFromUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(friendApply.getFromUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        mSignTv.setText(friendApply.getFromUserSign());

        if (!TextUtils.isEmpty(friendApply.getFromUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(friendApply.getFromUserLastestCirclePhotos(), String.class);
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
    }

    public void back(View view) {
        finish();
    }

    private void acceptFriendApply(String applyId) {
        String url = Constant.BASE_URL + "friendApplies";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyId", applyId);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(NewFriendsAcceptActivity.this, "通过验证", Toast.LENGTH_SHORT).show();
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

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(NewFriendsAcceptActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
