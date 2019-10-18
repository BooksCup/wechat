package com.bc.wechat.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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
        final String nickName = getIntent().getStringExtra("nickName");
        final String avatar = getIntent().getStringExtra("avatar");
        final String sex = getIntent().getStringExtra("sex");
        final String isFriend = getIntent().getStringExtra("isFriend");
        final String lastestCirclePhotos = getIntent().getStringExtra("lastestCirclePhotos");

        if (!TextUtils.isEmpty(lastestCirclePhotos)) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(lastestCirclePhotos, String.class);
                if (null == circlePhotoList) {
                    circlePhotoList = new ArrayList<>();
                }
            } catch (Exception e) {
                circlePhotoList = new ArrayList<>();
            }
            loadCirclePhotos(circlePhotoList);
        }


        mNickNameTv.setText(nickName);
        if (null != avatar && !"".equals(avatar)) {
            mAvatarSdv.setImageURI(Uri.parse(avatar));
        }
        if (Constant.USER_SEX_MALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        mOperateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.IS_FRIEND.equals(isFriend)) {
                    // 是好友，发消息
                    Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                    intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                    intent.putExtra("fromUserId", userId);
                    intent.putExtra("fromUserNickName", nickName);
                    intent.putExtra("fromUserAvatar", avatar);
                    startActivity(intent);
                } else {
                    // 非好友，添加到通讯录
                    Intent intent = new Intent(UserInfoActivity.this, AddFriendsFinalActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }

            }
        });

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", avatar);
                startActivity(intent);
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void loadCirclePhotos(List<String> circlePhotoList) {
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
