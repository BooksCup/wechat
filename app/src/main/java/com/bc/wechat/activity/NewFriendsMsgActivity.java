package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.NewFriendsMsgAdapter;
import com.bc.wechat.entity.FriendApply;

import java.util.List;

public class NewFriendsMsgActivity extends FragmentActivity {
    TextView mAddTv;
    TextView mSearchTv;
    ListView mNewFriendsMsgLv;
    NewFriendsMsgAdapter newFriendsMsgAdapter;

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

        List<FriendApply> friendApplyList = FriendApply.listAll(FriendApply.class);


        newFriendsMsgAdapter = new NewFriendsMsgAdapter(this, friendApplyList);
        mNewFriendsMsgLv.setAdapter(newFriendsMsgAdapter);
    }

    private void initView() {
        mAddTv = findViewById(R.id.tv_add);
        mSearchTv = findViewById(R.id.tv_search);
        mNewFriendsMsgLv = findViewById(R.id.lv_new_friends_msg);
    }

    public void back(View view) {
        finish();
    }

}
