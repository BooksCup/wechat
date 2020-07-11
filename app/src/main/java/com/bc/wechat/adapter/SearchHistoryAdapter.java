package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.dao.SearchHistoryDao;
import com.bc.wechat.entity.SearchHistory;

import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchHistory> mSearchHistoryList;
    private SearchHistoryDao mSearchHistoryDao;
    private ClickListener mClickListener;

    public SearchHistoryAdapter(Context context, List<SearchHistory> searchHistoryList, ClickListener clickListener) {
        this.mContext = context;
        this.mSearchHistoryList = searchHistoryList;
        this.mClickListener = clickListener;
        mSearchHistoryDao = new SearchHistoryDao();
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_history, null);

            viewHolder = new ViewHolder();
            viewHolder.mSearchHistoryTv = convertView.findViewById(R.id.tv_search_history);
            viewHolder.mClearIv = convertView.findViewById(R.id.iv_clear);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SearchHistory searchHistory = mSearchHistoryList.get(position);
        viewHolder.mSearchHistoryTv.setText(searchHistory.getKeyword());
        viewHolder.mClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSearchHistoryDao.deleteSearchHistoryByKeyword(searchHistory.getKeyword());
                mSearchHistoryList.remove(position);

                mClickListener.onClick(mSearchHistoryList.size());

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView mSearchHistoryTv;
        ImageView mClearIv;
    }

    public interface ClickListener {
        void onClick(Object... objects);
    }
}
