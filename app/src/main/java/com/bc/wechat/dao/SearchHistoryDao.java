package com.bc.wechat.dao;

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

        List<SearchHistory> searchHistoryList = SearchHistory.find(SearchHistory.class, "keyword = ?", searchHistory.getKeyword());
        SearchHistory.deleteInTx(searchHistoryList);
        SearchHistory.save(searchHistory);

    }

    /**
     * 取前N条搜索历史记录
     *
     * @param pageSize N
     * @return 前N条搜索历史记录
     */
    public List<SearchHistory> getSearchHistoryList(int pageSize) {
        return SearchHistory.findWithQuery(SearchHistory.class,
                "select * from search_history order by id desc limit ?", String.valueOf(pageSize));
    }

    /**
     * 根据关键字删除单条搜索
     *
     * @param keyword 关键字
     */
    public void deleteSearchHistoryByKeyword(String keyword) {
        String sql = "delete from search_history where keyword = ?";
        SearchHistory.executeQuery(sql, keyword);
    }

    /**
     * 清除搜索历史
     */
    public void clearSearchHistory() {
        SearchHistory.deleteAll(SearchHistory.class);
    }
}
