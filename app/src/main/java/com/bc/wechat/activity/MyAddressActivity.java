package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.MyAddressAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.AddressDao;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.List;

public class MyAddressActivity extends FragmentActivity {

    private ImageView mAddIv;
    private ListView mAddressLv;
    private MyAddressAdapter mMyAddressAdapter;
    private User mUser;
    private VolleyUtil mVolleyUtil;
    private AddressDao mAddressDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        initView();

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mAddressDao = new AddressDao();

        List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter = new MyAddressAdapter(this, addressList);
        mAddressLv.setAdapter(mMyAddressAdapter);

        getAddressListByUserId(mUser.getUserId());
    }

    private void initView() {
        mAddIv = findViewById(R.id.iv_add);
        mAddressLv = findViewById(R.id.lv_address);

        mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAddressActivity.this, AddAddressActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 本地读取
        List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter.setData(addressList);
        mMyAddressAdapter.notifyDataSetChanged();

        // 服务器读取
        getAddressListByUserId(mUser.getUserId());
    }

    private void getAddressListByUserId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/address";
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Address> addressList = JSONArray.parseArray(response, Address.class);
                if (null != addressList && addressList.size() > 0) {
                    // 持久化
                    mAddressDao.clearAddress();
                    for (Address address : addressList) {
                        if (null != address) {
                            mAddressDao.saveAddress(address);
                        }
                    }
                }
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                List<Address> addressList = mAddressDao.getAddressList();
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
            }
        });
    }
}
