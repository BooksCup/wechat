package com.bc.wechat.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.R;
import com.bc.wechat.adapter.ManageDevicesAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.DeviceInfoDao;
import com.bc.wechat.entity.DeviceInfo;
import com.bc.wechat.entity.User;
import com.bc.wechat.observer.UpdateUiInterface;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.EditDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录设备管理
 * TODO: editText会遮住dialog,需要解决
 *
 * @author zhou
 */
public class ManageDevicesActivity extends BaseActivity implements UpdateUiInterface {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_edit)
    TextView mEditTv;

    @BindView(R.id.lv_devices)
    ListView mDevicesLv;

    ManageDevicesAdapter mManageDevicesAdapter;

    DeviceInfoDao mDeviceInfoDao;
    VolleyUtil mVolleyUtil;
    User mUser;
    LoadingDialog mDialog;
    boolean mIsEdit = false;

    @Override
    public int getContentView() {
        return R.layout.activity_manage_devices;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mDeviceInfoDao = new DeviceInfoDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        List<DeviceInfo> loginDeviceList = mDeviceInfoDao.getDeviceInfoList();
        mManageDevicesAdapter = new ManageDevicesAdapter(this, loginDeviceList, this);
        mDevicesLv.setAdapter(mManageDevicesAdapter);

        getDeviceInfoListByUserId(mUser.getUserId());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_edit:
                List<DeviceInfo> loginDeviceList = mDeviceInfoDao.getDeviceInfoList();
                if (mIsEdit) {
                    mEditTv.setText(getString(R.string.edit));
                    for (DeviceInfo deviceInfo : loginDeviceList) {
                        deviceInfo.setEdit(false);
                    }
                    mIsEdit = false;
                } else {
                    mEditTv.setText(getString(R.string.complete));
                    for (DeviceInfo deviceInfo : loginDeviceList) {
                        deviceInfo.setEdit(true);
                    }
                    mIsEdit = true;
                }
                mManageDevicesAdapter.setData(loginDeviceList);
                mManageDevicesAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void getDeviceInfoListByUserId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/devices";
        mVolleyUtil.httpGetRequest(url, response -> {
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

            mDevicesLv.setOnItemClickListener((adapterView, view, position, id) -> {
                final DeviceInfo deviceInfo = deviceInfoList.get(position);
                openEditDialog(deviceInfo, position);
            });
        }, volleyError -> {
            final List<DeviceInfo> deviceInfoList = mDeviceInfoDao.getDeviceInfoList();
            mManageDevicesAdapter.setData(deviceInfoList);
            mManageDevicesAdapter.notifyDataSetChanged();

            mDevicesLv.setOnItemClickListener((adapterView, view, position, id) -> {
                final DeviceInfo deviceInfo = deviceInfoList.get(position);
                openEditDialog(deviceInfo, position);
            });
        });
    }

    /**
     * 打开编辑对话框
     *
     * @param deviceInfo 设备信息
     * @param position   位置
     */
    private void openEditDialog(final DeviceInfo deviceInfo, final int position) {
        String content;
        if (TextUtils.isEmpty(deviceInfo.getPhoneModelAlias())) {
            content = deviceInfo.getPhoneBrand() + "-" + deviceInfo.getPhoneModel();
        } else {
            content = deviceInfo.getPhoneModelAlias();
        }

        final EditDialog mEditDialog = new EditDialog(ManageDevicesActivity.this, getString(R.string.edit_device_name),
                content, getString(R.string.edit_device_name_tips),
                getString(R.string.ok), getString(R.string.cancel));
        mEditDialog.setOnDialogClickListener(new EditDialog.OnDialogClickListener() {
            @Override
            public void onOkClick() {
                mEditDialog.dismiss();
                String phoneModelAlias = mEditDialog.getContent();

                mDialog.setMessage(getString(R.string.please_wait));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                updateUserLoginDevice(position, mUser.getUserId(), deviceInfo, phoneModelAlias);
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

    private void updateUserLoginDevice(final int position, String userId, final DeviceInfo deviceInfo, final String phoneModelAlias) {
        String url = Constant.BASE_URL + "users/" + userId + "/devices/" + deviceInfo.getDeviceId();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneModelAlias", phoneModelAlias);

        mVolleyUtil.httpPutRequest(url, paramMap, s -> {
            mDialog.dismiss();

            // 更新本地数据
            DeviceInfo originDeviceInfo = mDeviceInfoDao.getDeviceInfoByDeviceId(deviceInfo.getDeviceId());
            if (null != originDeviceInfo) {
                deviceInfo.setId(originDeviceInfo.getId());
                mDeviceInfoDao.saveDeviceInfo(deviceInfo);
            }

            deviceInfo.setPhoneModelAlias(phoneModelAlias);

            Message message = new Message();
            message.what = 1;
            message.arg1 = position;
            message.obj = deviceInfo;
            mHandler.sendMessage(message);
        }, volleyError -> mDialog.dismiss());
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

    @Override
    public void updateUi() {
        mEditTv.setText(getString(R.string.edit));
        List<DeviceInfo> deviceInfoList = mDeviceInfoDao.getDeviceInfoList();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            deviceInfo.setEdit(false);
        }
        mIsEdit = false;
        mManageDevicesAdapter.setData(deviceInfoList);
        mManageDevicesAdapter.notifyDataSetChanged();
    }

}