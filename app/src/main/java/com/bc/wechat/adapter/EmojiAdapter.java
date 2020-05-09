package com.bc.wechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bc.wechat.R;

import java.util.List;

public class EmojiAdapter extends ArrayAdapter<String> {
    public EmojiAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_emoji, null);
        }

        ImageView imageView = convertView.findViewById(R.id.iv_emoji);

        String filename = getItem(position);
        int resId = getContext().getResources().getIdentifier(filename, "drawable", getContext().getPackageName());
        imageView.setImageResource(resId);

        return convertView;
    }
}
