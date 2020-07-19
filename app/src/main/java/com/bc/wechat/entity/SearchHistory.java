package com.bc.wechat.entity;

import com.orm.SugarRecord;

/**
 * 搜索历史
 *
 * @author zhou
 */
public class SearchHistory extends SugarRecord {
    private String userId;
    private String keyword;
    private String createTime;
    private Integer count;

    public SearchHistory() {

    }

    public SearchHistory(String keyword) {
        this.keyword = keyword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
