package com.bc.wechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bc.wechat.R;
import com.bc.wechat.adapter.NewFriendsMsgAdapter;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.utils.ExampleUtil;

import java.util.List;


public class NewFriendsMsgActivity extends FragmentActivity {
    TextView mAddTv;
    TextView mSearchTv;
    ListView mNewFriendsMsgLv;
    NewFriendsMsgAdapter newFriendsMsgAdapter;
    private MessageReceiver mMessageReceiver;

    public static boolean isForeground = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);
        registerMessageReceiver();
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

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_NEW_FRIENDS_MSG);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_NEW_FRIENDS_MSG.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(MainActivity.KEY_MESSAGE);
                    String extras = intent.getStringExtra(MainActivity.KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(MainActivity.KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(MainActivity.KEY_EXTRAS + " : " + extras + "\n");
                    }
                    List<FriendApply> friendApplyList = FriendApply.listAll(FriendApply.class);
                    newFriendsMsgAdapter.setData(friendApplyList);
                    newFriendsMsgAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
        }
    }

}
