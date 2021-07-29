package com.bc.wechat.activity;

import android.content.Intent;
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
import com.android.volley.TimeoutError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索好友
 *
 * @author zhou
 */
public class AddFriendsBySearchActivity extends BaseActivity {

    private static final String TAG = "AddFriendsBySearch";

    @BindView(R.id.et_search)
    EditText mSearchEt;

    @BindView(R.id.rl_search)
    RelativeLayout mSearchRl;

    @BindView(R.id.tv_search)
    TextView mSearchTv;

    @BindView(R.id.iv_clear)
    ImageView mClearIv;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;
    UserDao mUserDao;

    @Override
    public int getContentView() {
        return R.layout.activity_add_friends_by_search;
    }

    @Override
    public void initView() {
        initStatusBar();
    }

    @Override
    public void initListener() {
        mSearchEt.addTextChangedListener(new TextChange());
    }

    @Override
    public void initData() {
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(AddFriendsBySearchActivity.this);
        mUserDao = new UserDao();
        // 初始化弹出软键盘
        showKeyboard(mSearchEt);
    }

    @OnClick({R.id.rl_search, R.id.iv_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                mDialog.setMessage(getString(R.string.searching_for_user));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                String keyword = mSearchEt.getText().toString().trim();
                searchUser(keyword);
                break;
            case R.id.iv_clear:
                mSearchEt.setText("");
                break;
        }
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

    /**
     * 搜索用户
     *
     * @param keyword 关键字
     */
    private void searchUser(final String keyword) {
        String userId = mUser.getUserId();
        final String url = Constant.BASE_URL + "users/searchForAddFriends?keyword=" + keyword + "&userId=" + userId;
        mVolleyUtil.httpGetRequest(url, response -> {
            Log.d(TAG, "server response: " + response);
            User user = JSON.parseObject(response, User.class);
            mUserDao.saveUser(user);
            Log.d(TAG, "userId:" + user.getUserId());
            if (userId.equals(user.getUserId())) {
                Intent intent = new Intent(AddFriendsBySearchActivity.this, UserInfoMyActivity.class);
                startActivity(intent);
            } else {
                if (Constant.IS_FRIEND.equals(user.getIsFriend())) {
                    // 好友，进入用户详情页
                    Intent intent = new Intent(AddFriendsBySearchActivity.this, UserInfoActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    startActivity(intent);
                } else {
                    String from = "";
                    if (user.getUserPhone().equals(keyword)) {
                        from = Constant.CONTACTS_FROM_PHONE;
                    } else if (user.getUserWxId().equals(keyword)) {
                        from = Constant.CONTACTS_FROM_WX_ID;
                    }
                    // 陌生人，进入陌生人详情页
                    Intent intent = new Intent(AddFriendsBySearchActivity.this, UserInfoStrangerActivity.class);
                    intent.putExtra("contactId", user.getUserId());
                    intent.putExtra("from", from);
                    startActivity(intent);
                }
            }
            mDialog.dismiss();
        }, volleyError -> {
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