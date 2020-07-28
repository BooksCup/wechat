package cn.edu.heuet.littlecurl.ninegridview.detail;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView的Item的装饰类
 * 给每个item装一个左右padding边距
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space = -1;
    private float scale = 0.0F;
    private Context context;

    public SpaceItemDecoration(Context context, int space) {
        this.context = context;
        this.space = space;
    }

    public SpaceItemDecoration(Context context, float scale) {
        this.context = context;
        this.scale = scale;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) != 0) {
            if (space == -1) {
                // 设备宽
                int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
                outRect.left = (int) (deviceWidth * scale);
                outRect.right = outRect.left;
            } else {
                outRect.left = space;
                outRect.right = outRect.left;
            }
        }
    }
}