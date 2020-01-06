package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {

    private EditText mRemarkEt;
    private EditText mPhoneEt;
    private EditText mDescEt;

    private VolleyUtil mVolleyUtil;

    // 添加电话
    private RelativeLayout mAddPhoneTmpRl;
    private RelativeLayout mAddPhoneRl;

    // 保存
    private TextView mSaveTv;
    private User mUser;

    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        final String userId = getIntent().getStringExtra("userId");
        final String isFriend = getIntent().getStringExtra("isFriend");
        User user = mUserDao.getUserById(userId);

        mRemarkEt = findViewById(R.id.et_remark);
        mPhoneEt = findViewById(R.id.et_phone);
        mDescEt = findViewById(R.id.et_desc);

        mAddPhoneTmpRl = findViewById(R.id.rl_add_phone_tmp);
        mAddPhoneRl = findViewById(R.id.rl_add_phone);
        mSaveTv = findViewById(R.id.tv_save);

        if (TextUtils.isEmpty(user.getUserFriendRemark())) {
            // 无备注，展示昵称
            mRemarkEt.setText(user.getUserNickName());
        } else {
            // 有备注，展示备注
            mRemarkEt.setText(user.getUserFriendRemark());
        }

        mPhoneEt.setText(user.getUserFriendPhone());
        mDescEt.setText(user.getUserFriendDesc());

        if (Constant.IS_NOT_FRIEND.equals(isFriend)) {
            // 非好友不能添加电话
            mAddPhoneTmpRl.setVisibility(View.GONE);
            mAddPhoneRl.setVisibility(View.GONE);
        }

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = mRemarkEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String desc = mDescEt.getText().toString();
                setRemarks(mUser.getUserId(), userId, remark, phone, desc);
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void setRemarks(final String userId, final String friendId, final String remark,
                            final String phone, final String desc) {
        String url = Constant.BASE_URL + "users/" + userId + "/remarks";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("friendId", friendId);
        paramMap.put("friendRemark", remark);
        paramMap.put("friendPhone", phone);
        paramMap.put("friendDesc", desc);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = mUserDao.getUserById(friendId);
                user.setUserFriendRemark(remark);
                user.setUserFriendDesc(desc);
                user.setUserFriendPhone(phone);
                mUserDao.saveUser(user);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }
}
