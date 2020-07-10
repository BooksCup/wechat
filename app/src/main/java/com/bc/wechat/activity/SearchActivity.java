package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.SearchNewsAdapter;
import com.bc.wechat.entity.SearchNews;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜一搜
 *
 * @author zhou
 */
public class SearchActivity extends BaseActivity {

    private EditText mSearchEt;
    private ListView mSearchNewsLv;
    private SearchNewsAdapter mSearchNewsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initStatusBar();
        initView();
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mSearchNewsLv = findViewById(R.id.lv_search_news);

        final List<SearchNews> searchNewsList = new ArrayList<>();
        searchNewsList.add(new SearchNews());
        searchNewsList.add(new SearchNews());
        searchNewsList.add(new SearchNews());
        searchNewsList.add(new SearchNews());
        searchNewsList.add(new SearchNews());

        mSearchNewsAdapter = new SearchNewsAdapter(this, searchNewsList);
        mSearchNewsLv.setAdapter(mSearchNewsAdapter);

        mSearchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, SearchContentActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
