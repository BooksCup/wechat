package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
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
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

public class UpdateNickNameActivity extends FragmentActivity {
    private EditText mNickNameEt;
    private TextView mSaveTv;
    private VolleyUtil volleyUtil;
    LoadingDialog dialog;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);
        PreferencesUtil.getInstance().init(this);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new LoadingDialog(UpdateNickNameActivity.this);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage(getString(R.string.saving));
                dialog.show();
                String userId = user.getUserId();
                String userNickName = mNickNameEt.getText().toString();
                updateUserNickName(userId, userNickName);
            }
        });
    }

    private void initView() {
        mNickNameEt = findViewById(R.id.et_nick);
        mSaveTv = findViewById(R.id.tv_save);

        mNickNameEt.setText(user.getUserNickName());
        // 光标移至最后
        CharSequence charSequence = mNickNameEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mNickNameEt.addTextChangedListener(new TextChange());
    }

    public void back(View view) {
        finish();
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newNickName = mNickNameEt.getText().toString();
            String oldNickName = user.getUserNickName();
            // 是否填写
            boolean isNickNameHasText = newNickName.length() > 0;
            // 是否修改
            boolean isNickNameChanged = !oldNickName.equals(newNickName);

            if (isNickNameHasText && isNickNameChanged) {
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

    private void updateUserNickName(String userId, final String userNickName) {
        String url = Constant.BASE_URL + "users/" + userId + "/userNickName";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userNickName", userNickName);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                setResult(RESULT_OK);
                user.setUserNickName(userNickName);
                PreferencesUtil.getInstance().setUser(user);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(UpdateNickNameActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(UpdateNickNameActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
