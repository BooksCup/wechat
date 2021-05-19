package com.bc.wechat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.StatusGroupAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.StatusGroup;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.CustomListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户状态
 *
 * @author
 */
public class UserStatusActivity extends BaseActivity {

    @BindView(R.id.clv_user_status)
    CustomListView mUserStatusClv;

    StatusGroupAdapter mAdapter;

    List<StatusGroup> mUserStatusGroupList = new ArrayList<>();
    VolleyUtil mVolleyUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar();
        setContentView(R.layout.activity_user_status);
        ButterKnife.bind(this);
        mVolleyUtil = VolleyUtil.getInstance(this);
        initData();
    }

    /**
     * 透明化状态栏
     */
    private void transparentStatusBar() {
        Window window = getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void initData() {
        mAdapter = new StatusGroupAdapter(this, R.layout.item_user_status_group, mUserStatusGroupList);
        mUserStatusClv.setAdapter(mAdapter);
        getUserStatusGroupList();
    }

    private void getUserStatusGroupList() {
        String url = Constant.BASE_URL + "statusGroup";
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final List<StatusGroup> userStatusGroupList = JSONArray.parseArray(response, StatusGroup.class);
                mAdapter.setData(userStatusGroupList);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

}