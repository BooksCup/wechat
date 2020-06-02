package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 账号与安全
 *
 * @author zhou
 */
public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 标题
     */
    private TextView mTitleTv;

    private TextView mWeChatIdTv;
    private TextView mPhoneTv;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mWeChatIdTv = findViewById(R.id.tv_wechat_id);
        mPhoneTv = findViewById(R.id.tv_phone);

        mWeChatIdTv.setText(mUser.getUserWxId());
        mPhoneTv.setText(mUser.getUserPhone());
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
}
