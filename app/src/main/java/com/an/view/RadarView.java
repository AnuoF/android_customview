/**
 * @Title: RadarView.java
 * @Package: com.an.view
 * @Description: 自定义雷达控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.09.01 11:30
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

/**
 * 自定义雷达控件
 */
public class RadarView extends View {

    private int _margin;                 // 边距

    private Bitmap _backBmp;             // 背景
    private Canvas _backCanvas;

    private int _width;
    private int _height;

    private Paint _paint;
    private Paint _gradientPaint;


    public RadarView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initView();
    }

    public RadarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    private void initView() {
        _paint = new Paint();
        _gradientPaint = new Paint();
        _gradientPaint.setStyle(Paint.Style.FILL);
        _margin = 30;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();

        if (_backBmp != null) {
            _backBmp.recycle();
            _backBmp = null;
        }

        _backBmp = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
        _backCanvas = new Canvas(_backBmp);
        initBackground();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawPoint(canvas);
        drawScan(canvas);
        super.onDraw(canvas);
    }

    /**
     * 初始化背景
     */
    private void initBackground() {
        int px = _width / 2;
        int py = _height / 2;    // 圆心
        int r = Math.min(px, py) - _margin;   // 半径

        _paint.setColor(Color.GREEN);
        _paint.setStyle(Paint.Style.STROKE);

        // 画放射线，画刻度文本
        for (int i = 0; i < 15; i++) {
            _backCanvas.drawLine(px, py, px, py - r, _paint);
            String text = i * 15 + "";
            Rect textRect = new Rect();
            _paint.getTextBounds(text, 0, text.length(), textRect);
            _backCanvas.drawText(text, px - textRect.width() / 2, py - r - textRect.height(), _paint);
            _backCanvas.rotate(24, px, py);   //  3.75 = 360 / 96
        }

        // 画圆
        for (int i = 1; i <= 5; i++) {   // 5个圆
            int minR = (int) (i * (r / (float) 5));
            _backCanvas.drawCircle(px, py, minR, _paint);
        }
    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        if (_backBmp != null) {
            canvas.drawBitmap(_backBmp, 0, 0, _paint);
        }
    }

    private void drawPoint(Canvas canvas) {
        int px = _width / 2;
        int py = _height / 2;    // 圆心
        int r = Math.min(px, py) - _margin;   // 半径

        RadialGradient radialGradient;
        radialGradient = new RadialGradient(px, py - r / 2, r / 20, new int[]{Color.GREEN, Color.argb(10, 0, 255, 0)}, null, Shader.TileMode.CLAMP);
        _gradientPaint.setShader(radialGradient);
        canvas.drawCircle(px, py - r / 2, r / 20, _gradientPaint);
        radialGradient = new RadialGradient(px - r / 2, py, r / 20, new int[]{Color.GREEN, Color.argb(10, 0, 255, 0)}, null, Shader.TileMode.CLAMP);
        _gradientPaint.setShader(radialGradient);
        canvas.drawCircle(px - r / 2, py, r / 20, _gradientPaint);
        radialGradient = new RadialGradient(px - r / 3 * 2, py + r / 2, r / 20, new int[]{Color.GREEN, Color.argb(10, 0, 255, 0)}, null, Shader.TileMode.CLAMP);
        _gradientPaint.setShader(radialGradient);
        canvas.drawCircle(px - r / 3 * 2, py + r / 2, r / 20, _gradientPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawScan(Canvas canvas) {
        int px = _width / 2;
        int py = _height / 2;    // 圆心
        int r = Math.min(px, py) - _margin;   // 半径

        int[] colors = new int[]{Color.GREEN, Color.argb(200, 0, 255, 0), Color.argb(150, 0, 255, 0), Color.argb(100, 0, 255, 0), Color.argb(50, 0, 255, 0), Color.argb(10, 0, 255, 0)};
        SweepGradient sweepGradient = new SweepGradient(px, py, colors, null);
        _gradientPaint.setShader(sweepGradient);
        canvas.drawArc(px - r, py - r, px + r, py + r, 0, 45, true, _gradientPaint);

    }
}
