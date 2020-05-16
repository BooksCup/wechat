package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.PeopleNearbyAdapter;
import com.bc.wechat.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 附近的人
 *
 * @author zhou
 */
public class PeopleNearbyActivity extends FragmentActivity {
    private ListView mPeopleNearbyLv;
    private PeopleNearbyAdapter mPeopleNearbyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);
        initView();
    }

    private void initView() {
        mPeopleNearbyLv = findViewById(R.id.lv_people_nearby);
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());

        mPeopleNearbyAdapter = new PeopleNearbyAdapter(PeopleNearbyActivity.this,
                R.layout.item_people_nearby, userList);
        mPeopleNearbyLv.setAdapter(mPeopleNearbyAdapter);
    }
}
