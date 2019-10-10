package com.bc.wechat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.FriendsCircleAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class FriendsCircleActivity extends FragmentActivity {

    private ListView listView;
    private User user;
    private VolleyUtil volleyUtil;
    private List<FriendsCircle> friendsCircleList = new ArrayList<>();
    FriendsCircleAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(this);
        listView = findViewById(R.id.ll_friends_circle);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);

        friendsCircleList.add(new FriendsCircle());
        friendsCircleList.add(new FriendsCircle());
        mAdapter = new FriendsCircleAdapter(friendsCircleList, this);
        listView.setAdapter(mAdapter);
        listView.addHeaderView(headerView, null, false);
        listView.setHeaderDividersEnabled(false);

        // headerView
        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        TextView mNickNameTv = headerView.findViewById(R.id.tv_nick_name);
        mNickNameTv.setText(user.getUserNickName());

        SimpleDraweeView mAvatarSdv = headerView.findViewById(R.id.sdv_avatar);
        mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));

        getFriendsCircleList(user.getUserId());
    }

    public void back(View view) {
        finish();
    }

    private void getFriendsCircleList(String userId) {
        String url = Constant.BASE_URL + "friendsCircle?userId=" + userId;

        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<FriendsCircle> list = JSONArray.parseArray(response, FriendsCircle.class);
                friendsCircleList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(FriendsCircleActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(FriendsCircleActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

}
