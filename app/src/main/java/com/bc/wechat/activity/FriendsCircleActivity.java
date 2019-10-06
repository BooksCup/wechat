package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.FriendsCircleAdapter;
import com.bc.wechat.entity.FriendsCircle;

import java.util.ArrayList;
import java.util.List;

public class FriendsCircleActivity extends FragmentActivity {

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle);
        listView = findViewById(R.id.ll_friends_circle);
        List<FriendsCircle> list = new ArrayList<>();
        list.add(new FriendsCircle());
        list.add(new FriendsCircle());
        FriendsCircleAdapter adapter = new FriendsCircleAdapter(list, this);
        listView.setAdapter(adapter);
    }

}
