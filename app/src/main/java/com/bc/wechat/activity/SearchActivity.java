package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.SearchNewsAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.SearchHistory;
import com.bc.wechat.utils.VolleyUtil;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 搜一搜
 *
 * @author zhou
 */
public class SearchActivity extends BaseActivity2 {

    @BindView(R.id.et_search)
    EditText mSearchEt;

    @BindView(R.id.lv_search_news)
    ListView mSearchNewsLv;

    SearchNewsAdapter mSearchNewsAdapter;
    VolleyUtil mVolleyUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);

        initView();
    }

    private void initView() {

        getHotSearchHistoryList();

        mSearchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(SearchActivity.this, SearchContentActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }


    /**
     * 获取搜索热词
     */
    private void getHotSearchHistoryList() {
        String url = Constant.BASE_URL + "searchHistory/hot";

        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final List<SearchHistory> searchHistoryList = JSONArray.parseArray(response, SearchHistory.class);
                mSearchNewsAdapter = new SearchNewsAdapter(SearchActivity.this, searchHistoryList);
                mSearchNewsLv.setAdapter(mSearchNewsAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }
}
