package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DF extends View {
    private int _neswColor;                       // NESW颜色
    private int _neswSize;                        // NESW字体大小
    private ViewMode _viewMode;                   // 视图模式
    private NorthMode _nothMode;                  // 正北示向度
    private int _crossLineColor;                  // 十字交叉线条颜色

    private int _width;
    private int _height;

    private Bitmap _bitmap;
    private Canvas _canvas;
    private Paint _paint;


    public DF(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public DF(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        _neswColor = Color.WHITE;
        _neswSize = 30;
        _viewMode = ViewMode.COMPASS;
        _nothMode = NorthMode.NORTH;
        _crossLineColor = Color.WHITE;


        _paint = new Paint();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (_bitmap != null) {
            canvas.drawBitmap(_bitmap, 0, 0, _paint);
        }

        super.onDraw(canvas);
    }

    /**
     * 设置数据
     *
     * @param azimuth      示向度
     * @param compassAngle 罗盘方位
     */
    public void setData(float azimuth, float compassAngle) {
        draw(azimuth, compassAngle);
    }

    private void draw(float azimuth, float compassAngle) {
        if (_bitmap == null) {
            _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(_bitmap);
        }

        drawScale();
        drawAzimuthLine(azimuth);
        drawCar(compassAngle);

        postInvalidate();   // 在同一类中使用不需要回调
    }

    /**
     * 画刻度
     */
    private void drawScale() {

    }

    /**
     * 画示向线
     *
     * @param azimuth 示向度
     */
    private void drawAzimuthLine(float azimuth) {

    }

    /**
     * 画车
     *
     * @param compassAngle 罗盘角度
     */
    private void drawCar(float compassAngle) {

    }


    /**
     * 视图模式
     */
    private enum ViewMode {
        COMPASS,  // 方位视图
        CLOCK       // 钟表视图
    }

    /**
     * 正北模式
     */
    private enum NorthMode {
        NORTH,      // 正北示向度
        CAR_HEAD    // 相对车头
    }
}
