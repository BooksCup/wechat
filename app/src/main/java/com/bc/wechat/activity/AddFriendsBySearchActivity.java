package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import androidx.annotation.Nullable;

/**
 * 搜索好友
 *
 * @author zhou
 */
public class AddFriendsBySearchActivity extends BaseActivity {

    private static final String TAG = "AddFriendsBySearch";

    private EditText mSearchEt;
    private RelativeLayout mSearchRl;
    private TextView mSearchTv;

    private VolleyUtil mVolleyUtil;
    private LoadingDialog mDialog;
    private User mUser;
    private UserDao mUserDao;

    private ImageView mClearIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_by_search);
        initStatusBar();

        initView();
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(AddFriendsBySearchActivity.this);
        mUserDao = new UserDao();
        mSearchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage(getString(R.string.searching_for_user));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                String keyword = mSearchEt.getText().toString().trim();
                searchUser(keyword);
            }
        });

        // 初始化弹出软键盘
        showKeyboard(mSearchEt);
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mSearchRl = findViewById(R.id.rl_search);
        mSearchTv = findViewById(R.id.tv_search);

        mClearIv = findViewById(R.id.iv_clear);

        mSearchEt.addTextChangedListener(new TextChange());
        mClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEt.setText("");
            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean searchHasText = mSearchEt.getText().toString().length() > 0;
            if (searchHasText) {
                mSearchRl.setVisibility(View.VISIBLE);
                mSearchTv.setText(mSearchEt.getText().toString().trim());

                mClearIv.setVisibility(View.VISIBLE);
            } else {
                mSearchRl.setVisibility(View.GONE);
                mSearchTv.setText("");

                mClearIv.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void back(View view) {
        finish();
    }

    private void searchUser(final String keyword) {
        String userId = mUser.getUserId();
        final String url = Constant.BASE_URL + "users/searchForAddFriends?keyword=" + keyword + "&userId=" + userId;
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server response: " + response);
                User user = JSON.parseObject(response, User.class);
                mUserDao.saveUser(user);
                Log.d(TAG, "userId:" + user.getUserId());
                if (Constant.IS_FRIEND.equals(user.getIsFriend())) {
                    // 好友，进入用户详情页
                    Intent intent = new Intent(AddFriendsBySearchActivity.this, UserInfoActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    startActivity(intent);
                } else {
                    String source = "";
                    if (user.getUserPhone().equals(keyword)) {
                        source = Constant.FRIENDS_SOURCE_BY_PHONE;
                    } else if (user.getUserWxId().equals(keyword)) {
                        source = Constant.FRIENDS_SOURCE_BY_WX_ID;
                    }
                    // 陌生人，进入陌生人详情页
                    Intent intent = new Intent(AddFriendsBySearchActivity.this, StrangerUserInfoActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    intent.putExtra("source", source);
                    startActivity(intent);
                }
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(AddFriendsBySearchActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(AddFriendsBySearchActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(AddFriendsBySearchActivity.this,
                                R.string.user_not_exists, Toast.LENGTH_SHORT)
                                .show();
                        break;
                }

            }
        });
    }

    /**
     * 显示键盘
     *
     * @param editText 输入框
     */
    public void showKeyboard(final EditText editText) {
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
