package com.bc.wechat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bc.wechat.R;


/**
 * Created by lifei on 2015/12/29.
 */
public class RadarView extends FrameLayout {
    private Paint mPaintLine;//画圆
    private Paint mPaintGra;//渐变圆
    private int width, height;//屏幕宽高
    //    Matrix是一个3 x 3的矩阵，他对图片的处理分为四个基本类型：
//    1、Translate————平移变换
//    2、Scale————缩放变换
//    3、Rotate————旋转变换
//    4、Skew————错切变换
    private Matrix matrix;//矩阵
    private int start = 1;//角度
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            start += 1;
            matrix = new Matrix();
            matrix.postRotate(start, width / 2, height / 2);
            RadarView.this.invalidate();
            handler.postDelayed(runnable, 60);
        }
    };

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        setBackgroundResource(R.mipmap.ax);
        matrix = new Matrix();

        handler.post(runnable);
    }

    private void initPaint() {
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#A1A1A1"));
        mPaintLine.setStrokeWidth(3);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintGra = new Paint();
        mPaintGra.setColor(0X9D00FF00);
        mPaintLine.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(width / 2, height / 2, width / 6, mPaintLine);
        canvas.drawCircle(width / 2, height / 2, 2 * width / 6, mPaintLine);
        canvas.drawCircle(width / 2, height / 2, 11 * width / 20, mPaintLine);
        canvas.drawCircle(width / 2, height / 2, 7 * height / 16, mPaintLine);

        //Shader BitmapShader主要用来渲染图像，LinearGradient 用来进行梯度渲染，RadialGradient 用来进行环形渲染，SweepGradient 用来进行梯度渲染，ComposeShader则是一个 混合渲染
        //扫描渐变---围绕一个中心点扫描渐变就像电影里那种雷达扫描，用来梯度渲染。
        Shader shader = new SweepGradient(width / 2, height / 2, Color.TRANSPARENT, Color.parseColor("#AAAAAAAA"));
        mPaintGra.setShader(shader);
        //对canvas应用矩阵变换
        canvas.concat(matrix);
        canvas.drawCircle(width / 2, height / 2, 7 * height / 16, mPaintGra);
        super.onDraw(canvas);
    }

}
