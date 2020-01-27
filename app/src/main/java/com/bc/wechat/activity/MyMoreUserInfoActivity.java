package com.bc.wechat.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

public class MyMoreUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mSexRl;
    private RelativeLayout mSignRl;

    private TextView mSexTv;
    private TextView mSignTv;

    private LoadingDialog mDialog;
    private VolleyUtil mVolleyUtil;
    private User mUser;

    private static final int UPDATE_USER_SIGN = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_more_info);

        mVolleyUtil = VolleyUtil.getInstance(this);
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(MyMoreUserInfoActivity.this);
        initView();
    }

    private void initView() {
        mSexRl = findViewById(R.id.rl_sex);
        mSexTv = findViewById(R.id.tv_sex);

        mSignRl = findViewById(R.id.rl_sign);
        mSignTv = findViewById(R.id.tv_sign);

        String userSex = mUser.getUserSex();

        if (Constant.USER_SEX_MALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_female));
        }
        mSignTv.setText(mUser.getUserSign());

        mSexRl.setOnClickListener(this);
        mSignRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_sex:
                showSexDialog();
                break;
            case R.id.rl_sign:
                // 签名
                startActivityForResult(new Intent(this, UpdateSignActivity.class), UPDATE_USER_SIGN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            final User user = PreferencesUtil.getInstance().getUser();
            switch (requestCode) {
                case UPDATE_USER_SIGN:
                    // 个性签名
                    mSignTv.setText(user.getUserSign());
                    break;
            }
        }
    }

    /**
     * 显示修改性别对话框
     */
    private void showSexDialog() {
        final AlertDialog sexDialog = new AlertDialog.Builder(this).create();
        sexDialog.show();
        Window window = sexDialog.getWindow();
        window.setContentView(R.layout.dialog_alert);
        LinearLayout mTitleLl = window.findViewById(R.id.ll_title);
        mTitleLl.setVisibility(View.VISIBLE);
        TextView mTitleTv = window.findViewById(R.id.tv_title);
        mTitleTv.setText(getString(R.string.sex));
        TextView mMaleTv = window.findViewById(R.id.tv_content1);
        mMaleTv.setText(getString(R.string.sex_male));
        mMaleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                updateUserSex(mUser.getUserId(), Constant.USER_SEX_MALE);
                sexDialog.dismiss();
            }
        });
        TextView mFemaleTv = window.findViewById(R.id.tv_content2);
        mFemaleTv.setText(getString(R.string.sex_female));
        mFemaleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                updateUserSex(mUser.getUserId(), Constant.USER_SEX_FEMALE);
                sexDialog.dismiss();
            }
        });
    }

    /**
     * 修改用户性别
     *
     * @param userId  用户ID
     * @param userSex 用户性别
     */
    private void updateUserSex(String userId, final String userSex) {
        String url = Constant.BASE_URL + "users/" + userId + "/userSex";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userSex", userSex);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mUser.setUserSex(userSex);
                PreferencesUtil.getInstance().setUser(mUser);
                if (Constant.USER_SEX_MALE.equals(userSex)) {
                    mSexTv.setText(getString(R.string.sex_male));
                } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
                    mSexTv.setText(getString(R.string.sex_female));
                }
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(MyMoreUserInfoActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(MyMoreUserInfoActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
