package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.SearchNews;

import java.util.List;

/**
 * 搜索-新闻
 *
 * @author zhou
 */
public class SearchNewsAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchNews> mSearchNewsList;

    public SearchNewsAdapter(Context context, List<SearchNews> searchNewsList) {
        this.mContext = context;
        this.mSearchNewsList = searchNewsList;
    }

    @Override
    public int getCount() {
        return mSearchNewsList.size();
    }

    @Override
    public SearchNews getItem(int position) {
        return mSearchNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_news, null);
        return convertView;
    }
}
