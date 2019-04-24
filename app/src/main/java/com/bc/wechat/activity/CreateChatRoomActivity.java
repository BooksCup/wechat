package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.PickContactAdapter;
import com.bc.wechat.entity.Friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreateChatRoomActivity extends FragmentActivity {

    private PickContactAdapter contactAdapter;
    private ListView listView;
    String userId;

    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;
    private ImageView mSearchIv;

    private List<String> checkedUserIdList = new ArrayList<>();
    private int totalCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        userId = getIntent().getStringExtra("userId");
        final List<Friend> friendList = Friend.listAll(Friend.class);
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        menuLinerLayout = findViewById(R.id.linearLayoutMenu);
        mSearchIv = findViewById(R.id.iv_search);

        listView = findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this,
                R.layout.item_pick_contact_list, friendList, userId);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend friend = friendList.get(position);
                CheckBox mPickFriendCb = view.findViewById(R.id.cb_pick_friend);
                boolean isEnabled = mPickFriendCb.isEnabled();
                boolean isChecked = mPickFriendCb.isChecked();
                if (isEnabled) {
                    if (isChecked) {
                        mPickFriendCb.setChecked(false);
                    } else {
                        mPickFriendCb.setChecked(true);
                        showCheckedImage(friend.getUserAvatar(), friend.getUserId());
                    }
                }
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void showCheckedImage(String userAvatar, String userId) {
        totalCount++;
        // 包含TextView的LinearLayout
        // 参数设置
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chatroom_header_item, null);
        menuLinerLayoutParames.setMargins(6, 0, 6, 15);

        // 设置id，方便后面删除
        view.setTag(userId);
        menuLinerLayout.addView(view, menuLinerLayoutParames);
        if (totalCount > 0) {
            if (mSearchIv.getVisibility() == View.VISIBLE) {
                mSearchIv.setVisibility(View.GONE);
            }
        }
    }

    public class PinyinComparator implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            String py1 = o1.getUserHeader();
            String py2 = o2.getUserHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getUserHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getUserHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }
}
