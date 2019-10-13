package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.R;
import com.bc.wechat.activity.ShowFriendsCircleBigImageActivity;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.utils.TimestampUtil;
import com.bc.wechat.widget.FriendsCirclePhotoGridView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class FriendsCircleAdapter extends BaseAdapter {
    private List<FriendsCircle> friendsCircleList;
    private Context mContext;

    public FriendsCircleAdapter(List<FriendsCircle> friendsCircleList, Context context) {
        this.friendsCircleList = friendsCircleList;
        this.mContext = context;
    }

    public void setData(List<FriendsCircle> dataList) {
        this.friendsCircleList = dataList;
    }

    public void addData(List<FriendsCircle> dataList) {
        this.friendsCircleList.addAll(dataList);
    }

    @Override
    public int getCount() {
        return friendsCircleList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsCircleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendsCircle friendsCircle = friendsCircleList.get(position);
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_friends_circle, null);
            viewHolder = new ViewHolder();
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            viewHolder.mContentTv = convertView.findViewById(R.id.tv_content);
            viewHolder.mMoreTv = convertView.findViewById(R.id.tv_more);
            viewHolder.mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);
            viewHolder.mPhotosGv = convertView.findViewById(R.id.gv_friends_circle_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAvatarSdv.setImageURI(Uri.parse(friendsCircle.getUserAvatar()));
        viewHolder.mNickNameTv.setText(friendsCircle.getUserNickName());
        viewHolder.mContentTv.setText(friendsCircle.getCircleContent());
        viewHolder.mCreateTimeTv.setText(TimestampUtil.getTimePoint(friendsCircle.getTimestamp()));

        List<String> photoList;
        try {
            photoList = JSONArray.parseArray(friendsCircle.getCirclePhotos(), String.class);
        } catch (Exception e) {
            photoList = new ArrayList<>();
        }

        viewHolder.mPhotosGv.setAdapter(new FriendsCirclePhotoAdapter(photoList, mContext));

        viewHolder.mPhotosGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mContext.startActivity(new Intent(mContext, ShowFriendsCircleBigImageActivity.class));
            }
        });

        viewHolder.mMoreTv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewHolder.mContentTv.getLineCount() > 4) {
                    viewHolder.mMoreTv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mMoreTv.setVisibility(View.GONE);
                }
                return true;
            }
        });

        viewHolder.mMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.mContentTv.getMaxLines() <= 4) {
                    viewHolder.mMoreTv.setText("收起");
                    viewHolder.mContentTv.setMaxLines(viewHolder.mContentTv.getLineCount());
                } else {
                    viewHolder.mMoreTv.setText("全文");
                    viewHolder.mContentTv.setMaxLines(4);
                }

            }
        });

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mAvatarSdv;
        TextView mNickNameTv;
        TextView mContentTv;
        TextView mMoreTv;
        TextView mCreateTimeTv;
        FriendsCirclePhotoGridView mPhotosGv;
    }
}
