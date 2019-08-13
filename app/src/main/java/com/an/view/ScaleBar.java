/**
 * @Title: ScaleBar.java
 * @Package: com.an.view
 * @Description: 自定义刻度条控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date: 2019.08.01 20:00
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义刻度条控件：可用于显示电平、质量等值
 */
public class ScaleBar extends View {

    private Paint _paint;       // 画笔
    private int _maxValue;      // 刻度显示的最大值
    private int _minValue;      // 刻度显示的最小值
    private int _scaleCount;    // 刻度几等分
    private String _title;      // 刻度条显示的标题
    private int _titleHeight;   // 标题预留的空间
    private int _orientation;   // 水平，垂直绘制
    private int _barColor;      // 刻度条颜色
    private int _scaleColor;    // 刻度线条颜色

    private int _scaleLineLength;  // 刻度线的长，这个自适应
    private int _value;         // 刻度值
    private int _width;         // View 宽度值
    private int _height;        // View 高度值

    public ScaleBar(Context context, AttributeSet attrs, int defStypeAttr) {
        super(context, attrs, defStypeAttr);
        init(context, attrs);
    }

    public ScaleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleBar(Context context) {
        super(context);
        init();
    }

    /**
     * 获取刻度值
     *
     * @param value
     */
    public void setValue(int value) {
        if (value < _minValue) {
            _value = _minValue;
        } else if (value > _maxValue) {
            _value = _maxValue;
        } else {
            _value = value;
        }
        postInvalidate();
    }

    /**
     * 设置刻度值
     *
     * @return
     */
    public int getValue() {
        return _value;
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleBar);
        if (typedArray != null) {
            _scaleColor = typedArray.getColor(R.styleable.ScaleBar_scale_color_sb, Color.GREEN);
            _paint = new Paint();

            _maxValue = typedArray.getInt(R.styleable.ScaleBar_max_value_sb, 100);
            _minValue = typedArray.getInt(R.styleable.ScaleBar_min_value_sb, 0);
            _scaleCount = typedArray.getInt(R.styleable.ScaleBar_scale_count_sb, 10);
            _title = typedArray.getString(R.styleable.ScaleBar_title_sb);
            _titleHeight = typedArray.getInt(R.styleable.ScaleBar_title_height_sb, 80);
            _orientation = typedArray.getInt(R.styleable.ScaleBar_orientation_sb, 0);
            _barColor = typedArray.getColor(R.styleable.ScaleBar_bar_color_sb, Color.GREEN);
            _value = _minValue;
        } else {
            init();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        _paint = new Paint();
        _paint.setColor(Color.GREEN);
        _maxValue = 100;
        _minValue = 0;
        _scaleCount = 10;
        _title = "";
        _orientation = 0;
        _value = _minValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth() - 1;      // 宽度值
        _height = getMeasuredHeight() - 1;    // 高度值

        if (_orientation == 0) {
            _scaleLineLength = _height / 4;
        } else {
            _scaleLineLength = _width / 4;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        _paint.setColor(_scaleColor);
        drawBorker(canvas);
        boolean valid = Utils.checkValid();
        if (valid) {
            if (_orientation == 0) {
                _paint.setTextSize(_height / 3);
            } else {
                _paint.setTextSize(_width / 3);   // 设置文字大小
            }
            drawTitle(canvas);
            drawScale(canvas);
            canvas.save();
            drawValueBar(canvas);
        } else {
            drawExpired(canvas);
        }

        super.onDraw(canvas);
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawBorker(Canvas canvas) {
        canvas.drawLine(0, 0, _width, 0, _paint);    // 画顶边
        canvas.drawLine(0, 0, 0, _height, _paint);   // 画左边
        canvas.drawLine(_width, 0, _width, _height, _paint);         // 画右边
        canvas.drawLine(0, _height, _width, _height, _paint);        // 画底边
    }

    /**
     * 绘制无效提示
     *
     * @param canvas
     */
    private void drawExpired(Canvas canvas) {
        // Control expired, please contact the author
        _paint.setColor(Color.RED);
        if (_orientation == 0) {
            // 水平布局
            canvas.drawText("Control expired, please contact the author", 0, _height / 2, _paint);
        } else {
            // 垂直布局
            canvas.drawText("Control", _width / 2, 50, _paint);
            canvas.drawText("expired", _width / 2, 90, _paint);
            canvas.drawText("please", _width / 2, 130, _paint);
            canvas.drawText("contact", _width / 2, 170, _paint);
            canvas.drawText("the", _width / 2, 210, _paint);
            canvas.drawText("author", _width / 2, 250, _paint);
        }
    }

    /**
     * 画标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        if (_orientation == 0) {
            if (_title == null || _title.length() <= 0) {
                _titleHeight = 30;
            } else {
                canvas.drawText(_title, _width - _titleHeight / 2 - _paint.measureText(_title) / 2, _height / 2, _paint);
            }
        } else {
            if (_title == null || _title.length() <= 0) {
                _titleHeight = 30;
            } else {
                canvas.drawText(_title, _width / 2 - _paint.measureText(_title) / 2, _titleHeight / 2, _paint); // 居中
            }
        }
    }

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        if (_orientation == 0) {
            float oneScaleWidth = ((float) (_width - _titleHeight)) / _scaleCount;
            for (int i = 0; i <= _scaleCount; i++) {
                float width = i * oneScaleWidth;
                canvas.drawLine(width, _height, width, _height - _scaleLineLength, _paint);
                canvas.drawText((_minValue + ((_maxValue - _minValue) / _scaleCount) * i) + "", width, _height - _scaleLineLength, _paint);   //
            }
        } else {
            float oneScaleHeight = ((float) (_height - _titleHeight)) / _scaleCount;
            for (int i = 0; i <= _scaleCount; i++) {
                float height = i * oneScaleHeight + _titleHeight;
                canvas.drawLine(0, height, _scaleLineLength, height, _paint);
                //canvas.drawText((_maxValue - _minValue) / _scaleCount * (_scaleCount - i) + "", _scaleLineLength, height, _paint);
                canvas.drawText(_maxValue - (_maxValue - _minValue) / _scaleCount * i + "", _scaleLineLength, height, _paint);
            }
        }
    }

    /**
     * 画刻度值条
     *
     * @param canvas
     */
    private void drawValueBar(Canvas canvas) {
        _paint.setColor(_barColor);

        if (_orientation == 0) {
            canvas.drawRect(0, 0, (_value - _minValue) / ((_maxValue - _minValue) / (float) _scaleCount) * ((_width - _titleHeight) / (float) _scaleCount), _height, _paint);
        } else {
            canvas.drawRect(0, _height - ((_value - _minValue) / ((_maxValue - _minValue) / (float) _scaleCount)) * ((_height - _titleHeight) / (float) _scaleCount), _width, _height, _paint);
        }
    }

}
