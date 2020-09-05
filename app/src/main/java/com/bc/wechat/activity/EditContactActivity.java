package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.DensityUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
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

    @BindView(R.id.et_desc)
    EditText mDescEt;

    private VolleyUtil mVolleyUtil;

    // 添加电话
    @BindView(R.id.rl_add_phone_tmp)
    RelativeLayout mAddPhoneTmpRl;

    @BindView(R.id.rl_add_phone)
    RelativeLayout mAddPhoneRl;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.iv_clear_phone)
    ImageView mClearPhoneIv;

    // 保存
    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.tv_add_tag)
    TextView mAddTagTv;

    @BindView(R.id.fl_add_tag)
    FlowLayout mAddTagFl;

    @BindView(R.id.ll_add_phone)
    LinearLayout mAddPhoneLl;

    private LinearLayout.LayoutParams mParams;

    private String mContactId;
    private User mContact;
    private User mUser;
    private UserDao mUserDao;
    LoadingDialog mDialog;


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
        mDialog = new LoadingDialog(EditContactActivity.this);
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

        mPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString();
                if (!TextUtils.isEmpty(phone)) {
                    if (mAddPhoneLl.getChildCount() <= 1) {
                        addPhoneView();
                    }
                    mClearPhoneIv.setVisibility(View.VISIBLE);
                } else {
                    mClearPhoneIv.setVisibility(View.GONE);
                }
            }
        });
    }

    public void back(View view) {
        finish();
    }

    /**
     * 设置联系人
     *
     * @param userId    用户ID
     * @param contactId 联系人ID
     * @param alias     联系人备注名
     * @param mobiles   联系人电话(可以多个号码,json格式)
     * @param desc      联系人描述
     */
    private void editContact(final String userId, final String contactId, final String alias,
                             final String mobiles, final String desc) {
        String url = Constant.BASE_URL + "users/" + userId + "/contacts/" + contactId;

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("contactAlias", alias);
        paramMap.put("contactMobiles", mobiles);
        paramMap.put("contactDesc", desc);

        mVolleyUtil.httpPutRequest(url, paramMap, response -> {
            mDialog.dismiss();
            User user = mUserDao.getUserById(contactId);
            user.setUserFriendRemark(alias);
            user.setUserFriendDesc(desc);
            user.setUserFriendPhone(mobiles);
            mUserDao.saveUser(user);
            finish();
        }, volleyError -> {
            mDialog.dismiss();
            showNoTitleAlertDialog(EditContactActivity.this, "设置联系人失败", "确定");
        });
    }

    @OnClick({R.id.rl_add_tag, R.id.iv_clear_phone, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_add_tag:
                Intent intent = new Intent(EditContactActivity.this, AddTagActivity.class);
                intent.putExtra("contactId", mContactId);
                startActivity(intent);
                break;
            case R.id.iv_clear_phone:
                mPhoneEt.setText("");
                break;
            case R.id.tv_save:
                String remark = mRemarkEt.getText().toString();
                List<String> mobileList = getPhoneList();
                String mobiles = JSON.toJSONString(mobileList);
                String desc = mDescEt.getText().toString();

                mDialog.setMessage("设置中...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                editContact(mUser.getUserId(), mContactId, remark, mobiles, desc);
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

    private void addPhoneView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this, 58));
        View view = LayoutInflater.from(this).inflate(R.layout.item_contact_phone, null);
        view.setLayoutParams(lp);
        EditText phoneEt = view.findViewById(R.id.et_phone);
        ImageView clearPhoneIv = view.findViewById(R.id.iv_clear_phone);
        phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString();
                if (!TextUtils.isEmpty(phone)) {
                    if (view == mAddPhoneLl.getChildAt(mAddPhoneLl.getChildCount() - 1)) {
                        addPhoneView();
                    }
                    clearPhoneIv.setVisibility(View.VISIBLE);
                } else {
                    mAddPhoneLl.removeView(view);
                    View lastView = mAddPhoneLl.getChildAt(mAddPhoneLl.getChildCount() - 1);
                    EditText lastPhoneEt = lastView.findViewById(R.id.et_phone);
                    lastPhoneEt.requestFocus();
                    clearPhoneIv.setVisibility(View.GONE);
                }
            }
        });
        clearPhoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneEt.setText("");
            }
        });
        mAddPhoneLl.addView(view);
    }

    /**
     * 获取所有的电话号码
     *
     * @return 所有的电话号码
     */
    private List<String> getPhoneList() {
        List<String> phoneList = new ArrayList<>();
        int childCount = mAddPhoneLl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mAddPhoneLl.getChildAt(i);
            EditText phoneEt = view.findViewById(R.id.et_phone);
            phoneList.add(phoneEt.getText().toString());
        }
        return phoneList;
    }
}
