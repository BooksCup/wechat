package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.MyAddressAdapter;
import com.bc.wechat.entity.Address;

import java.util.ArrayList;
import java.util.List;

public class MyAddressActivity extends FragmentActivity {

    private ListView mAddressLv;
    private MyAddressAdapter mMyAddressAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        initView();

        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        mMyAddressAdapter = new MyAddressAdapter(this, addressList);
        mAddressLv.setAdapter(mMyAddressAdapter);
    }

    private void initView() {
        mAddressLv = findViewById(R.id.lv_address);
    }
}
