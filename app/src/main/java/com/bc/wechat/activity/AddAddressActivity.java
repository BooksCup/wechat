package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;


public class AddAddressActivity extends FragmentActivity implements View.OnClickListener {

    private EditText mNameEt;
    private EditText mPhoneEt;
    private EditText mAddressDetailEt;
    private EditText mAddressInfoEt;
    private EditText mPostCodeEt;
    private TextView mSaveTv;

    private VolleyUtil mVolleyUtil;
    private User mUser;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
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
                    "确定", getString(R.string.cancel));
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
        mNameEt = findViewById(R.id.et_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mAddressInfoEt = findViewById(R.id.et_address_info);
        mAddressDetailEt = findViewById(R.id.et_address_detail);
        mPostCodeEt = findViewById(R.id.et_post_code);
        mSaveTv = findViewById(R.id.tv_save);

        mAddressInfoEt.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
        PreferencesUtil.getInstance().setPickedProvince("");
        PreferencesUtil.getInstance().setPickedCity("");
        PreferencesUtil.getInstance().setPickedDistrict("");

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
}
