package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bc.wechat.R;

public class AddFriendsActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mRadarRl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initView();
    }

    private void initView() {
        mRadarRl = findViewById(R.id.rl_radar);

        mRadarRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_radar:
                startActivity(new Intent(this, AddFriendsByRadarActivity.class));
                break;
        }
    }
}
