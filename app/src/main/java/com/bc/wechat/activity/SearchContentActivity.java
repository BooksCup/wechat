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
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.SearchHistoryAdapter;
import com.bc.wechat.dao.SearchHistoryDao;
import com.bc.wechat.entity.SearchHistory;
import com.bc.wechat.utils.StatusBarUtil;

import java.util.List;

/**
 * 搜一搜
 *
 * @author zhou
 */
public class SearchContentActivity extends BaseActivity {

    private EditText mSearchEt;
    private ImageView mClearIv;

    private ListView mSearchHistoryLv;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchHistoryDao mSearchHistoryDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(SearchContentActivity.this, R.color.status_bar_color_white);

        mSearchHistoryDao = new SearchHistoryDao();

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);

        mClearIv = findViewById(R.id.iv_clear);
        mSearchHistoryLv = findViewById(R.id.lv_search_history);

        List<SearchHistory> searchHistoryList = mSearchHistoryDao.getSearchHistoryList(5);

        mSearchHistoryAdapter = new SearchHistoryAdapter(this, searchHistoryList);
        mSearchHistoryLv.setAdapter(mSearchHistoryAdapter);

        mSearchEt.addTextChangedListener(new TextChange());
        mClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEt.setText("");
            }
        });

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
}
