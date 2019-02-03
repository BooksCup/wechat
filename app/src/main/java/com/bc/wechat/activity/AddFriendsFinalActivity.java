package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;

public class AddFriendsFinalActivity extends FragmentActivity {
    private EditText mReasonEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_final);

        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mReasonEt = findViewById(R.id.et_reason);

        String nickName = PreferencesUtil.getInstance().getUserNickName();
        mReasonEt.setText("我是" + nickName);
    }

    public void back(View view) {
        finish();
    }
}
