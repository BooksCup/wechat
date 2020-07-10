package com.bc.wechat.dao;

import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.SearchHistory;

import java.util.List;

/**
 * 搜索历史
 *
 * @author zhou
 */
public class SearchHistoryDao {

    /**
     * 保存搜索历史
     *
     * @param searchHistory 搜索历史
     */
    public void saveSearchHistory(SearchHistory searchHistory) {
        SearchHistory.save(searchHistory);
    }

    /**
     * 取前N条搜索历史记录
     *
     * @param pageSize N
     * @return 前N条搜索历史记录
     */
    public List<SearchHistory> getSearchHistoryList(int pageSize) {
        return FriendsCircle.findWithQuery(SearchHistory.class,
                "select * from search_history order by create_time desc limit ?", String.valueOf(pageSize));
    }
}
