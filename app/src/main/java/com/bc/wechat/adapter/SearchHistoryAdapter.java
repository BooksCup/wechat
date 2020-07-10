package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;

import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mSearchHistoryList;

    public SearchHistoryAdapter(Context context, List<String> searchHistoryList) {
        this.mContext = context;
        this.mSearchHistoryList = searchHistoryList;
    }

    @Override
    public int getCount() {
        return mSearchHistoryList.size();
    }

    @Override
    public String getItem(int position) {
        return mSearchHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_history, null);
        return convertView;
    }
}
