package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.DensityUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑联系人(设置备注和标签)
 *
 * @author zhou
 */
public class EditContactActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_remark)
    EditText mRemarkEt;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_desc)
    EditText mDescEt;

    private VolleyUtil mVolleyUtil;

    // 添加电话
    @BindView(R.id.rl_add_phone_tmp)
    RelativeLayout mAddPhoneTmpRl;

    @BindView(R.id.rl_add_phone)
    RelativeLayout mAddPhoneRl;

    // 保存
    @BindView(R.id.tv_save)
    TextView mSaveTv;
    private User mUser;

    private UserDao mUserDao;

    @BindView(R.id.tv_add_tag)
    TextView mAddTagTv;

    @BindView(R.id.fl_add_tag)
    FlowLayout mAddTagFl;

    private LinearLayout.LayoutParams mParams;

    private String mContactId;
    private User mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        ButterKnife.bind(this);

        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mUserDao = new UserDao();
        mContactId = getIntent().getStringExtra("userId");
        initView();
    }

    private void initView() {
        mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginLeft = DensityUtil.dip2px(EditContactActivity.this, 10);
        int marginTop = DensityUtil.dip2px(EditContactActivity.this, 10);
        mParams.setMargins(marginLeft, marginTop, 0, 0);
        loadTags();

        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        final String isFriend = getIntent().getStringExtra("isFriend");
        mContact = mUserDao.getUserById(mContactId);


        if (TextUtils.isEmpty(mContact.getUserFriendRemark())) {
            // 无备注，展示昵称
            mRemarkEt.setText(mContact.getUserNickName());
        } else {
            // 有备注，展示备注
            mRemarkEt.setText(mContact.getUserFriendRemark());
        }

        mPhoneEt.setText(mContact.getUserFriendPhone());
        mDescEt.setText(mContact.getUserFriendDesc());

        if (Constant.IS_NOT_FRIEND.equals(isFriend)) {
            // 非好友不能添加电话
            mAddPhoneTmpRl.setVisibility(View.GONE);
            mAddPhoneRl.setVisibility(View.GONE);
        }
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

    @OnClick({R.id.rl_add_tag, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_add_tag:
                Intent intent = new Intent(EditContactActivity.this, AddTagActivity.class);
                intent.putExtra("contactId", mContactId);
                startActivity(intent);
                break;
            case R.id.tv_save:
                String remark = mRemarkEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String desc = mDescEt.getText().toString();
                setRemarks(mUser.getUserId(), mContactId, remark, phone, desc);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTags();
    }

    /**
     * 创建一个正常状态的标签
     *
     * @param label
     * @return
     */
    private TextView getTag(String label) {
        TextView textView = new TextView(getApplicationContext());
        textView.setTextSize(14);
        textView.setBackgroundResource(R.drawable.label_normal);
        textView.setTextColor(getColor(R.color.register_btn_bg_enable));
        textView.setText(label);
        textView.setLayoutParams(mParams);
        return textView;
    }

    private void loadTags() {
        mContact = mUserDao.getUserById(mContactId);
        List<String> selectedTagList = mContact.getUserContactTagList();

        if (null != selectedTagList && selectedTagList.size() > 0) {
            mAddTagTv.setVisibility(View.GONE);
            mAddTagFl.setVisibility(View.VISIBLE);
            mAddTagFl.removeAllViews();
            for (String selectedTag : selectedTagList) {
                // 添加标签
                final TextView temp = getTag(selectedTag);
                mAddTagFl.addView(temp);
            }
        } else {
            mAddTagTv.setVisibility(View.VISIBLE);
            mAddTagFl.setVisibility(View.GONE);
        }
    }
}
