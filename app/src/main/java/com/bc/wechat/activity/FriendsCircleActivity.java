package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);

        List<FriendsCircle> list = new ArrayList<>();
        list.add(new FriendsCircle());
        list.add(new FriendsCircle());
        FriendsCircleAdapter adapter = new FriendsCircleAdapter(list, this);
        listView.setAdapter(adapter);
        listView.addHeaderView(headerView, null, false);
        listView.setHeaderDividersEnabled(false);

        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void back(View view) {
        finish();
    }

}
