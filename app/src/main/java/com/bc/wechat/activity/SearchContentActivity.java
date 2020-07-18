package com.bc.wechat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.SearchContentAdapter;
import com.bc.wechat.adapter.SearchHistoryAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.SearchHistoryDao;
import com.bc.wechat.entity.FileItem;
import com.bc.wechat.entity.SearchHistory;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
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
    private RelativeLayout mSearchHistoryRl;
    private RelativeLayout mSearchContentRl;
    private ListView mSearchHistoryLv;
    private ListView mSearchContentLv;

    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchContentAdapter mSearchContentAdapter;
    private SearchHistoryAdapter.ClickListener mClickListener;
    private List<SearchHistory> mSearchHistoryList;
    private List<FileItem> mSearchContentList;

    private User mUser;
    private VolleyUtil mVolleyUtil;
    private SearchHistoryDao mSearchHistoryDao;

    private RefreshLayout mRefreshLayout;
    int mPage = 1;

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
        mSearchHistoryRl = findViewById(R.id.rl_search_history);
        mSearchContentRl = findViewById(R.id.rl_search_content);
        mSearchHistoryLv = findViewById(R.id.lv_search_history);
        mSearchContentLv = findViewById(R.id.lv_search_content);

        // 上拉加载，下拉刷新
        mRefreshLayout = findViewById(R.id.srl_friends_circle);

        mSearchHistoryList = mSearchHistoryDao.getSearchHistoryList(5);
        if (null != mSearchHistoryList && mSearchHistoryList.size() > 0) {
            mClearSearchHistoryRl.setVisibility(View.VISIBLE);
        } else {
            mClearSearchHistoryRl.setVisibility(View.GONE);
        }

        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mSearchHistoryList, mClickListener);
        mSearchHistoryLv.setAdapter(mSearchHistoryAdapter);

        mSearchContentAdapter = new SearchContentAdapter(SearchContentActivity.this, new ArrayList<FileItem>());
        mSearchContentLv.setAdapter(mSearchContentAdapter);

        mSearchHistoryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchHistory searchHistory = mSearchHistoryList.get(position);
                mSearchEt.setText(searchHistory.getKeyword());
                mSearchHistoryRl.setVisibility(View.GONE);
                mSearchContentRl.setVisibility(View.VISIBLE);
                getSearchContentList(searchHistory.getKeyword(), 1, false);
            }
        });

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
                    if (!TextUtils.isEmpty(keyword.trim())) {
                        SearchHistory searchHistory = new SearchHistory(keyword);
                        mSearchHistoryDao.saveSearchHistory(searchHistory);
                        saveSearchHistory(keyword);

                        mSearchHistoryRl.setVisibility(View.GONE);
                        mSearchContentRl.setVisibility(View.VISIBLE);

                        getSearchContentList(keyword, mPage, false);
                    }
                }
                // 返回true，保留软键盘。false，隐藏软键盘
                return true;
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                // 下拉刷新
                mPage = 1;
                String keyword = mSearchEt.getText().toString();
                getSearchContentList(keyword, mPage, false);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                // 上拉加载
                mPage++;
                String keyword = mSearchEt.getText().toString();
                getSearchContentList(keyword, mPage, true);
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

    /**
     * 获取搜索内容
     *
     * @param keyword 关键字
     * @param page    分页数
     * @param isAdd
     */
    private void getSearchContentList(final String keyword, final int page, final boolean isAdd) {
        String url = Constant.BASE_URL + "fileItems/v4?searchKey=" + keyword + "&color=%2307C063&page=" + page;

        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isAdd) {
                    // 上拉加载
                    mRefreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    mRefreshLayout.finishRefresh();
                }

                List<FileItem> list = JSONArray.parseArray(response, FileItem.class);
                if (isAdd) {
                    // 上拉加载
                    mSearchContentAdapter.addData(list);
                } else {
                    // 下拉刷新
                    mSearchContentAdapter.setData(list);
                }
                mSearchContentAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isAdd) {
                    // 上拉加载
                    mRefreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }
}
