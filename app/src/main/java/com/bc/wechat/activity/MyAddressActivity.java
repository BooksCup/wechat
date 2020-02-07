package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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
import com.bc.wechat.widget.LoadingDialog;

import java.util.List;

public class MyAddressActivity extends FragmentActivity {

    private ListView mAddressLv;
    private MyAddressAdapter mMyAddressAdapter;
    private User mUser;
    private VolleyUtil mVolleyUtil;
    private AddressDao mAddressDao;
    LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        initView();

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mAddressDao = new AddressDao();
        mDialog = new LoadingDialog(MyAddressActivity.this);

        List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter = new MyAddressAdapter(this, addressList);
        mAddressLv.setAdapter(mMyAddressAdapter);

        getAddressListByUserId(mUser.getUserId());
    }

    private void initView() {
        mAddressLv = findViewById(R.id.lv_address);
    }

    public void back(View view) {
        finish();
    }

    private void getAddressListByUserId(String userId) {
        mDialog.show();
        String url = Constant.BASE_URL + "users/" + userId + "/address";
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
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
                mDialog.dismiss();
                List<Address> addressList = mAddressDao.getAddressList();
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
            }
        });
    }
}
