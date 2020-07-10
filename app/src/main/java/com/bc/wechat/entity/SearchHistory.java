package com.bc.wechat.entity;

import com.bc.wechat.utils.CommonUtil;
import com.orm.SugarRecord;

/**
 * 搜索历史
 *
 * @author zhou
 */
public class SearchHistory extends SugarRecord {
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 查询时间
     */
    private String createTime;

    public SearchHistory() {

    }

    public SearchHistory(String keyword) {
        this.keyword = keyword;
        this.createTime = CommonUtil.now();
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
}
