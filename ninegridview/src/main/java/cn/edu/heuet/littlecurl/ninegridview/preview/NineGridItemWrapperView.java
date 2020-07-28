package cn.edu.heuet.littlecurl.ninegridview.preview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;

public class NineGridItemWrapperView extends AppCompatImageView {

    private int moreNum = 0;              //显示更多的数量
    private int maskColor = 0x88000000;   //默认的遮盖颜色
    private float textSize = 35;          //显示文字的大小单位sp
    private int textColor = 0xFFFFFFFF;   //显示文字的颜色

    private TextPaint textPaint;              //文字的画笔
    private String msg = "";                  //要绘制的文字

    public NineGridItemWrapperView(Context context) {
        this(context, null);
    }

    public NineGridItemWrapperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridItemWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //转化单位
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getContext().getResources().getDisplayMetrics());
        textPaint = new TextPaint();
        textPaint.setTextAlign(Paint.Align.CENTER);  //文字居中对齐
        textPaint.setAntiAlias(true);                //抗锯齿
        textPaint.setTextSize(textSize);             //设置文字大小
        textPaint.setColor(textColor);               //设置文字颜色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (moreNum > 0) {
            canvas.drawColor(maskColor);
            float baseY = getHeight() / 2 - (textPaint.ascent() + textPaint.descent()) / 2;
            canvas.drawText(msg, getWidth() / 2, baseY, textPaint);
        }
    }

    /**
     * 九宫格触摸事件：
     * 按下有灰色遮罩
     * 松开去掉遮罩
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 按下之后九宫格的格子加一个灰色（gray）遮罩
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getDrawable();
                if (drawable != null) {
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                // 去掉灰色遮罩
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null) {
                    drawableUp.clearColorFilter();
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    // 回收资源
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
    }

    // 设置更多数量，重绘格子
    public void setMoreNum(int moreNum) {
        this.moreNum = moreNum;
        msg = "+" + moreNum;
        invalidate();
    }
}