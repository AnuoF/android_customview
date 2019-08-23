package com.an.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DF extends View {
    private int _neswColor;                       // NESW颜色
    private int _neswSize;                        // NESW字体大小
    private ViewMode _viewMode;                   // 视图模式
    private NorthMode _nothMode;                  // 正北示向度
    private int _crossLineColor;                  // 十字交叉线条颜色
    private int _gridCount;                       // 几等分

    private int _width;
    private int _height;

    private Bitmap _bitmap;
    private Canvas _canvas;
    private Paint _mPaint;
    private Paint _paint;

    private static final Object _lockObj = new Object();     // 互斥锁
    private ExecutorService _executorService;


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
        _neswSize = 40;
        _viewMode = ViewMode.COMPASS;
        _nothMode = NorthMode.NORTH;
        _crossLineColor = Color.WHITE;

        _paint = new Paint();
        _mPaint = new Paint();
        _executorService = Executors.newFixedThreadPool(1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (_lockObj) {
            if (_bitmap != null) {
                canvas.drawBitmap(_bitmap, 0, 0, _paint);
            } else {
                drawScale(canvas);
            }
        }

        super.onDraw(canvas);
    }

    /**
     * 设置数据
     *
     * @param azimuth      示向度
     * @param compassAngle 罗盘方位
     */
    public void setData(final float azimuth, final float compassAngle) {
        _executorService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (_lockObj) {
                    draw(azimuth, compassAngle);
                    postInvalidate();
                }
            }
        });
    }

    private void draw(float azimuth, float compassAngle) {
        if (_bitmap == null) {
            _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(_bitmap);
        }

        drawScale(_canvas);
        drawAzimuthLine(azimuth);
        drawCar(compassAngle);

        postInvalidate();   // 在同一类中使用不需要回调
    }

    /**
     * 画刻度
     *
     * @param canvas 画布，可能是系统画布或者离屏画布
     */
    private void drawScale(Canvas canvas) {
        int px = _width / 2;
        int py = _height / 2;     // 中心点（原点）   画图均以中心点作为参考
        int r = Math.min(_width, _height) / 2;    // 直径，取较小的值

        _mPaint.setTextSize(_neswSize);
        _mPaint.setColor(_neswColor);
        _mPaint.setStyle(Paint.Style.STROKE);
        Rect neswRect = new Rect();
        _mPaint.getTextBounds("N", 0, "N".length(), neswRect);
        int size = Math.max(neswRect.height(), neswRect.width());

        canvas.drawText("N", px - neswRect.width() / 2, py - r + size, _mPaint);
        canvas.drawText("E", px + r - size, py + neswRect.height() / 2, _mPaint);
        canvas.drawText("S", px - neswRect.width() / 2, py + r - size + size / 2, _mPaint);
        canvas.drawText("W", px - r + size - size / 2, py + neswRect.height() / 2, _mPaint);

        canvas.drawLine(px, py - r + size, px, py + r - size, _mPaint);    // 纵轴
        canvas.drawLine(px - r + size, py, px + r - size, py, _mPaint);   // 横轴

        int maxR = r / 5 * 4;
        int minR = r / 5 * 3;
        canvas.drawCircle(px, py, maxR, _mPaint);
        canvas.drawCircle(px, py, minR, _mPaint);

        for (int i = 0; i < 36; i++) {

        }


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
