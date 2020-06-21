package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 绑定手机号
 *
 * @author zhou
 */
public class ModifyPhoneActivity extends BaseActivity implements View.OnClickListener {

    private TextView mPhoneTv;
    private Button mMobileContactsBtn;
    private Button mChangeMobileBtn;

    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();

        initView();
    }

    private void initView() {
        mPhoneTv = findViewById(R.id.tv_phone);
        mMobileContactsBtn = findViewById(R.id.btn_mobile_contacts);
        mChangeMobileBtn = findViewById(R.id.btn_change_mobile);

        mMobileContactsBtn.setOnClickListener(this);
        mChangeMobileBtn.setOnClickListener(this);
        mPhoneTv.setText("绑定的手机号：" + mUser.getUserPhone());
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mobile_contacts:
                startActivity(new Intent(ModifyPhoneActivity.this, PhoneContactActivity.class));
                break;
            case R.id.btn_change_mobile:
                break;
        }
    }
}
