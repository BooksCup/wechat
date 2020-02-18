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
import com.bc.wechat.dao.AddressDao;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改地址
 *
 * @author zhou
 */
public class ModifyAddressActivity extends FragmentActivity implements View.OnClickListener {
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
    private Address mAddress;
    private AddressDao mAddressDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_modify_address);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(ModifyAddressActivity.this);
        mAddressDao = new AddressDao();
        initView();
        PreferencesUtil.getInstance().init(this);
    }

    public void back(View view) {
        String addressName = mNameEt.getText().toString();
        String addressPhone = mPhoneEt.getText().toString();
        String addressInfo = mAddressInfoEt.getText().toString();
        String addressDetail = mAddressDetailEt.getText().toString();
        String addressPostCode = mPostCodeEt.getText().toString();

        boolean addressNameModifyFlag = !TextUtils.isEmpty(addressName) && !mAddress.getAddressName().equals(addressName);
        boolean addressPhoneModifyFlag = !TextUtils.isEmpty(addressPhone) && !mAddress.getAddressPhone().equals(addressPhone);
        boolean addressInfoModifyFlag = !TextUtils.isEmpty(addressInfo) && !mAddress.getAddressInfo().equals(addressInfo);
        boolean addressDetailModifyFlag = !TextUtils.isEmpty(addressDetail) && !mAddress.getAddressDetail().equals(addressDetail);
        boolean addressPostCodeModifyFlag = !mAddress.getAddressPostCode().equals(addressPostCode);

        if (addressNameModifyFlag ||
                addressPhoneModifyFlag ||
                addressInfoModifyFlag ||
                addressDetailModifyFlag ||
                addressPostCodeModifyFlag) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(ModifyAddressActivity.this, "提示",
                    "是否放弃修改地址信息？",
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
        mTitleTv = findViewById(R.id.tv_title);
        mTitleTv.setText("修改地址");

        mAddress = (Address) getIntent().getSerializableExtra("address");

        mNameEt = findViewById(R.id.et_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mAddressInfoEt = findViewById(R.id.et_address_info);
        mAddressDetailEt = findViewById(R.id.et_address_detail);
        mPostCodeEt = findViewById(R.id.et_post_code);
        mSaveTv = findViewById(R.id.tv_save);

        mNameEt.setText(mAddress.getAddressName());
        mPhoneEt.setText(mAddress.getAddressPhone());
        mAddressDetailEt.setText(mAddress.getAddressDetail());
        mPostCodeEt.setText(mAddress.getAddressPostCode());
        StringBuffer addressInfoBuffer = new StringBuffer();
        addressInfoBuffer.append(mAddress.getAddressProvince()).append(" ")
                .append(mAddress.getAddressCity()).append(" ")
                .append(mAddress.getAddressDistrict());
        mAddress.setAddressInfo(addressInfoBuffer.toString());
        mAddressInfoEt.setText(addressInfoBuffer.toString());


        mAddressInfoEt.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
        PreferencesUtil.getInstance().setPickedProvince("");
        PreferencesUtil.getInstance().setPickedCity("");
        PreferencesUtil.getInstance().setPickedDistrict("");

        mNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mAddressInfoEt.addTextChangedListener(new TextChange());
        mAddressDetailEt.addTextChangedListener(new TextChange());
        mPostCodeEt.addTextChangedListener(new TextChange());
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
            String addressPostCode = mPostCodeEt.getText().toString();

            boolean addressNameModifyFlag = !TextUtils.isEmpty(addressName) && !mAddress.getAddressName().equals(addressName);
            boolean addressPhoneModifyFlag = !TextUtils.isEmpty(addressPhone) && !mAddress.getAddressPhone().equals(addressPhone);
            boolean addressInfoModifyFlag = !TextUtils.isEmpty(addressInfo) && !mAddress.getAddressInfo().equals(addressInfo);
            boolean addressDetailModifyFlag = !TextUtils.isEmpty(addressDetail) && !mAddress.getAddressDetail().equals(addressDetail);
            boolean addressPostCodeModifyFlag = !mAddress.getAddressPostCode().equals(addressPostCode);

            if (addressNameModifyFlag ||
                    addressPhoneModifyFlag ||
                    addressInfoModifyFlag ||
                    addressDetailModifyFlag ||
                    addressPostCodeModifyFlag) {
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
                startActivity(new Intent(ModifyAddressActivity.this, PickProvinceActivity.class));
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
                modifyAddress(mAddress.getAddressId(), addressName, addressPhone, addressProvince,
                        addressCity, addressDistrict, addressDetail, addressPostCode);
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

    private void modifyAddress(final String addressId, final String addressName, final String addressPhone, final String addressProvince,
                               final String addressCity, final String addressDistrict, final String addressDetail, final String addressPostCode) {
        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/address/" + addressId;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("addressName", addressName);
        paramMap.put("addressPhone", addressPhone);
        paramMap.put("addressProvince", addressProvince);
        paramMap.put("addressCity", addressCity);
        paramMap.put("addressDistrict", addressDistrict);
        paramMap.put("addressDetail", addressDetail);
        paramMap.put("addressPostCode", addressPostCode);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();

                // 持久化
                Address address = new Address(mUser.getUserId(), addressId, addressName, addressPhone, addressProvince,
                        addressCity, addressDistrict, addressDetail, addressPostCode);
                Address currentAddress = mAddressDao.getAddressByAddressId(addressId);
                address.setId(currentAddress.getId());

                Address.save(address);

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(ModifyAddressActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(ModifyAddressActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
