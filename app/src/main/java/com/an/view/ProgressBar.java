/**
 * @Title: ProgressBar.java
 * @Package: com.an.view
 * @Description: 自定义进度条控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date: 2019.08.08 14:05
 * @Version: V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

/**
 * 自定义进度条控件
 */
public class ProgressBar extends View {

    private int _borderColor;         // 边框颜色
    private int _borderSize;          // 边框粗细
    private int _rectColor;           // 矩形颜色
    private int _textColor;           // 文本颜色
    private int _textSize;
    private int _orientation;         // 水平、垂直绘制

    private Paint _paint;             // 画笔
    private int _width;
    private int _height;
    private int _value = 50;
    private int _maxValue = 100;
    private int _minValue = 0;

    public ProgressBar(Context context, AttributeSet attrs, int defStypeAttr) {
        super(context, attrs, defStypeAttr);
        initView(context, attrs);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ProgressBar(Context context) {
        super(context);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth() - 1;
        _height = getMeasuredHeight() - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBorder(canvas);
        drawRect(canvas);
        drawText(canvas);

        super.onDraw(canvas);
    }

    /**
     * 设置进度值
     *
     * @param value
     */
    public void setValue(int value) {
        _value = value;
        postInvalidate();
    }

    /**
     * 获取进度值
     *
     * @return
     */
    public int getValue() {
        return _value;
    }

    /**
     * 初始化控件
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar);
        if (typedArray != null) {
            _borderColor = typedArray.getColor(R.styleable.ProgressBar_border_color_pb, Color.GREEN);
            _rectColor = typedArray.getColor(R.styleable.ProgressBar_rect_color_pb, Color.argb(100, 0, 0, 255));
            _borderSize = typedArray.getInt(R.styleable.ProgressBar_border_size_pb, 2);
            _textColor = typedArray.getColor(R.styleable.ProgressBar_text_color_pb, Color.RED);
            _textSize = typedArray.getInt(R.styleable.ProgressBar_text_size_pb, 40);
            _orientation = typedArray.getInt(R.styleable.ProgressBar_orientation_pb, 0);
        }

        _paint = new Paint();
    }

    private void initView() {
        _borderColor = Color.GREEN;
        _borderSize = 2;
        _rectColor = Color.argb(100, 0, 0, 255);
        _textColor = Color.RED;
        _textSize = 40;
        _orientation = 0;
        _paint = new Paint();
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawBorder(Canvas canvas) {
        _paint.setColor(_borderColor);
        _paint.setStrokeWidth(_borderSize);

        canvas.drawLine(0, 0, _width, 0, _paint);       // 绘制上边
        canvas.drawLine(0, 0, 0, _height, _paint);      // 绘制左边
        canvas.drawLine(_width, 0, _width, _height, _paint);            // 绘制右边
        canvas.drawLine(0, _height, _width, _height, _paint);           // 绘制下边
    }

    /**
     * 绘制矩形
     *
     * @param canvas
     */
    private void drawRect(Canvas canvas) {
        _paint.setColor(_rectColor);

        if (_orientation == 0) {
            float perSize = _width / ((float) (_maxValue - _minValue));
            int w = (int) (perSize * _value);
            canvas.drawRect(0, 0, w, _height, _paint);
        } else {
            float perSize = _height / ((float) (_maxValue - _minValue));
            int h = (int) (perSize * _value);
            canvas.drawRect(0, _height - h, _width, _height, _paint);
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        _paint.setColor(_textColor);
        _paint.setTextSize(_textSize);
        String text = _value + "%";
        float measureLength = _paint.measureText(text);

        if (_orientation == 0) {
            int w = _width / 2 - (int) (measureLength / 2);
            canvas.drawText(text, w, _height / 3 * 2, _paint);
        } else {
            canvas.drawText(text, _width / 2 - (int) (measureLength / 2), _height / 2, _paint);
        }
    }
}
