package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.ManageDevicesAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.DeviceInfoDao;
import com.bc.wechat.entity.DeviceInfo;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.List;

/**
 * 登录设备管理
 *
 * @author zhou
 */
public class ManageDevicesActivity extends BaseActivity {

    private TextView mTitleTv;
    private ListView mDevicesLv;
    private ManageDevicesAdapter mManageDevicesAdapter;

    private DeviceInfoDao mDeviceInfoDao;
    private VolleyUtil mVolleyUtil;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);
        initStatusBar();
        initView();

        mDeviceInfoDao = new DeviceInfoDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();

        List<DeviceInfo> loginDeviceList = mDeviceInfoDao.getDeviceInfoList();
        mManageDevicesAdapter = new ManageDevicesAdapter(this, loginDeviceList);
        mDevicesLv.setAdapter(mManageDevicesAdapter);

        getDeviceInfoListByUserId(mUser.getUserId());
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mDevicesLv = findViewById(R.id.lv_devices);
    }

    private void getDeviceInfoListByUserId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/devices";
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final List<DeviceInfo> deviceInfoList = JSONArray.parseArray(response, DeviceInfo.class);
                if (null != deviceInfoList && deviceInfoList.size() > 0) {
                    // 持久化
                    mDeviceInfoDao.clearDeviceInfo();
                    for (DeviceInfo deviceInfo : deviceInfoList) {
                        if (null != deviceInfo) {
                            mDeviceInfoDao.saveDeviceInfo(deviceInfo);
                        }
                    }
                }
                mManageDevicesAdapter.setData(deviceInfoList);
                mManageDevicesAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                final List<DeviceInfo> deviceInfoList = mDeviceInfoDao.getDeviceInfoList();
                mManageDevicesAdapter.setData(deviceInfoList);
                mManageDevicesAdapter.notifyDataSetChanged();
            }
        });
    }
}
