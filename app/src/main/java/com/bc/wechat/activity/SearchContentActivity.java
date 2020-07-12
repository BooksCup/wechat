package com.bc.wechat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.SearchHistoryAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.SearchHistoryDao;
import com.bc.wechat.entity.SearchHistory;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜一搜
 *
 * @author zhou
 */
public class SearchContentActivity extends BaseActivity implements View.OnClickListener {

    private EditText mSearchEt;
    private ImageView mClearIv;
    private RelativeLayout mClearSearchHistoryRl;
    private ListView mSearchHistoryLv;

    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchHistoryAdapter.ClickListener mClickListener;
    private List<SearchHistory> mSearchHistoryList;

    private User mUser;
    private VolleyUtil mVolleyUtil;
    private SearchHistoryDao mSearchHistoryDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(SearchContentActivity.this, R.color.status_bar_color_white);

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mSearchHistoryDao = new SearchHistoryDao();


        mClickListener = new SearchHistoryAdapter.ClickListener() {
            @Override
            public void onClick(Object... objects) {
                int searchHistoryListSize = (int) objects[0];
                if (searchHistoryListSize > 0) {
                    mClearSearchHistoryRl.setVisibility(View.VISIBLE);
                } else {
                    mClearSearchHistoryRl.setVisibility(View.GONE);
                }
            }
        };

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mClearIv = findViewById(R.id.iv_clear);
        mClearSearchHistoryRl = findViewById(R.id.rl_clear_search_history);
        mSearchHistoryLv = findViewById(R.id.lv_search_history);

        mSearchHistoryList = mSearchHistoryDao.getSearchHistoryList(5);
        if (null != mSearchHistoryList && mSearchHistoryList.size() > 0) {
            mClearSearchHistoryRl.setVisibility(View.VISIBLE);
        } else {
            mClearSearchHistoryRl.setVisibility(View.GONE);
        }

        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mSearchHistoryList, mClickListener);
        mSearchHistoryLv.setAdapter(mSearchHistoryAdapter);

        mSearchEt.addTextChangedListener(new TextChange());
        mClearIv.setOnClickListener(this);
        mClearSearchHistoryRl.setOnClickListener(this);

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 按下搜索
                    // 隐藏软键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(SearchContentActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    String keyword = mSearchEt.getText().toString();
                    SearchHistory searchHistory = new SearchHistory(keyword);
                    mSearchHistoryDao.saveSearchHistory(searchHistory);
                    saveSearchHistory(keyword);
                }
                // 返回true，保留软键盘。false，隐藏软键盘
                return true;
            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean searchHasText = mSearchEt.getText().toString().length() > 0;
            if (searchHasText) {
                mClearIv.setVisibility(View.VISIBLE);
            } else {
                mClearIv.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear:
                mSearchEt.setText("");
                break;
            case R.id.rl_clear_search_history:
                mSearchHistoryDao.clearSearchHistory();
                mSearchHistoryList.clear();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mClearSearchHistoryRl.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 保存搜索关键字
     *
     * @param keyword 搜索关键字
     */
    private void saveSearchHistory(String keyword) {
        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/searchHistory";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }
}
