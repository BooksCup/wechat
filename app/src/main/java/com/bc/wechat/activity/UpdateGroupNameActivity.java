package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.bc.wechat.R;

public class UpdateGroupNameActivity extends FragmentActivity {
    String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_name);
        initView();
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
    }

    public void back(View view) {
        finish();
    }
}
