package com.bc.wechat.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 新增地址
 *
 * @author zhou
 */
public class AddAddressActivity extends FragmentActivity implements View.OnClickListener {

    private static final int CONTACTS_PERMISSION = 0x01;
    private static final int REQUEST_CODE_CONTACTS = 0;

    private TextView mTitleTv;

    private EditText mNameEt;
    private EditText mPhoneEt;
    private EditText mAddressDetailEt;
    private EditText mAddressInfoEt;
    private EditText mPostCodeEt;
    private TextView mSaveTv;

    private VolleyUtil mVolleyUtil;
    private User mUser;

    private LoadingDialog mDialog;

    private ImageView mAddressBookIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_modify_address);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(AddAddressActivity.this);
        initView();
        PreferencesUtil.getInstance().init(this);
    }

    public void back(View view) {
        String addressName = mNameEt.getText().toString();
        String addressPhone = mPhoneEt.getText().toString();
        String addressInfo = mAddressInfoEt.getText().toString();
        String addressDetail = mAddressDetailEt.getText().toString();
        String addressPostCode = mPostCodeEt.getText().toString();

        if (!TextUtils.isEmpty(addressName) ||
                !TextUtils.isEmpty(addressPhone) ||
                !TextUtils.isEmpty(addressInfo) ||
                !TextUtils.isEmpty(addressDetail) ||
                !TextUtils.isEmpty(addressPostCode)) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(AddAddressActivity.this, "提示",
                    "是否放弃新增地址信息？",
                    "确定", getString(R.string.cancel), getColor(R.color.navy_blue));
            confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    confirmDialog.dismiss();
                    finish();
                }

                @Override
                public void onCancelClick() {
                    confirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            confirmDialog.setCancelable(true);
            confirmDialog.show();
        } else {
            finish();
        }
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        mTitleTv.setText("新增地址");

        mNameEt = findViewById(R.id.et_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mAddressInfoEt = findViewById(R.id.et_address_info);
        mAddressDetailEt = findViewById(R.id.et_address_detail);
        mPostCodeEt = findViewById(R.id.et_post_code);
        mSaveTv = findViewById(R.id.tv_save);

        mAddressBookIv = findViewById(R.id.iv_address_book);

        mAddressInfoEt.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
        mAddressBookIv.setOnClickListener(this);
        PreferencesUtil.getInstance().setPickedProvince("");
        PreferencesUtil.getInstance().setPickedCity("");
        PreferencesUtil.getInstance().setPickedDistrict("");
        PreferencesUtil.getInstance().setPickedPostCode("");

        mNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mAddressInfoEt.addTextChangedListener(new TextChange());
        mAddressDetailEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String addressName = mNameEt.getText().toString();
            String addressPhone = mPhoneEt.getText().toString();
            String addressInfo = mAddressInfoEt.getText().toString();
            String addressDetail = mAddressDetailEt.getText().toString();

            if (!TextUtils.isEmpty(addressName) &&
                    !TextUtils.isEmpty(addressPhone) &&
                    !TextUtils.isEmpty(addressInfo) &&
                    !TextUtils.isEmpty(addressDetail)) {
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                mSaveTv.setTextColor(0xFFD0EFC6);
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_address_info:
                startActivity(new Intent(AddAddressActivity.this, PickProvinceActivity.class));
                break;
            case R.id.tv_save:
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                String addressName = mNameEt.getText().toString();
                String addressPhone = mPhoneEt.getText().toString();
                String addressProvince = PreferencesUtil.getInstance().getPickedProvince();
                String addressCity = PreferencesUtil.getInstance().getPickedCity();
                String addressDistrict = PreferencesUtil.getInstance().getPickedDistrict();
                String addressDetail = mAddressDetailEt.getText().toString();
                String addressPostCode = mPostCodeEt.getText().toString();
                addAddress(addressName, addressPhone, addressProvince, addressCity, addressDistrict, addressDetail, addressPostCode);
                break;
            case R.id.iv_address_book:
                String[] permissions = new String[]{"android.permission.READ_CONTACTS"};
                requestPermissions(AddAddressActivity.this, permissions, CONTACTS_PERMISSION);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String pickedProvince = PreferencesUtil.getInstance().getPickedProvince();
        String pickedCity = PreferencesUtil.getInstance().getPickedCity();
        String pickedDistrict = PreferencesUtil.getInstance().getPickedDistrict();
        if (!TextUtils.isEmpty(pickedProvince)
                && !TextUtils.isEmpty(pickedCity)
                && !TextUtils.isEmpty(pickedDistrict)) {
            StringBuffer addressInfoBuffer = new StringBuffer();
            addressInfoBuffer.append(pickedProvince).append(" ")
                    .append(pickedCity).append(" ")
                    .append(pickedDistrict);
            mAddressInfoEt.setText(addressInfoBuffer.toString());
        }
        String pickedPostCode = PreferencesUtil.getInstance().getPickedPostCode();
        if (!TextUtils.isEmpty(pickedPostCode)) {
            mPostCodeEt.setText(pickedPostCode);
        }
    }

    private void addAddress(final String addressName, final String addressPhone, final String addressProvince,
                            final String addressCity, final String addressDistrict, final String addressDetail, final String addressPostCode) {
        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/address";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("addressName", addressName);
        paramMap.put("addressPhone", addressPhone);
        paramMap.put("addressProvince", addressProvince);
        paramMap.put("addressCity", addressCity);
        paramMap.put("addressDistrict", addressDistrict);
        paramMap.put("addressDetail", addressDetail);
        paramMap.put("addressPostCode", addressPostCode);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();

                // 持久化
                Address address = new Address(addressName, addressPhone, addressProvince,
                        addressCity, addressDistrict, addressDetail, addressPostCode);
                Address.save(address);

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(AddAddressActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(AddAddressActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    if (data != null) {
                        Uri uri = data.getData();
                        String name = null;
                        String phoneNumber = null;
                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(uri,
                                null, null, null, null);
                        while (cursor.moveToNext()) {
                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        cursor.close();
                        phoneNumber = phoneNumber.replaceAll(" ", "");
                        if (!TextUtils.isEmpty(name)) {
                            mNameEt.setText(name);
                        }
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            mPhoneEt.setText(phoneNumber);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                showContacts();
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, request);
            }
        }
    }

    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            // 同意权限做的处理,开启服务提交通讯录
            showContacts();
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(AddAddressActivity.this, permissions[0]);
        }
    }

    public void handleRejectPermission(final Activity context, String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(AddAddressActivity.this, "权限申请",
                    "在设置-应用-微信-权限中开启通讯录权限，以正常使用联系人查找功能",
                    "去设置", "取消", getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    /**
     * 跳转到通讯录
     */
    private void showContacts() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, REQUEST_CODE_CONTACTS);
    }
}
