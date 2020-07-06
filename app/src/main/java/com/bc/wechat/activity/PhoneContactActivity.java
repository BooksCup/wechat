package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.PhoneContactAdapter;
import com.bc.wechat.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机通讯录
 *
 * @author zhou
 */
public class PhoneContactActivity extends BaseActivity {
    private PhoneContactAdapter mPhoneContactAdapter;
    private ListView mPhoneContactLv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        initStatusBar();

        initView();

        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());

        mPhoneContactAdapter = new PhoneContactAdapter(this, userList);
        mPhoneContactLv.setAdapter(mPhoneContactAdapter);
    }

    private void initView() {
        mPhoneContactLv = findViewById(R.id.lv_phone_contact);
    }
}
