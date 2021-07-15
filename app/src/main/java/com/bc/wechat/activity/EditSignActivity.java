package com.bc.wechat.activity;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 修改个性签名
 *
 * @author zhou
 */
public class EditSignActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_sign)
    EditText mSignEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.tv_sign_length)
    TextView mSignLengthTv;

    final int maxSignLength = 30;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    public int getContentView() {
        return R.layout.activity_edit_sign;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mSignEt.addTextChangedListener(new TextChange());
        mSaveTv.setOnClickListener(view -> {
            mDialog.setMessage(getString(R.string.saving));
            mDialog.show();
            String userId = mUser.getUserId();
            String userNickName = mSignEt.getText().toString();
            updateUserSign(userId, userNickName);
        });
    }

    @Override
    public void initData() {
        PreferencesUtil.getInstance().init(this);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(EditSignActivity.this);
        mUser = PreferencesUtil.getInstance().getUser();

        mSignEt.setText(mUser.getUserSign());
        // 剩余可编辑字数
        int leftSignLength = maxSignLength - mSignEt.length();
        if (leftSignLength >= 0) {
            mSignLengthTv.setText(String.valueOf(leftSignLength));
        }

        // 光标移至最后
        CharSequence charSequence = mSignEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newSign = mSignEt.getText().toString();
            String oldSign = mUser.getUserSign() == null ? "" : mUser.getUserSign();
            // 是否填写
            boolean isSignHasText = mSignEt.length() > 0;
            // 是否修改
            boolean isSignChanged = !oldSign.equals(newSign);

            if (isSignHasText && isSignChanged) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }

            int leftSignLength = maxSignLength - mSignEt.length();
            if (leftSignLength >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLength));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 是否填写
            int leftSignLength = maxSignLength - mSignEt.length();
            if (leftSignLength >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLength));
            }
        }
    }

    public void back(View view) {
        finish();
    }

    private void updateUserSign(final String userId, final String userSign) {
        String url = Constant.BASE_URL + "users/" + userId + "/userSign";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userSign", userSign);

        mVolleyUtil.httpPutRequest(url, paramMap, response -> {
            mDialog.dismiss();
            mUser.setUserSign(userSign);
            PreferencesUtil.getInstance().setUser(mUser);
            finish();
        }, volleyError -> {
            mDialog.dismiss();
            if (volleyError instanceof NetworkError) {
                Toast.makeText(EditSignActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            } else if (volleyError instanceof TimeoutError) {
                Toast.makeText(EditSignActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
