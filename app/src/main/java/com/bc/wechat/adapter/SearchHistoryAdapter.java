package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.SearchHistory;

import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchHistory> mSearchHistoryList;

    public SearchHistoryAdapter(Context context, List<SearchHistory> searchHistoryList) {
        this.mContext = context;
        this.mSearchHistoryList = searchHistoryList;
    }

    @Override
    public int getCount() {
        return mSearchHistoryList.size();
    }

    @Override
    public SearchHistory getItem(int position) {
        return mSearchHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_history, null);

            viewHolder = new ViewHolder();
            viewHolder.mSearchHistoryTv = convertView.findViewById(R.id.tv_search_history);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchHistory searchHistory = mSearchHistoryList.get(position);
        viewHolder.mSearchHistoryTv.setText(searchHistory.getKeyword());
        return convertView;
    }

    class ViewHolder {
        TextView mSearchHistoryTv;
    }
}
