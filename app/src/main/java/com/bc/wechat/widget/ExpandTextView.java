package com.bc.wechat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;

/**
 * 朋友圈显示与收起全文的自定义TextView
 *
 * @author zhou
 */
public class ExpandTextView extends LinearLayout {

    // 最大的行数(默认)
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView mContentTv;
    private TextView mStateTv;

    private int mShowLines;

    private ExpandStatusListener expandStatusListener;
    private boolean isExpand;

    public ExpandTextView(Context context) {
        super(context);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_expand_text, this);
        mContentTv = findViewById(R.id.tv_content);
        if (mShowLines > 0) {
            mContentTv.setMaxLines(mShowLines);
        }

        mStateTv = findViewById(R.id.tv_state);
        mStateTv.setOnClickListener(view -> {
            String textStr = mStateTv.getText().toString().trim();
            if ("全文".equals(textStr)) {
                mContentTv.setMaxLines(Integer.MAX_VALUE);
                mStateTv.setText("收起");
                setExpand(true);
            } else {
                mContentTv.setMaxLines(mShowLines);
                mStateTv.setText("全文");
                setExpand(false);
            }
            //通知外部状态已变更
            if (expandStatusListener != null) {
                expandStatusListener.statusChange(isExpand());
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0);
        try {
            mShowLines = typedArray.getInt(R.styleable.ExpandTextView_showLines, DEFAULT_MAX_LINES);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(final CharSequence content) {
        mContentTv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                mContentTv.getViewTreeObserver().removeOnPreDrawListener(this);
                int linCount = mContentTv.getLineCount();
                if (linCount > mShowLines) {

                    if (isExpand) {
                        mContentTv.setMaxLines(Integer.MAX_VALUE);
                        mStateTv.setText("收起");
                    } else {
                        mContentTv.setMaxLines(mShowLines);
                        mStateTv.setText("全文");
                    }
                    mStateTv.setVisibility(View.VISIBLE);
                } else {
                    mStateTv.setVisibility(View.GONE);
                }
                return true;
            }
        });
        mContentTv.setText(content);
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener) {
        this.expandStatusListener = listener;
    }

    public interface ExpandStatusListener {
        void statusChange(boolean isExpand);
    }

}