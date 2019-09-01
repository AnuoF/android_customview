/**
 * @Title: CircleProcessBar.java
 * @Package: com.an.view
 * @Description: 自定义圆形进度条控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.27 22:36
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

/**
 * 自定义圆形进度条控件
 */
public class CircleProcessBar extends View {

    private int _fillColor;              // 内圆填充色
    private int _circleColor;            // 圆的颜色
    private int _scaleColor;             // 圆上刻度县的颜色
    private int _fontSize;               // 字体大小
    private int _margin;                 // 边距
    private int _fontColor;

    private int _currentValue;           // 当前值

    private int _width;
    private int _height;
    private int _count;                  // 几等分
    private int _circleCount;

    private Paint _paint;


    public CircleProcessBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public CircleProcessBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProcessBar);
        if (typedArray != null) {
            _fillColor = typedArray.getColor(R.styleable.CircleProcessBar_fillColor_CPB, 0);
            _circleColor = typedArray.getColor(R.styleable.CircleProcessBar_circleColor_CPB, Color.BLUE);
            _scaleColor = typedArray.getColor(R.styleable.CircleProcessBar_scaleColor_CPB, Color.GREEN);
            _fontSize = typedArray.getInt(R.styleable.CircleProcessBar_fontSize_CPB, 30);
            _margin = typedArray.getInt(R.styleable.CircleProcessBar_margin_CPB, 10);
            _fontColor = typedArray.getColor(R.styleable.CircleProcessBar_fontColor_CPB, Color.GREEN);

            _paint = new Paint();
            _paint.setTextSize(_fontSize);
            _count = 5;
            _circleCount = 3;

            _currentValue = 50;

        } else {
            initView();
        }
    }

    private void initView() {
        _fillColor = 0;
        _circleColor = Color.BLUE;
        _scaleColor = Color.GREEN;
        _fontSize = 30;
        _margin = 10;
        _fontColor = Color.GREEN;

        _paint = new Paint();
        _paint.setTextSize(_fontSize);
        _count = 5;

    }

    public void setValue(int value) {
        if (value < 0) {
            _currentValue = 0;
        } else if (value > 100) {
            _currentValue = 100;
        } else {
            _currentValue = value;
        }

        postInvalidate();
    }

    public int getValue() {
        return _currentValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawScale(canvas);
        drawText(canvas);

        super.onDraw(canvas);
    }

    /**
     * 画圆和填充圆
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        int px = _width / 2;
        int py = _height / 2;   // 圆心
        int r = (Math.min(px, py) - _margin) * 3 / _count;

        _paint.setColor(_circleColor);
        _paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(px, py, r, _paint);


        if (_fillColor != 0) {
            _paint.setColor(_fillColor);
            _paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(px, py, r, _paint);
        }
    }

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        _paint.setStyle(Paint.Style.STROKE);


        if (_currentValue <= 0) return;
        int px = _width / 2;
        int py = _height / 2;   // 圆心
        int r = (Math.min(px, py) - _margin) * 3 / _count;

        canvas.translate(px, py);

        for (int i = 1; i <= _currentValue; i++) {
            canvas.rotate((float) 3.6);
            int len = 0;
            if (i % 10 == 0) {
                _paint.setColor(_circleColor);
                len = (Math.min(px, py) - _margin);
            } else {
                _paint.setColor(_scaleColor);
                len = (Math.min(px, py) - _margin) * 4 / _count;
            }

            canvas.drawLine(0, -r, 0, -len, _paint);
        }

        canvas.rotate(-(_currentValue * (float) 3.6));
        canvas.translate(-px, -py);
    }

    private void drawText(Canvas canvas) {
        _paint.setColor(_fontColor);
        _paint.setTextSize(_fontSize);

        int px = _width / 2;
        int py = _height / 2;   // 圆心

        String text = _currentValue + " %";
        Rect textRect = new Rect();
        _paint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, px - textRect.width() / 2, py - textRect.height() / 2, _paint);
    }
}
