package com.bc.wechat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.MomentsActivity;
import com.bc.wechat.entity.MyMedia;
import com.bc.wechat.entity.RecyclerViewItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MomentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "FriendsCircleAdapter";
    Activity activity;
    private List<RecyclerViewItem> recyclerViewItemList;

    public MomentsAdapter(ArrayList<RecyclerViewItem> list, MomentsActivity momentsActivity) {
        super();
        this.activity = momentsActivity;
        this.recyclerViewItemList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_moments, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewItem recyclerViewItem = recyclerViewItemList.get(position);
        TextView nickname = holder.itemView.findViewById(R.id.tv_nick_name);
        TextView content = holder.itemView.findViewById(R.id.tv_content);
        TextView time = holder.itemView.findViewById(R.id.tv_create_time);
        ImageView avatar = holder.itemView.findViewById(R.id.sdv_avatar);
        LinearLayout pinglun = holder.itemView.findViewById(R.id.pinglun);

//        NineGridViewGroup photos = holder.itemView.findViewById(R.id.gv_friends_circle_photo);
//        ArrayList<MyMedia> mediaList = recyclerViewItem.getMediaList();
//        if (mediaList != null && mediaList.size() > 0) {
//            ArrayList<NineGridItem> nineGridItemList = new ArrayList<>();
//            for (MyMedia myMedia : mediaList) {
//                String thumbnailUrl = myMedia.getImageUrl();
//                String bigImageUrl = myMedia.getImageUrl();
//                String videoUrl = myMedia.getVideoUrl();
//                nineGridItemList.add(new NineGridItem(thumbnailUrl, bigImageUrl, videoUrl));
//            }
//            NineGridViewAdapter nineGridViewAdapter = new NineGridViewAdapter(nineGridItemList);
//            photos.setAdapter(nineGridViewAdapter);
//        }
        nickname.setText(recyclerViewItem.getNickName());
        Glide.with(avatar).load(recyclerViewItem.getHeadImageUrl()).into(avatar);
        content.setText(recyclerViewItem.getContent());
        time.setText(recyclerViewItem.getCreateTime());
    }

    @Override
    public int getItemCount() {
        return recyclerViewItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private static class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView nickname;

        public RecyclerHolder(View view) {
            super(view);
            nickname = view.findViewById(R.id.tv_nick_name);
        }
    }
}
