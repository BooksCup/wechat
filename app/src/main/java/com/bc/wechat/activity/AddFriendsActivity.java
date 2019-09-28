package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

public class AddFriendsActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mSearchRl;

    private RelativeLayout mRadarRl;
    private TextView mWxIdTv;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        user = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mSearchRl = findViewById(R.id.rl_search);
        mRadarRl = findViewById(R.id.rl_radar);
        mWxIdTv = findViewById(R.id.tv_wx_id);

        mWxIdTv.setText("我的微信号：" + user.getUserWxId());
        mSearchRl.setOnClickListener(this);
        mRadarRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                startActivity(new Intent(this, AddFriendsBySearchActivity.class));
                break;

            case R.id.rl_radar:
                startActivity(new Intent(this, AddFriendsByRadarActivity.class));
                break;
        }
    }
}
