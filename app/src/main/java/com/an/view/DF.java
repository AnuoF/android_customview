package com.an.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.CollapsibleActionView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.an.customview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DF extends View {
    private int _neswColor;                       // NESW颜色
    private int _neswSize;                        // NESW字体大小
    private ViewMode _viewMode;                   // 视图模式
    private NorthMode _nothMode;                  // 正北示向度
    private int _crossLineColor;                  // 十字交叉线条颜色
    private int _gridCount;                       // 几等分
    private int _minScaleLineLength;              // 短刻度线长度
    private int _maxScaleLineLength;               // 长刻度线的长度
    private int _dataSize;                         // 数据长度 缓存多少个点
    private int _scaleLineColor;
    private int _scaleCircleColor;

    private boolean _initFinished;
    private int _width;
    private int _height;

    private Bitmap _bitmap;
    private Canvas _canvas;
    private Paint _mPaint;
    private Paint _paint;

    private List<float[]> _dataList = new ArrayList<>();

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
        _viewMode = ViewMode.CLOCK;
        _nothMode = NorthMode.NORTH;
        _crossLineColor = Color.WHITE;
        _minScaleLineLength = 10;
        _maxScaleLineLength = 20;
        _dataSize = 10;
        _scaleLineColor = Color.GREEN;
        _scaleCircleColor = Color.BLUE;

        _initFinished = false;
        _paint = new Paint();
        _mPaint = new Paint();
        _executorService = Executors.newFixedThreadPool(1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
        _initFinished = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (_lockObj) {
            if (_bitmap != null) {
                canvas.drawBitmap(_bitmap, 0, 0, _paint);
            } else {
                drawScale(canvas);
                drawOutsideCircle(canvas);
                drawCar(canvas);
            }
        }

        super.onDraw(canvas);
    }

    /**
     * @param azimuth      示向度
     * @param quality      质量
     * @param compassAngle 罗盘方位
     */
    public void setData(final float azimuth, final float quality, final float compassAngle) {
        if (_initFinished == false)
            return;

        if (_dataList.size() >= _dataSize) {
            _dataList.remove(0);
        }
        _dataList.add(new float[]{azimuth, quality, compassAngle});

        _executorService.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                synchronized (_lockObj) {
                    draw(azimuth, quality, compassAngle);
                    postInvalidate();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void draw(float azimuth, float quality, float compassAngle) {
        if (_bitmap == null) {
            _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(_bitmap);
        } else {
            // 清屏
            Paint p = new Paint();
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            _canvas.drawPaint(p);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

        drawScale(_canvas);
        drawOutsideCircle(_canvas);
        drawAzimuthLine(_canvas);
        drawCar(_canvas);

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
        int minR = r / 5 * 3;

        _mPaint.setStyle(Paint.Style.STROKE);
        _mPaint.setTextSize(20);
        _mPaint.setColor(_scaleLineColor);

        canvas.translate(px, py);

        if (_viewMode == ViewMode.COMPASS) {
            // 罗盘视图
            for (int i = 0; i < 40; i++) {
                if (i % 5 == 0) {
                    _mPaint.setStrokeWidth(2);
                    canvas.drawLine(0, -minR, 0, -(minR - _maxScaleLineLength), _mPaint);
                    String text = i == 0 ? "360" : (i) * 9 + "";
                    Rect textRect = new Rect();
                    _mPaint.getTextBounds(text, 0, text.length(), textRect);
                    canvas.drawText(text, 0 - textRect.width() / 2, -minR + textRect.height() + _maxScaleLineLength, _mPaint);
                } else {
                    _mPaint.setStrokeWidth(1);
                    canvas.drawLine(0, -minR, 0, -(minR - _minScaleLineLength), _mPaint);
                }
                canvas.save();
                canvas.restore();
                canvas.rotate(9);
            }
        } else {
            // 钟表视图
            for (int i = 0; i < 60; i++) {
                if (i % 5 == 0) {
                    _mPaint.setStrokeWidth(2);
                    canvas.drawLine(0, -minR, 0, -(minR - _maxScaleLineLength), _mPaint);
                    String text = i == 0 ? "12" : (int) (i * 0.2) + "";
                    Rect textRect = new Rect();
                    _mPaint.getTextBounds(text, 0, text.length(), textRect);
                    canvas.drawText(text, 0 - textRect.width() / 2, -minR + textRect.height() + _maxScaleLineLength, _mPaint);
                } else {
                    _mPaint.setStrokeWidth(1);
                    canvas.drawLine(0, -minR, 0, -(minR - _minScaleLineLength), _mPaint);
                }

                canvas.save();
                canvas.restore();
                canvas.rotate(6);
            }
        }

        canvas.translate(-px, -py);

        _mPaint.setColor(_scaleCircleColor);
        _mPaint.setStrokeWidth(2);
        canvas.drawCircle(px, py, minR, _mPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawOutsideCircle(Canvas canvas) {
        float angle = 0;
        if (_nothMode == NorthMode.CAR_HEAD || _dataList.size() != 0) {
            angle = _dataList.get(_dataList.size() - 1)[2];
        }

        int px = _width / 2;
        int py = _height / 2;     // 中心点（原点）   画图均以中心点作为参考
        int r = Math.min(_width, _height) / 2;    // 半径，取较小的值

        int maxR = r / 5 * 4;

        canvas.translate(px, py);

        canvas.rotate(angle);

        _mPaint.setColor(Color.RED);
        canvas.drawArc(new RectF(-maxR, -maxR, maxR, maxR), -90, 180, false, _mPaint);
        _mPaint.setColor(Color.BLUE);
        canvas.drawArc(new RectF(-maxR, -maxR, maxR, maxR), 90, 180, false, _mPaint);

        _mPaint.setTextSize(_neswSize);
        _mPaint.setColor(_neswColor);
        _mPaint.setStyle(Paint.Style.STROKE);
        Rect neswRect = new Rect();
        _mPaint.getTextBounds("N", 0, "N".length(), neswRect);
        int size = Math.max(neswRect.height(), neswRect.width());

        _mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawText("N", 0 - neswRect.width() / 2, -r + size, _mPaint);
        canvas.drawText("E", r - size, 0 + neswRect.height() / 2, _mPaint);
        canvas.drawText("S", -neswRect.width() / 2, r, _mPaint);
        canvas.drawText("W", -r - size + neswRect.width(), 0 + neswRect.height() / 2, _mPaint);

        canvas.drawLine(0, 0 - r + size, 0, 0 + r - size, _mPaint);   // 纵轴
        canvas.drawLine(0 - r + size, 0, 0 + r - size, 0, _mPaint);   // 横轴

        canvas.save();
        canvas.restore();
        canvas.rotate(-angle);

        canvas.translate(-px, -py);
    }

    /**
     * 画示向线
     */
    private void drawAzimuthLine(Canvas canvas) {
        if (_dataList.size() <= 0)
            return;

        for (int i = 0; i < _dataList.size(); i++) {
            float quality = _dataList.get(i)[1];
            float azimuth = _dataList.get(i)[0];


        }

    }

    /**
     * 画车
     */
    private void drawCar(Canvas canvas) {
        int px = _width / 2;
        int py = _height / 2;     // 中心点（原点）   画图均以中心点作为参考
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        canvas.drawBitmap(bitmap, px - bitmap.getWidth() / 2, py - bitmap.getHeight() / 2, _paint);
        bitmap.recycle();
    }


    /**
     * 视图模式
     */
    private enum ViewMode {
        COMPASS,    // 方位视图
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
