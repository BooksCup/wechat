package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {
    TextView mAgreementTv;
    EditText mNickNameEt;
    EditText mPhoneEt;
    EditText mPasswordEt;

    ImageView mHidePasswordIv;
    ImageView mShowPasswordIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mNickNameEt = findViewById(R.id.et_nick_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mPasswordEt = findViewById(R.id.et_password);
        mAgreementTv = findViewById(R.id.tv_agreement);

        mHidePasswordIv = findViewById(R.id.iv_password_hide);
        mShowPasswordIv = findViewById(R.id.iv_password_show);

        String agreement = "<font color=" + "\"" + "#AAAAAA" + "\">" + "点击上面的"
                + "\"" + "注册" + "\"" + "按钮,即表示你同意" + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《腾讯微信软件许可及服务协议》"
                + "</font>" + "</u>";
        mAgreementTv.setText(Html.fromHtml(agreement));

        mHidePasswordIv.setOnClickListener(this);
        mShowPasswordIv.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_password_hide:
                // 点击显示密码
                mHidePasswordIv.setVisibility(View.GONE);
                mShowPasswordIv.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_password_show:
                mHidePasswordIv.setVisibility(View.VISIBLE);
                mShowPasswordIv.setVisibility(View.GONE);
                break;
        }
    }
}
