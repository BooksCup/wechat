package com.bc.wechat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircleComment;
import com.bc.wechat.utils.TextParser;

import java.util.List;

public class FriendsCircleCommentAdapter extends BaseAdapter {
    private List<FriendsCircleComment> mFriendsCircleCommentList;
    private Context mContext;

    public FriendsCircleCommentAdapter(
            List<FriendsCircleComment> friendsCircleCommentList, Context context) {
        this.mFriendsCircleCommentList = friendsCircleCommentList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mFriendsCircleCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriendsCircleCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final FriendsCircleComment friendsCircleComment = mFriendsCircleCommentList.get(position);
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_friends_circle_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.mCommentTv = convertView.findViewById(R.id.tv_comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextParser textParser = new TextParser();
        textParser.append(friendsCircleComment.getCommentUserNickName(), 20, Color.rgb(87, 107, 149), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, friendsCircleComment.getCommentUserNickName(), Toast.LENGTH_SHORT).show();
            }
        });

        textParser.append(":" + friendsCircleComment.getCommentContent(), 20, Color.BLACK, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, friendsCircleComment.getCommentContent(), Toast.LENGTH_SHORT).show();
            }
        });
        textParser.parse(viewHolder.mCommentTv);

        return convertView;
    }

    class ViewHolder {
        private TextView mCommentTv;
    }
}
