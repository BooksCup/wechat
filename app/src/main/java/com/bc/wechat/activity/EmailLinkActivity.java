package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;

/**
 * 绑定邮箱地址
 *
 * @author zhou
 */
public class EmailLinkActivity extends BaseActivity {

    private EditText mEmailEt;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_email);
        initStatusBar();
        StatusBarUtil.setStatusBarColor(EmailLinkActivity.this, R.color.bottom_text_color_normal);

        mUser = PreferencesUtil.getInstance().getUser();

        initView();
    }

    private void initView() {
        mEmailEt = findViewById(R.id.et_email);

        mEmailEt.setText(mUser.getUserEmail());

        String isEmailLinked = mUser.getUserIsEmailLinked();
        if (Constant.EMAIL_NOT_VERIFIED.equals(isEmailLinked)) {
            // 未验证
        } else {
            // 已验证
            mEmailEt.setEnabled(false);
        }
    }

    public void back(View view) {
        finish();
    }
}
