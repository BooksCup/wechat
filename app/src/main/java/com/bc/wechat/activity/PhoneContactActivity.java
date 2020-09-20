package com.bc.wechat.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.R;
import com.bc.wechat.adapter.PhoneContactAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.PhoneContact;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PinyinComparator;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 手机通讯录
 *
 * @author zhou
 */
public class PhoneContactActivity extends BaseActivity {
    @BindView(R.id.lv_phone_contact)
    ListView mPhoneContactLv;

    private PhoneContactAdapter mPhoneContactAdapter;
    private VolleyUtil mVolleyUtil;
    private User mUser;
    private LoadingDialog mDialog;

    private Map<String, String> mContactNameMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        ButterKnife.bind(this);

        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        List<PhoneContact> phoneContactList = getPhoneContant();
        List<String> phoneList = new ArrayList<>();
        for (PhoneContact phoneContact : phoneContactList) {
            String phone = phoneContact.getPhoneNumber().replaceAll(" ", "");
            phoneList.add(phone);
            mContactNameMap.put(phone, phoneContact.getDisplayName());
        }
        getPhoneContactList(mUser.getUserId(), JSON.toJSONString(phoneList));
    }

    public void back(View view) {
        finish();
    }

    /**
     * 获取通讯录里的微信好友列表
     *
     * @param userId 用户ID
     * @param phones 手机号列表(json格式)
     * @return 通讯录里的微信好友列表
     */
    private void getPhoneContactList(String userId, String phones) {
        String url = Constant.BASE_URL + "users/" + userId + "/phoneContact";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phones", phones);

        mDialog.setMessage("正在获取朋友信息");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        mVolleyUtil.httpPostRequest(url, paramMap, response -> {
            final List<User> userList = JSONArray.parseArray(response, User.class);
            for (User user : userList) {
                user.setUserHeader(CommonUtil.setUserHeader(mContactNameMap.get(user.getUserPhone())));
            }
            // 对list进行排序
            Collections.sort(userList, new PinyinComparator() {
            });

            mPhoneContactAdapter = new PhoneContactAdapter(PhoneContactActivity.this, userList, mContactNameMap);
            mPhoneContactLv.setAdapter(mPhoneContactAdapter);

            mDialog.dismiss();
        }, volleyError -> mDialog.dismiss());
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
}
