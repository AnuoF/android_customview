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
import android.util.AttributeSet;
import android.view.View;


/**
 * 自定义雷达控件
 */
public class RadarView extends View {


    private Bitmap _backBmp;             // 背景
    private Canvas _backCanvas;

    private Paint _paint;

    private int _width;
    private int _height;


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
        int py = _height / 2;

        _paint.setColor(Color.GREEN);



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

    }

    private void drawScan(Canvas canvas) {

    }
}
