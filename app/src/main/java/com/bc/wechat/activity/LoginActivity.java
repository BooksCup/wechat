package com.bc.wechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public static int sequence = 1;

    private VolleyUtil volleyUtil;

    EditText mPhoneEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    TextView mRegisterTv;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PreferencesUtil.getInstance().init(this);
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(LoginActivity.this);
        initView();
    }

    private void initView() {
        mPhoneEt = findViewById(R.id.et_user_phone);
        mPasswordEt = findViewById(R.id.et_password);
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterTv = findViewById(R.id.tv_register);

        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
        mLoginBtn.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                dialog.setMessage(getString(R.string.logging_in));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                final String phone = mPhoneEt.getText().toString().trim();
                final String password = mPasswordEt.getText().toString().trim();
                login(phone, password);
                break;
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean phoneEtHasText = mPhoneEt.getText().length() > 0;
            boolean passwordEtHasText = mPasswordEt.getText().length() > 0;
            if (phoneEtHasText && passwordEtHasText) {
                mLoginBtn.setTextColor(0xFFFFFFFF);
                mLoginBtn.setEnabled(true);
            } else {
                mLoginBtn.setTextColor(0xFFD0EFC6);
                mLoginBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void login(String phone, String password) {
        String url = Constant.BASE_URL + "users/login?phone=" + phone + "&password=" + password;
        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server response: " + response);
                User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());
                // 登录成功后设置userId和isLogin至sharedpreferences中
                PreferencesUtil.getInstance().setUserId(user.getUserId());
                PreferencesUtil.getInstance().setUserNickName(user.getUserNickName());
                PreferencesUtil.getInstance().setUserWxId(user.getUserWxId());
                PreferencesUtil.getInstance().setUserAvatar(user.getUserAvatar());
                PreferencesUtil.getInstance().setUserSex(user.getUserSex());
                PreferencesUtil.getInstance().setUserSign(user.getUserSign());
                PreferencesUtil.getInstance().setLogin(true);
                JPushInterface.setAlias(LoginActivity.this, sequence, user.getUserId());

                List<User> friendList = user.getFriendList();
                for (User userFriend : friendList) {
                    List<Friend> checkList = Friend.find(Friend.class, "user_id = ?", userFriend.getUserId());
                    if (null != checkList && checkList.size() > 0) {
                        // 好友已存在，忽略
                    } else {
                        // 不存在,插入sqlite
                        Friend friend = new Friend();
                        friend.setUserId(userFriend.getUserId());
                        friend.setUserNickName(userFriend.getUserNickName());
                        friend.setUserAvatar(userFriend.getUserAvatar());
                        friend.setUserHeader(setUserHeader(userFriend.getUserNickName()));
                        friend.setUserSex(userFriend.getUserSex());
                        Friend.save(friend);
                    }
                }

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();

                if (volleyError instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(LoginActivity.this,
                                R.string.username_or_password_error, Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        });
    }

    /**
     * 获取用户header
     * 如: "张三"的header就是"Z"，用于用户默认分组
     *
     * @param userNickName 用户昵称
     * @return 用户header
     */
    private static String setUserHeader(String userNickName) {
        StringBuffer stringBuffer = new StringBuffer();
        // 将汉字拆分成一个个的char
        char[] chars = userNickName.toCharArray();
        // 遍历汉字的每一个char
        for (int i = 0; i < chars.length; i++) {

            try {
                HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

                // UPPERCASE：大写  (ZHONG)
                format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//输出大写

                // WITHOUT_TONE：无音标  (zhong)
                format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                // 汉字的所有读音放在一个pinyins数组
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
                if (pinyins == null) {
                    stringBuffer.append(chars[i]);
                } else {
                    stringBuffer.append(pinyins[0]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        char firstChar = stringBuffer.toString().toUpperCase().charAt(0);
        // 不是A-Z字母
        if (firstChar > 90 || firstChar < 65) {
            return "#";
        } else { // 代表是A-Z
            return String.valueOf(firstChar);
        }
    }
}
