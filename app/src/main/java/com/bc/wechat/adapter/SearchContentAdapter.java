package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.FileItem;

import java.util.List;

/**
 * 搜索内容
 *
 * @author zhou
 */
public class SearchContentAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileItem> mSearchContentList;

    public SearchContentAdapter(Context context, List<FileItem> searchContentList) {
        this.mContext = context;
        this.mSearchContentList = searchContentList;
    }

    public void setData(List<FileItem> dataList) {
        this.mSearchContentList = dataList;
    }

    public List<FileItem> getData() {
        return this.mSearchContentList;
    }

    public void addData(List<FileItem> dataList) {
        this.mSearchContentList.addAll(dataList);
    }

    @Override
    public int getCount() {
        return mSearchContentList.size();
    }

    @Override
    public FileItem getItem(int position) {
        return mSearchContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_content, null);
        return convertView;
    }
}
