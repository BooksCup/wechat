package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.PickContactAdapter;
import com.bc.wechat.entity.Friend;

import java.util.List;

public class CreateChatRoomActivity extends FragmentActivity {

    private PickContactAdapter contactAdapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        List<Friend> friendList = Friend.listAll(Friend.class);
        friendList.add(new Friend());
        friendList.add(new Friend());
        listView = findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this,
                R.layout.item_pick_contact_list, friendList);
        listView.setAdapter(contactAdapter);

    }

    public void back(View view) {
        finish();
    }
}
