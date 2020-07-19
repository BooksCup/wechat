package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.SearchHistory;

import java.util.List;

/**
 * 搜索-新闻
 *
 * @author zhou
 */
public class SearchNewsAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchHistory> mSearchNewsList;

    public SearchNewsAdapter(Context context, List<SearchHistory> searchNewsList) {
        this.mContext = context;
        this.mSearchNewsList = searchNewsList;
    }

    @Override
    public int getCount() {
        return mSearchNewsList.size();
    }

    @Override
    public SearchHistory getItem(int position) {
        return mSearchNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_news, null);

            viewHolder = new ViewHolder();
            viewHolder.mSearchNewsTv = convertView.findViewById(R.id.tv_search_news);
            viewHolder.mHotSearchTv = convertView.findViewById(R.id.tv_hot_search);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final SearchHistory searchNews = mSearchNewsList.get(position);
        viewHolder.mSearchNewsTv.setText(searchNews.getKeyword());

        if (searchNews.getCount() >= Constant.HOT_SEARCH_THRESHOLD) {
            viewHolder.mHotSearchTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mHotSearchTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView mSearchNewsTv;
        TextView mHotSearchTv;
    }
}
