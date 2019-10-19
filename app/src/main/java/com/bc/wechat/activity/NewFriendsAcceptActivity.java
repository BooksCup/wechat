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

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept);
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

        final String nickName = getIntent().getStringExtra("nickName");
        final String avatar = getIntent().getStringExtra("avatar");
        final String sex = getIntent().getStringExtra("sex");
        final String sign = getIntent().getStringExtra("sign");
        final String circlePhotos = getIntent().getStringExtra("circlePhotos");

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

        mSignTv.setText(sign);

        if (!TextUtils.isEmpty(circlePhotos)) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(circlePhotos, String.class);
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
}
