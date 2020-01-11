package com.bc.wechat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
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
import com.bc.wechat.widget.EditDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);
        initStatusBar();
        initView();

        mDeviceInfoDao = new DeviceInfoDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

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

                mDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        final DeviceInfo deviceInfo = deviceInfoList.get(position);
                        openEditDialog(deviceInfo, position);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                final List<DeviceInfo> deviceInfoList = mDeviceInfoDao.getDeviceInfoList();
                mManageDevicesAdapter.setData(deviceInfoList);
                mManageDevicesAdapter.notifyDataSetChanged();

                mDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        final DeviceInfo deviceInfo = deviceInfoList.get(position);
                        openEditDialog(deviceInfo, position);
                    }
                });
            }
        });
    }

    private void openEditDialog(final DeviceInfo deviceInfo, final int position) {
        final EditDialog mEditDialog = new EditDialog(ManageDevicesActivity.this, "修改手机名",
                deviceInfo.getPhoneBrand() + "-" + deviceInfo.getPhoneModel(),
                "确定", getString(R.string.cancel));
        mEditDialog.setOnDialogClickListener(new EditDialog.OnDialogClickListener() {
            @Override
            public void onOkClick() {
                mEditDialog.dismiss();
                String phoneModel = mEditDialog.getContent();
                deviceInfo.setPhoneModelAlias(phoneModel);

                mDialog.setMessage("请稍候...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                updateUserLoginDevice(position, mUser.getUserId(), deviceInfo);
            }

            @Override
            public void onCancelClick() {
                mEditDialog.dismiss();
            }
        });

        // 点击空白处消失
        mEditDialog.setCancelable(false);
        mEditDialog.show();
    }

    private void updateUserLoginDevice(final int position, String userId, final DeviceInfo deviceInfo) {
        String url = Constant.BASE_URL + "users/" + userId + "/devices/" + deviceInfo.getDeviceId();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneModelAlias", deviceInfo.getPhoneModelAlias());

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();

                // TODO
                // 更新本地数据

                Message message = new Message();
                message.what = 1;
                message.arg1 = position;
                message.obj = deviceInfo;
                mHandler.sendMessage(message);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();

            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1) {
                int position = message.arg1;
                DeviceInfo deviceInfo = (DeviceInfo) message.obj;
                mManageDevicesAdapter.updateChangedItem(position, deviceInfo);
            }
        }
    };
}
