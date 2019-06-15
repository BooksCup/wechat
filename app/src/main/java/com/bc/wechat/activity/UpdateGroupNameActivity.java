package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
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
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;

public class UpdateGroupNameActivity extends FragmentActivity {
    String groupId;
    String oldGroupName;
    private EditText mGroupNameEt;
    private TextView mSaveTv;

    private VolleyUtil volleyUtil;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_name);
        volleyUtil = VolleyUtil.getInstance(this);
        loadingDialog = new LoadingDialog(this);
        initView();
    }

    private void initView() {
        mGroupNameEt = findViewById(R.id.et_group_name);
        mSaveTv = findViewById(R.id.tv_save);

        groupId = getIntent().getStringExtra("groupId");
        Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));
        GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
        oldGroupName = groupInfo.getGroupName();
        if (TextUtils.isEmpty(groupInfo.getGroupDescription())) {
            mGroupNameEt.setHint(groupInfo.getGroupName());
        } else {
            mGroupNameEt.setText(groupInfo.getGroupName());
            // 光标移至最后
            CharSequence charSequence = mGroupNameEt.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        }
        mGroupNameEt.addTextChangedListener(new TextChange());

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.setMessage("正在保存");
                loadingDialog.show();
                final String groupName = mGroupNameEt.getText().toString();
                updateGroupName(groupId, groupName);
            }
        });
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
            String newGroupName = mGroupNameEt.getText().toString();
            // 是否填写
            boolean isGroupNameHasText = newGroupName.length() > 0;
            // 是否修改
            boolean isGroupNameChanged = !oldGroupName.equals(newGroupName);

            if (isGroupNameHasText && isGroupNameChanged) {
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

    private void updateGroupName(String groupId, final String groupName) {
        String url = Constant.BASE_URL + "groups/" + groupId + "/groupName";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("groupName", groupName);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                loadingDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("groupName", groupName);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadingDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(UpdateGroupNameActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(UpdateGroupNameActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
