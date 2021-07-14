package com.bc.wechat.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
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
import com.bc.wechat.dao.AddressDao;
import com.bc.wechat.dao.AreaDao;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.Area;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * 修改地址
 *
 * @author zhou
 */
public class ModifyAddressActivity extends BaseActivity {

    private static final int REQUEST_CODE_CONTACTS = 0;
    private static final int REQUEST_CODE_LOCATION = 1;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_name)
    EditText mNameEt;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_address_detail)
    EditText mAddressDetailEt;

    @BindView(R.id.et_address_info)
    EditText mAddressInfoEt;

    @BindView(R.id.et_post_code)
    EditText mPostCodeEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.iv_address_book)
    ImageView mAddressBookIv;

    @BindView(R.id.iv_location)
    ImageView mLocationIv;

    @BindView(R.id.vi_name)
    View mNameVi;

    @BindView(R.id.vi_phone)
    View mPhoneVi;

    @BindView(R.id.vi_address_detail)
    View mAddressDetailVi;

    @BindView(R.id.vi_post_code)
    View mPostCodeVi;

    private VolleyUtil mVolleyUtil;
    private User mUser;

    private LoadingDialog mDialog;
    private Address mAddress;
    private AddressDao mAddressDao;
    private AreaDao mAreaDao;

    public void back(View view) {
        String addressName = mNameEt.getText().toString();
        String addressPhone = mPhoneEt.getText().toString();
        String addressInfo = mAddressInfoEt.getText().toString();
        String addressDetail = mAddressDetailEt.getText().toString();
        String addressPostCode = mPostCodeEt.getText().toString();

        boolean addressNameModifyFlag = !TextUtils.isEmpty(addressName) && !mAddress.getName().equals(addressName);
        boolean addressPhoneModifyFlag = !TextUtils.isEmpty(addressPhone) && !mAddress.getPhone().equals(addressPhone);
        boolean addressInfoModifyFlag = !TextUtils.isEmpty(addressInfo) && !mAddress.getInfo().equals(addressInfo);
        boolean addressDetailModifyFlag = !TextUtils.isEmpty(addressDetail) && !mAddress.getDetail().equals(addressDetail);
        boolean addressPostCodeModifyFlag = !mAddress.getPostCode().equals(addressPostCode);

        if (addressNameModifyFlag ||
                addressPhoneModifyFlag ||
                addressInfoModifyFlag ||
                addressDetailModifyFlag ||
                addressPostCodeModifyFlag) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(ModifyAddressActivity.this, getString(R.string.tips),
                    getString(R.string.modify_address_abandon_tips),
                    getString(R.string.ok), getString(R.string.cancel), getColor(R.color.navy_blue));
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

    @Override
    public int getContentView() {
        return R.layout.activity_add_or_modify_address;
    }

    @Override
    public void initView() {
        initStatusBar();
        mTitleTv.setText(getString(R.string.modify_address));
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mAddressInfoEt.addTextChangedListener(new TextChange());
        mAddressDetailEt.addTextChangedListener(new TextChange());
        mPostCodeEt.addTextChangedListener(new TextChange());
    }

    @Override
    public void initData() {
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(ModifyAddressActivity.this);
        mAddressDao = new AddressDao();
        mAreaDao = new AreaDao();
        PreferencesUtil.getInstance().init(this);

        mAddress = (Address) getIntent().getSerializableExtra("address");

        mNameEt.setText(mAddress.getName());
        mPhoneEt.setText(mAddress.getPhone());
        mAddressDetailEt.setText(mAddress.getDetail());
        mPostCodeEt.setText(mAddress.getPostCode());
        StringBuffer addressInfoBuffer = new StringBuffer();
        addressInfoBuffer.append(mAddress.getProvince()).append(" ")
                .append(mAddress.getCity()).append(" ")
                .append(mAddress.getDistrict());
        mAddress.setInfo(addressInfoBuffer.toString());
        mAddressInfoEt.setText(addressInfoBuffer.toString());

        PreferencesUtil.getInstance().setPickedProvince("");
        PreferencesUtil.getInstance().setPickedCity("");
        PreferencesUtil.getInstance().setPickedDistrict("");
        PreferencesUtil.getInstance().setPickedPostCode("");
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

            boolean addressNameModifyFlag = !TextUtils.isEmpty(addressName) && !mAddress.getName().equals(addressName);
            boolean addressPhoneModifyFlag = !TextUtils.isEmpty(addressPhone) && !mAddress.getPhone().equals(addressPhone);
            boolean addressInfoModifyFlag = !TextUtils.isEmpty(addressInfo) && !mAddress.getInfo().equals(addressInfo);
            boolean addressDetailModifyFlag = !TextUtils.isEmpty(addressDetail) && !mAddress.getDetail().equals(addressDetail);
            boolean addressPostCodeModifyFlag = !mAddress.getPostCode().equals(addressPostCode);

            if (addressNameModifyFlag ||
                    addressPhoneModifyFlag ||
                    addressInfoModifyFlag ||
                    addressDetailModifyFlag ||
                    addressPostCodeModifyFlag) {
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    @OnClick({R.id.et_address_info, R.id.tv_save, R.id.iv_address_book, R.id.iv_location})
    public void onClick(View view) {
        String[] permissions;
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

            case R.id.iv_address_book:
                permissions = new String[]{"android.permission.READ_CONTACTS"};
                requestPermissions(ModifyAddressActivity.this, permissions, REQUEST_CODE_CONTACTS);
                break;
            case R.id.iv_location:
                permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
                requestPermissions(ModifyAddressActivity.this, permissions, REQUEST_CODE_LOCATION);
                break;
        }
    }

    @OnFocusChange({R.id.et_name, R.id.et_phone, R.id.et_address_detail, R.id.et_post_code})
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_name:
                if (hasFocus) {
                    mNameVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mNameVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_phone:
                if (hasFocus) {
                    mPhoneVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mPhoneVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_address_detail:
                if (hasFocus) {
                    mAddressDetailVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mAddressDetailVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_post_code:
                if (hasFocus) {
                    mPostCodeVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mPostCodeVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
        }
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
                case REQUEST_CODE_LOCATION:
                    // 获取省市区以及详细地址信息
                    String province = data.getStringExtra("province");
                    String city = data.getStringExtra("city");
                    String district = data.getStringExtra("district");
                    String addressDetail = data.getStringExtra("addressDetail");
                    PreferencesUtil.getInstance().setPickedProvince(province);
                    PreferencesUtil.getInstance().setPickedCity(city);
                    PreferencesUtil.getInstance().setPickedDistrict(district);

                    StringBuffer addressInfoBuffer = new StringBuffer();
                    addressInfoBuffer.append(province).append(" ")
                            .append(city).append(" ")
                            .append(district);
                    mAddressInfoEt.setText(addressInfoBuffer.toString());
                    mAddressDetailEt.setText(addressDetail);

                    Area districtArea = mAreaDao.getDistrictByCityNameAndDistrictName(city, district);
                    if (TextUtils.isEmpty(districtArea.getPostCode())) {
                        mPostCodeEt.setText(Constant.DEFAULT_POST_CODE);
                    } else {
                        mPostCodeEt.setText(districtArea.getPostCode());
                    }

                    break;
            }
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

    private void modifyAddress(final String addressId, final String addressName, final String addressPhone, final String addressProvince,
                               final String addressCity, final String addressDistrict, final String addressDetail, final String addressPostCode) {
        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/address/" + addressId;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", addressName);
        paramMap.put("phone", addressPhone);
        paramMap.put("province", addressProvince);
        paramMap.put("city", addressCity);
        paramMap.put("district", addressDistrict);
        paramMap.put("detail", addressDetail);
        paramMap.put("postCode", addressPostCode);

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

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
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
                switch (requestCode) {
                    case REQUEST_CODE_CONTACTS:
                        showContacts();
                        break;
                    case REQUEST_CODE_LOCATION:
                        showMapPicker();
                        break;
                }

            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
            }
        }
    }

    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    // 同意通讯录权限,开启通讯录服务
                    showContacts();
                    break;
                case REQUEST_CODE_LOCATION:
                    // 同意定位权限,进入地图选择器
                    showMapPicker();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(ModifyAddressActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    content = getString(R.string.request_permission_contacts);
                    break;
                case REQUEST_CODE_LOCATION:
                    content = getString(R.string.request_permission_location);
                    break;
            }
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(ModifyAddressActivity.this, getString(R.string.request_permission),
                    content,
                    getString(R.string.go_setting), getString(R.string.cancel), getColor(R.color.navy_blue));
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

    /**
     * 进入地图选择页面
     */
    private void showMapPicker() {
        Intent intent = new Intent(ModifyAddressActivity.this, MapPickerActivity.class);
        // 是否发送定位
        // true:发送定位  false:无发送按钮，显示定位
        intent.putExtra("sendLocation", true);

        // 定位类型
        // 获取省市区信息
        intent.putExtra("locationType", Constant.LOCATION_TYPE_AREA);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
    }

}