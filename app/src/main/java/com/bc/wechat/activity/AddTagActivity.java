package com.bc.wechat.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加标签
 *
 * @author zhou
 */
public class AddTagActivity extends BaseActivity {
    private static final int TAG_TEXT_SIZE = 14;

    /**
     * 上面的FlowLayout
     */
    @BindView(R.id.fl_add_tag)
    FlowLayout mAddTagFl;

    /**
     * 所有标签的TagFlowLayout
     */
    @BindView(R.id.tfl_all_tag)
    TagFlowLayout mAllTagTfl;

    /**
     * 上面的标签列表
     */
    private List<String> mTagList = new ArrayList<>();

    /**
     * 所有标签列表
     */
    private List<String> mAllTagList = new ArrayList<>();

    /**
     * 标签内容
     */
    final List<TextView> mTagTextList = new ArrayList<>();

    /**
     * 标签状态
     */
    final List<Boolean> mTagStateList = new ArrayList<>();
    final Set<Integer> set = new HashSet<>();//存放选中的

    /**
     * 标签适配器
     */
    private TagAdapter<String> mTagAdapter;
    private LinearLayout.LayoutParams mParams;
    private EditText mDefaultEt;
    private VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    private UserDao mUserDao;

    private User mUser;
    /**
     * 联系人用户ID
     */
    private String mContactId;
    private User mContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.bind(this);
        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(AddTagActivity.this);
        mUserDao = new UserDao();

