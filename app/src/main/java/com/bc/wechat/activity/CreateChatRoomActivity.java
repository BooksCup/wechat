package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.PickContactAdapter;
import com.bc.wechat.entity.Friend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreateChatRoomActivity extends FragmentActivity {

    private PickContactAdapter contactAdapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        List<Friend> friendList = Friend.listAll(Friend.class);
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        listView = findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this,
                R.layout.item_pick_contact_list, friendList);
        listView.setAdapter(contactAdapter);

    }

    public void back(View view) {
        finish();
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
