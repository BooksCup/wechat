package com.bc.wechat.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.PhoneContact;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绑定手机号
 *
 * @author zhou
 */
public class ModifyPhoneActivity extends BaseActivity implements View.OnClickListener {

    private TextView mPhoneTv;
    private Button mMobileContactsBtn;
    private Button mChangeMobileBtn;

    private User mUser;
    private VolleyUtil mVolleyUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);

        initView();
    }

    private void initView() {
        mPhoneTv = findViewById(R.id.tv_phone);
        mMobileContactsBtn = findViewById(R.id.btn_mobile_contacts);
        mChangeMobileBtn = findViewById(R.id.btn_change_mobile);

        mMobileContactsBtn.setOnClickListener(this);
        mChangeMobileBtn.setOnClickListener(this);
        mPhoneTv.setText("绑定的手机号：" + mUser.getUserPhone());
    }

    public void back() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mobile_contacts:
//                List<PhoneContact> phoneContactList = getPhoneContant();
//                List<String> phoneList = new ArrayList<>();
//                for (PhoneContact phoneContact : phoneContactList) {
//                    phoneList.add(phoneContact.getPhoneNumber().replaceAll(" ", ""));
//                }
//                getPhoneContactList(mUser.getUserId(), JSON.toJSONString(phoneList));
                startActivity(new Intent(ModifyPhoneActivity.this, PhoneContactActivity.class));
                break;
            case R.id.btn_change_mobile:
                break;
        }
    }

    /**
     * 获取手机所有联系人
     *
     * @return 手机所有联系人列表
     */
    public List<PhoneContact> getPhoneContant() {
        // 取得ContentResolver
        List<PhoneContact> phoneContactList = new ArrayList<>();
        ContentResolver content = getContentResolver();

        // 联系人的URI
        Cursor cursor = content
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // id下标
                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                // 名称下标
                int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                // 电话号码列
                int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String contactId = cursor.getString(idColumn);
                String displayName = cursor.getString(displayNameColumn);
                String phoneNumber = cursor.getString(numberColumn);

                PhoneContact phoneContact = new PhoneContact(contactId, displayName, phoneNumber);
                phoneContactList.add(phoneContact);
            }
        }
        return phoneContactList;
    }

    private void getPhoneContactList(String userId, String phones) {
        String url = Constant.BASE_URL + "users/" + userId + "/phoneContact";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phones", phones);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }
}
