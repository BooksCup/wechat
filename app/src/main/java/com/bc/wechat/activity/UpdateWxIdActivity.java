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
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

public class UpdateWxIdActivity extends FragmentActivity {
    private EditText mWxIdEt;
    private TextView mSaveTv;
    private VolleyUtil volleyUtil;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wx_id);

        PreferencesUtil.getInstance().init(this);
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(UpdateWxIdActivity.this);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage(getString(R.string.saving));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                String userId = PreferencesUtil.getInstance().getUserId();
                String userWxId = mWxIdEt.getText().toString();
                updateUserWxId(userId, userWxId);
            }
        });
    }

    private void initView() {
        mWxIdEt = findViewById(R.id.et_wx_id);
        mSaveTv = findViewById(R.id.tv_save);

        mWxIdEt.setText(PreferencesUtil.getInstance().getUserWxId());
        // 光标移至最后
        CharSequence charSequence = mWxIdEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mWxIdEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newWxId = mWxIdEt.getText().toString();
            String oldWxId = PreferencesUtil.getInstance().getUserNickName();
            // 是否填写
            boolean isNickNameHasText = mWxIdEt.length() > 0;
            // 是否修改
            boolean isNickNameChanged = !oldWxId.equals(newWxId);

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

    public void back(View view) {
        finish();
    }

    private void updateUserWxId(final String userId, final String userWxId) {
        String url = Constant.BASE_URL + "users/" + userId + "/userWxId";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userWxId", userWxId);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                setResult(RESULT_OK);
                PreferencesUtil.getInstance().setUserWxId(userWxId);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError instanceof NetworkError) {
                    Toast.makeText(UpdateWxIdActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                dialog.dismiss();
            }
        });
    }
}
