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

import com.bc.wechat.R;
import com.bc.wechat.utils.DensityUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private TagAdapter<String> mTagAdapter;//标签适配器
    private LinearLayout.LayoutParams params;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.bind(this);
        initStatusBar();

        initView();
        initData();
        initEdittext();
        initAllLeblLayout();
    }


    /**
     * 初始化View
     */
    private void initView() {
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginLeft = DensityUtil.dip2px(AddTagActivity.this, 10);
        int marginTop = DensityUtil.dip2px(AddTagActivity.this, 10);
        params.setMargins(marginLeft, marginTop, 0, 0);
        mAddTagFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTextContent = editText.getText().toString();
                if (TextUtils.isEmpty(editTextContent)) {
                    tagNormal();
                } else {
                    addTag(editText);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化上面标签
        mTagList.add("同事");
        mTagList.add("亲人");
        mTagList.add("同学");
        mTagList.add("朋友");
        mTagList.add("知己");
        //初始化下面标签列表
        mAllTagList.addAll(mTagList);
        mAllTagList.add("异性朋友");
        mAllTagList.add("高中同学");
        mAllTagList.add("大学同学");
        mAllTagList.add("社会朋友");

        for (int i = 0; i < mTagList.size(); i++) {
            editText = new EditText(getApplicationContext());
            editText.setText(mTagList.get(i));
            // 添加标签
            addTag(editText);
        }

    }

    /**
     * 初始化默认的添加标签
     */
    private void initEdittext() {
        editText = new EditText(getApplicationContext());
        editText.setHint("添加标签");
        //设置固定宽度
        editText.setMinEms(4);
        editText.setTextSize(TAG_TEXT_SIZE);
        //设置shape
        editText.setBackgroundResource(R.drawable.label_add);
        editText.setHintTextColor(Color.parseColor("#b4b4b4"));
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setLayoutParams(params);
        //添加到layout中
        mAddTagFl.addView(editText);
        editText.addTextChangedListener(new TextWatcher() {
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
    private void initAllLeblLayout() {
        //初始化适配器
        mTagAdapter = new TagAdapter<String>(mAllTagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.flag_adapter,
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
                    editText.setText(mAllTagList.get(position));
                    addTag(editText);
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
                    editText.setText(mAllTagList.get(position));
                    addTag(editText);
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
        String editTextContent = editText.getText().toString();
        //判断输入是否为空
        if (editTextContent.equals(""))
            return true;
        //判断是否重复
        for (TextView tag : mTagTextList) {
            String tempStr = tag.getText().toString();
            if (tempStr.equals(editTextContent)) {
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
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curIndex = mTagTextList.indexOf(temp);
                if (!mTagStateList.get(curIndex)) {
                    //显示 ×号删除
                    temp.setText(temp.getText() + " ×");
                    temp.setBackgroundResource(R.drawable.label_del);
                    temp.setTextColor(Color.parseColor("#ffffff"));
                    //修改选中状态
                    mTagStateList.set(curIndex, true);
                } else {
                    delByTest(temp.getText().toString());
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
     * 根据字符删除标签
     *
     * @param text
     */
    private void delByTest(String text) {

        for (int i = 0; i < mAllTagList.size(); i++) {
            String a = mAllTagList.get(i) + " ×";
            if (a.equals(text)) {
                set.remove(i);
            }
        }
        mTagAdapter.setSelectedList(set);//重置选中的标签

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
        textView.setLayoutParams(params);
        return textView;
    }
}
