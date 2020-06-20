package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 修改手机号
 *
 * @author zhou
 */
public class ModifyPhoneActivity extends BaseActivity {

    private TextView mPhoneTv;
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
        mPhoneTv.setText("绑定的手机号：" + mUser.getUserPhone());
    }

    public void back() {
        finish();
    }
}
