package com.bc.wechat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.R;
import com.bc.wechat.adapter.StatusGroupAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.StatusGroupDao;
import com.bc.wechat.entity.StatusGroup;
import com.bc.wechat.utils.CollectionUtils;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.CustomListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 状态
 *
 * @author zhou
 */
public class StatusActivity extends BaseActivity2 {

    @BindView(R.id.sv_bg)
    ScrollView mBgSv;

    @BindView(R.id.clv_user_status)
    CustomListView mUserStatusClv;

    StatusGroupAdapter mAdapter;

    List<StatusGroup> mStatusGroupList = new ArrayList<>();
    VolleyUtil mVolleyUtil;
    StatusGroupDao mStatusGroupDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar();
        setContentView(R.layout.activity_status);
        ButterKnife.bind(this);
        mBgSv.setFocusable(true);
        mBgSv.setFocusableInTouchMode(true);
        mBgSv.requestFocus();
        setRandomBg();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mStatusGroupDao = new StatusGroupDao();
        initData();
    }

    /**
     * 设置随机渐变色的背景
     */
    private void setRandomBg() {
        int random = new Random().nextInt(3);
        switch (random) {
            case 0:
                mBgSv.setBackgroundResource(R.drawable.bg_gradient_1);
                break;
            case 1:
                mBgSv.setBackgroundResource(R.drawable.bg_gradient_2);
                break;
            case 2:
                mBgSv.setBackgroundResource(R.drawable.bg_gradient_3);
                break;
        }
    }

    public void back(View view) {
        finish();
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
        mStatusGroupList = mStatusGroupDao.getStatusGroupList();
        mAdapter = new StatusGroupAdapter(this, R.layout.item_status_group, mStatusGroupList);
        mUserStatusClv.setAdapter(mAdapter);
        getStatusGroupList();
    }

    /**
     * 获取状态组列表
     */
    private void getStatusGroupList() {
        String url = Constant.BASE_URL + "statusGroup";
        mVolleyUtil.httpGetRequest(url, response -> {
            final List<StatusGroup> statusGroupList = JSONArray.parseArray(response, StatusGroup.class);
            if (!CollectionUtils.isEmpty(statusGroupList)) {
                // 持久化
                mStatusGroupDao.clearStatusGroup();
                for (StatusGroup statusGroup : statusGroupList) {
                    if (null != statusGroup) {
                        mStatusGroupDao.saveStatusGroup(statusGroup);
                    }
                }
            }
            mAdapter.setData(statusGroupList);
            mAdapter.notifyDataSetChanged();
        }, volleyError -> {
            final List<StatusGroup> statusGroupList = mStatusGroupDao.getStatusGroupList();
            mAdapter.setData(statusGroupList);
            mAdapter.notifyDataSetChanged();
        });
    }

}