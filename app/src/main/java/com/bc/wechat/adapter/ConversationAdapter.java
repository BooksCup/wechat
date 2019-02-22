package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.bc.wechat.entity.Conversation;

import java.util.List;

public class ConversationAdapter extends BaseAdapter {


    private List<Conversation> normalList;
    private List<Conversation> topList;
    private Context mContext;
    private LayoutInflater inflater;


    public ConversationAdapter(Context context, List<Conversation> normalList,
                               List<Conversation> topList) {
        this.mContext = context;
        this.normalList = normalList;
        this.topList = topList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return normalList.size() + topList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < topList.size()) {
            return topList.get(position);
        } else {
            return normalList.get(position - topList.size());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int size = 0;
        convertView = creatConvertView(position);
        return convertView;
    }

    private View creatConvertView(int size) {
        View convertView;

        if (size == 0) {
            convertView = inflater.inflate(R.layout.item_conversation_single,
                    null, false);
        } else if (size == 1) {
            convertView = inflater.inflate(R.layout.item_conversation_group1,
                    null, false);

        } else if (size == 2) {
            convertView = inflater.inflate(R.layout.item_conversation_group2,
                    null, false);

        } else if (size == 3) {
            convertView = inflater.inflate(R.layout.item_conversation_group3,
                    null, false);

        } else if (size == 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group4,
                    null, false);

        } else if (size > 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group5,
                    null, false);

        } else {
            convertView = inflater.inflate(R.layout.item_conversation_group5,
                    null, false);

        }

        return convertView;
    }
}
