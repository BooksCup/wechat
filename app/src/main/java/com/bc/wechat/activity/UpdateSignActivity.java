package com.bc.wechat.activity;

import android.app.ProgressDialog;
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
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

public class UpdateSignActivity extends FragmentActivity {

    private EditText mSignEt;
    private TextView mSaveTv;
    private TextView mSignLengthTv;
    final int maxSignLenth = 30;

    private VolleyUtil volleyUtil;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);

        PreferencesUtil.getInstance().init(this);
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(UpdateSignActivity.this);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage(getString(R.string.saving));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                String userId = PreferencesUtil.getInstance().getUserId();
                String userNickName = mSignEt.getText().toString();
                updateUserSign(userId, userNickName);
            }
        });

    }

    private void initView() {
        mSignEt = findViewById(R.id.et_sign);
        mSaveTv = findViewById(R.id.tv_save);
        mSignLengthTv = findViewById(R.id.tv_sign_length);

        mSignEt.setText(PreferencesUtil.getInstance().getUserSign());
        // 光标移至最后
        CharSequence charSequence = mSignEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mSignEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newSign = mSignEt.getText().toString();
            String oldSign = PreferencesUtil.getInstance().getUserSign();
            // 是否填写
            boolean isSignHasText = mSignEt.length() > 0;
            // 是否修改
            boolean isSignChanged = !oldSign.equals(newSign);

            if (isSignHasText && isSignChanged) {
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                mSaveTv.setTextColor(0xFFD0EFC6);
                mSaveTv.setEnabled(false);
            }

            int leftSignLenth = maxSignLenth - mSignEt.length();
            if (leftSignLenth >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLenth));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 是否填写
            int leftSignLenth = maxSignLenth - mSignEt.length();
            if (leftSignLenth >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLenth));
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

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                setResult(RESULT_OK);
                PreferencesUtil.getInstance().setUserSign(userSign);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(UpdateSignActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(UpdateSignActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
