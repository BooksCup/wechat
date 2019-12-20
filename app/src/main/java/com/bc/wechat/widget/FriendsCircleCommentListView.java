package com.bc.wechat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FriendsCircleCommentListView extends ListView {
    public FriendsCircleCommentListView(Context context) {
        super(context);
    }

    public FriendsCircleCommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FriendsCircleCommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FriendsCircleCommentListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
