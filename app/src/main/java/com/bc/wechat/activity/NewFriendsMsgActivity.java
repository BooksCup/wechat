package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;

public class NewFriendsMsgActivity extends FragmentActivity {
    TextView mAddTv;
    TextView mSearchTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);
        initView();

        mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewFriendsMsgActivity.this, AddFriendsActivity.class));
            }
        });

        mSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewFriendsMsgActivity.this, AddFriendsBySearchActivity.class));
            }
        });
    }

    private void initView() {
        mAddTv = findViewById(R.id.tv_add);
        mSearchTv = findViewById(R.id.tv_search);
    }

    public void back(View view) {
        finish();
    }

}
