package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.ManageDevicesAdapter;
import com.bc.wechat.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录设备管理
 *
 * @author zhou
 */
public class ManageDevicesActivity extends BaseActivity {

    private ListView mDevicesLv;
    private ManageDevicesAdapter mManageDevicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);
        initView();

        List<DeviceInfo> loginDeviceList = new ArrayList<>();
        loginDeviceList.add(new DeviceInfo());
        loginDeviceList.add(new DeviceInfo());
        mManageDevicesAdapter = new ManageDevicesAdapter(this, loginDeviceList);
        mDevicesLv.setAdapter(mManageDevicesAdapter);
    }

    private void initView() {
        mDevicesLv = findViewById(R.id.lv_devices);
    }
}
