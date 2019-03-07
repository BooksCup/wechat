package com.bc.wechat.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

public class MyUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mNickNameRl;
    private RelativeLayout mWxIdRl;
    private RelativeLayout mSexRl;

    private TextView mNickNameTv;
    private TextView mWxIdTv;
    private TextView mSexTv;
    private SimpleDraweeView mAvatarSdv;

    private VolleyUtil volleyUtil;

    private static final int UPDATE_USER_NICK_NAME = 3;
    private static final int UPDATE_USER_WX_ID = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        volleyUtil = VolleyUtil.getInstance(this);
        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mNickNameRl = findViewById(R.id.rl_nick_name);
        mNickNameTv = findViewById(R.id.tv_nick_name);

        mWxIdRl = findViewById(R.id.rl_wx_id);
        mWxIdTv = findViewById(R.id.tv_wx_id);

        mSexRl = findViewById(R.id.rl_sex);
        mSexTv = findViewById(R.id.tv_sex);

        mAvatarSdv = findViewById(R.id.sdv_avatar);

        mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
        mWxIdTv.setText(PreferencesUtil.getInstance().getUserWxId());
        String userAvatar = PreferencesUtil.getInstance().getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        }
        String userSex = PreferencesUtil.getInstance().getUserSex();
        if (Constant.USER_SEX_MALE.equals(userSex)) {
            mSexTv.setText("男");
        } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
            mSexTv.setText("女");
        }

        mNickNameRl.setOnClickListener(this);
        mWxIdRl.setOnClickListener(this);
        mSexRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_nick_name:
                // 昵称
                startActivityForResult(new Intent(this, UpdateNickNameActivity.class), UPDATE_USER_NICK_NAME);
                break;
            case R.id.rl_wx_id:
                startActivityForResult(new Intent(this, UpdateWxIdActivity.class), UPDATE_USER_WX_ID);
                break;
            case R.id.rl_sex:
                showSexDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_USER_NICK_NAME:
                    mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
                    break;
                case UPDATE_USER_WX_ID:
                    mWxIdTv.setText(PreferencesUtil.getInstance().getUserWxId());
                    break;
            }
        }
    }

    private void showSexDialog() {
        final AlertDialog sexDialog = new AlertDialog.Builder(this).create();
        sexDialog.show();
        Window window = sexDialog.getWindow();
        window.setContentView(R.layout.dialog_alert);
        LinearLayout mTitleLl = window.findViewById(R.id.ll_title);
        mTitleLl.setVisibility(View.VISIBLE);
        TextView mTitleTv = window.findViewById(R.id.tv_title);
        mTitleTv.setText("性别");
        TextView mMaleTv = window.findViewById(R.id.tv_content1);
        mMaleTv.setText("男");
        mMaleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSexTv.setText("男");
                updateUserSex(PreferencesUtil.getInstance().getUserId(), Constant.USER_SEX_MALE);
                sexDialog.dismiss();
            }
        });
        TextView mFemaleTv = window.findViewById(R.id.tv_content2);
        mFemaleTv.setText("女");
        mFemaleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSexTv.setText("女");
                updateUserSex(PreferencesUtil.getInstance().getUserId(), Constant.USER_SEX_FEMALE);
                sexDialog.dismiss();
            }
        });
    }

    private void updateUserSex(String userId, final String userSex) {
        String url = Constant.BASE_URL + "users/" + userId + "/userSex";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userSex", userSex);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                PreferencesUtil.getInstance().setUserSex(userSex);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError instanceof NetworkError) {
                    Toast.makeText(MyUserInfoActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
