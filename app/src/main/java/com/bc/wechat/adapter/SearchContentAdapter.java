package com.bc.wechat.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_content, null);

            viewHolder = new ViewHolder();
            viewHolder.mTitleTv = convertView.findViewById(R.id.tv_title);
            viewHolder.mContentTv = convertView.findViewById(R.id.tv_content);
            viewHolder.mAuthorTv = convertView.findViewById(R.id.tv_author);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FileItem fileItem = mSearchContentList.get(position);
        viewHolder.mTitleTv.setText(Html.fromHtml(fileItem.getFileName()));
        viewHolder.mContentTv.setText(Html.fromHtml(fileItem.getFilePath()));
        viewHolder.mAuthorTv.setText(fileItem.getDiskName() + "(" + fileItem.getFileSize() + ")");

        return convertView;
    }

    class ViewHolder {
        TextView mTitleTv;
        TextView mContentTv;
        TextView mAuthorTv;
    }
}
