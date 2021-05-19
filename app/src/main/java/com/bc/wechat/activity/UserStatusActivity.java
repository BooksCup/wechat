package com.bc.wechat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bc.wechat.R;
import com.bc.wechat.adapter.StatusGroupAdapter;
import com.bc.wechat.entity.UserStatus;
import com.bc.wechat.entity.UserStatusGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar();
        setContentView(R.layout.activity_user_status);
        ButterKnife.bind(this);
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
        List<UserStatusGroup> userStatusGroupList = new ArrayList<>();
        UserStatusGroup userStatusGroup1 = new UserStatusGroup();
        userStatusGroup1.setName("心情想法");
        List<UserStatus> list1 = new ArrayList<>();
        UserStatus userStatus1 = new UserStatus();
        userStatus1.setName("美滋滋");
        userStatus1.setIcon("icon_chat_collect");
        UserStatus userStatus2 = new UserStatus();
        userStatus2.setName("裂开");
        userStatus2.setIcon("icon_chat_location");
        list1.add(userStatus1);
        list1.add(userStatus2);
        userStatusGroup1.setUserStatusList(list1);

        UserStatusGroup userStatusGroup2 = new UserStatusGroup();
        userStatusGroup2.setName("工作学习");
        List<UserStatus> list2 = new ArrayList<>();
        UserStatus userStatus3 = new UserStatus();
        userStatus3.setName("搬砖");
        userStatus3.setIcon("icon_chat_collect");
        UserStatus userStatus4 = new UserStatus();
        userStatus4.setName("沉迷学习");
        userStatus4.setIcon("icon_chat_location");
        list2.add(userStatus3);
        list2.add(userStatus4);
        userStatusGroup2.setUserStatusList(list2);

        UserStatusGroup userStatusGroup3 = new UserStatusGroup();
        userStatusGroup3.setName("其他");
        List<UserStatus> list3 = new ArrayList<>();
        UserStatus userStatus5 = new UserStatus();
        userStatus5.setName("(未知)");
        userStatus5.setIcon("icon_status_mysterious");
        list3.add(userStatus5);
        userStatusGroup3.setUserStatusList(list3);

        userStatusGroupList.add(userStatusGroup3);

        mAdapter = new StatusGroupAdapter(this, R.layout.item_user_status_group, userStatusGroupList);
        mUserStatusClv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}