        mUser = PreferencesUtil.getInstance().getUser();
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);

        initView();
        initData();
        initEditText();
        initAllTagLayout();
    }

    public void back(View view) {
        finish();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginLeft = DensityUtil.dip2px(AddTagActivity.this, 10);
        int marginTop = DensityUtil.dip2px(AddTagActivity.this, 10);
        mParams.setMargins(marginLeft, marginTop, 0, 0);
        mAddTagFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTextContent = mDefaultEt.getText().toString();
                if (TextUtils.isEmpty(editTextContent)) {
                    tagNormal();
                } else {
                    addTag(mDefaultEt);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化好友标签
        mTagList = mContact.getUserContactTagList();
        // 初始化所有标签
        mAllTagList = mUser.getUserTagList();

        for (int i = 0; i < mTagList.size(); i++) {
            mDefaultEt = new EditText(getApplicationContext());
            mDefaultEt.setText(mTagList.get(i));
            // 添加标签
            addTag(mDefaultEt);
        }

    }

    /**
     * 初始化默认的添加标签
     */
    private void initEditText() {
        mDefaultEt = new EditText(getApplicationContext());
        mDefaultEt.setHint(getString(R.string.add_tag));
        //设置固定宽度
        mDefaultEt.setMinEms(4);
        mDefaultEt.setTextSize(TAG_TEXT_SIZE);
        //设置shape
        mDefaultEt.setBackgroundResource(R.drawable.label_add);
        mDefaultEt.setHintTextColor(Color.parseColor("#b4b4b4"));
        mDefaultEt.setTextColor(Color.parseColor("#000000"));
        mDefaultEt.setLayoutParams(mParams);
        //添加到layout中
        mAddTagFl.addView(mDefaultEt);
        mDefaultEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tagNormal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 初始化所有标签列表
     */
    private void initAllTagLayout() {
        //初始化适配器
        mTagAdapter = new TagAdapter<String>(mAllTagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_all_tag,
                        mAllTagTfl, false);
                tv.setText(s);
                return tv;
            }
        };

        mAllTagTfl.setAdapter(mTagAdapter);

        //根据上面标签来判断下面的标签是否含有上面的标签
        for (int i = 0; i < mTagList.size(); i++) {
            for (int j = 0; j < mAllTagList.size(); j++) {
                if (mTagList.get(i).equals(
                        mAllTagList.get(j))) {
                    mTagAdapter.setSelectedList(i);//设为选中
                }
            }
        }
        mTagAdapter.notifyDataChanged();

        //给下面的标签添加监听
        mAllTagTfl.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (mTagTextList.size() == 0) {
                    mDefaultEt.setText(mAllTagList.get(position));
                    addTag(mDefaultEt);
                    return false;
                }
                List<String> list = new ArrayList<>();
                for (int i = 0; i < mTagTextList.size(); i++) {
                    list.add(mTagTextList.get(i).getText().toString());
                }
                //如果上面包含点击的标签就删除
                if (list.contains(mAllTagList.get(position))) {
                    for (int i = 0; i < list.size(); i++) {
                        if (mAllTagList.get(position).equals(list.get(i))) {
                            mAddTagFl.removeView(mTagTextList.get(i));
                            mTagTextList.remove(i);
                        }
                    }

                } else {
                    mDefaultEt.setText(mAllTagList.get(position));
                    addTag(mDefaultEt);
                }

                return false;
            }
        });

        //已经选中的监听
        mAllTagTfl.setOnSelectListener(new TagFlowLayout.OnSelectListener() {

            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                set.clear();
                set.addAll(selectPosSet);
            }
        });
    }

    /**
     * 添加标签
     *
     * @param editText
     * @return
     */
    private boolean addTag(EditText editText) {
        String tagContent = editText.getText().toString();
        //判断输入是否为空
        if (tagContent.equals("")) {
            return true;
        }
        //判断是否重复
        for (TextView tagTv : mTagTextList) {
            String tempStr = tagTv.getText().toString();
            if (tempStr.equals(tagContent)) {
                editText.setText("");
                editText.requestFocus();
                return true;
            }
        }
        //添加标签
        final TextView temp = getTag(editText.getText().toString());
        mTagTextList.add(temp);
        mTagStateList.add(false);
        //添加点击事件，点击变成选中状态，选中状态下被点击则删除
        temp.setOnClickListener(v -> {
            int curIndex = mTagTextList.indexOf(temp);
            if (!mTagStateList.get(curIndex)) {
                //显示 ×号删除
                temp.setText(temp.getText() + " ×");
                temp.setBackgroundResource(R.drawable.label_del);
                temp.setTextColor(Color.parseColor("#ffffff"));
                //修改选中状态
                mTagStateList.set(curIndex, true);
            } else {
                deleteTagByText(temp.getText().toString());
                mAddTagFl.removeView(temp);
                mTagTextList.remove(curIndex);
                mTagStateList.remove(curIndex);
                for (int i = 0; i < mTagList.size(); i++) {
                    for (int j = 0; j < mTagTextList.size(); j++) {
                        if (mTagList.get(i).equals(
                                mTagTextList.get(j).getText())) {
                            mTagAdapter.setSelectedList(i);
                        }
                    }
                }
                mTagAdapter.notifyDataChanged();
            }
        });
        mAddTagFl.addView(temp);
        //让输入框在最后一个位置上
        editText.bringToFront();
        //清空编辑框
        editText.setText("");
        editText.requestFocus();
        return true;

    }


    /**
     * 根据标签内容删除标签
     *
     * @param text 标签内容
     */
    private void deleteTagByText(String text) {
        for (int i = 0; i < mAllTagList.size(); i++) {
            String a = mAllTagList.get(i) + " ×";
            if (a.equals(text)) {
                set.remove(i);
            }
        }
        // 重置选中的标签
        mTagAdapter.setSelectedList(set);
    }


    /**
     * 标签恢复到正常状态
     */
    private void tagNormal() {
        //输入文字时取消已经选中的标签
        for (int i = 0; i < mTagStateList.size(); i++) {
            if (mTagStateList.get(i)) {
                TextView tmp = mTagTextList.get(i);
                tmp.setText(tmp.getText().toString().replace(" ×", ""));
                mTagStateList.set(i, false);
                tmp.setBackgroundResource(R.drawable.label_normal);
                tmp.setTextColor(getColor(R.color.register_btn_bg_enable));
            }
        }
    }


    /**
     * 创建一个正常状态的标签
     *
     * @param label
     * @return
     */
    private TextView getTag(String label) {
        TextView textView = new TextView(getApplicationContext());
        textView.setTextSize(TAG_TEXT_SIZE);
        textView.setBackgroundResource(R.drawable.label_normal);
        textView.setTextColor(getColor(R.color.register_btn_bg_enable));
        textView.setText(label);
        textView.setLayoutParams(mParams);
        return textView;
    }

    @OnClick({R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                List<String> selectedTagList = new ArrayList<>();
                for (int i = 0; i < mTagTextList.size(); i++) {
                    selectedTagList.add(mTagTextList.get(i).getText().toString().replace(" ×", ""));
                }

                mDialog.setMessage("标签保存中...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                saveUserContactTags(mUser.getUserId(), mContactId, JSON.toJSONString(selectedTagList), JSON.toJSONString(mAllTagList));
                break;
        }
    }

    /**
     * 保存用户标签
     *
     * @param userId      用户ID
     * @param contactId   联系人用户ID
     * @param contactTags 联系人标签
     * @param tags        所有标签
     */
    private void saveUserContactTags(String userId, final String contactId, final String contactTags, final String tags) {
        String url = Constant.BASE_URL + "users/" + userId + "/userContactTags";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("contactId", contactId);
        paramMap.put("contactTags", contactTags);
        paramMap.put("tags", tags);

        mVolleyUtil.httpPostRequest(url, paramMap, response -> {
            mDialog.dismiss();
            // 用户标签保存至本地
            mContact.setUserContactTags(contactTags);
            mUserDao.saveUser(mContact);
            // 用户所有标签保存至SharePreference中
            String unionTags = getUnionTags(contactTags, tags);
            mUser.setUserTags(unionTags);
            PreferencesUtil.getInstance().setUser(mUser);
            finish();
        }, volleyError -> {
            mDialog.dismiss();
            showNoTitleAlertDialog(AddTagActivity.this, "添加标签失败", "确定");
        });
    }

    /**
     * 获取标签合集(json格式)
     *
     * @param firstTags  第一个标签集合(json格式)
     * @param secondTags 第二个标签集合(json格式)
     * @return 标签合集(json格式)
     */
    private String getUnionTags(String firstTags, String secondTags) {
        List<String> firstTagList;
        List<String> secondTagList;
        Set<String> unionTagSet = new LinkedHashSet<>();
        List<String> unionTagList = new ArrayList<>();
        try {
            firstTagList = JSON.parseArray(firstTags, String.class);
        } catch (Exception e) {
            firstTagList = new ArrayList<>();
        }
        try {
            secondTagList = JSON.parseArray(secondTags, String.class);
        } catch (Exception e) {
            secondTagList = new ArrayList<>();
        }
        if (null != firstTagList) {
            for (String firstTag : firstTagList) {
                unionTagSet.add(firstTag);
            }
        }

        if (null != secondTagList) {
            for (String secondTag : secondTagList) {
                unionTagSet.add(secondTag);
            }
        }
        unionTagList.addAll(unionTagSet);
        return JSON.toJSONString(unionTagList);
    }
}
