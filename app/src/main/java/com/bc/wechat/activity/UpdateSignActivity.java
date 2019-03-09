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

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;

public class UpdateSignActivity extends FragmentActivity {

    private EditText mSignEt;
    private TextView mSaveTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);
        initView();
    }

    private void initView() {
        mSignEt = findViewById(R.id.et_sign);
        mSaveTv = findViewById(R.id.tv_save);

        mSignEt.setText(PreferencesUtil.getInstance().getUserWxId());
        // 光标移至最后
        CharSequence charSequence = mSignEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mSignEt.addTextChangedListener(new  TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newWxId = mSignEt.getText().toString();
            String oldWxId = PreferencesUtil.getInstance().getUserNickName();
            // 是否填写
            boolean isNickNameHasText = mSignEt.length() > 0;
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
}
