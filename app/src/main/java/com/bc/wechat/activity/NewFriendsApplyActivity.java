package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;

import com.bc.wechat.R;

/**
 * 申请添加朋友
 *
 * @author zhou
 */
public class NewFriendsApplyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_apply);
    }

    public void back(View view) {
        finish();
    }
}
