/**
 * @Title: SpectrumView.java
 * @Package: com.an.view
 * @Description: 自定义频谱图控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.09 20:27
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
 * 自定义频谱图控件
 */
public class SpectrumView extends View {

    // 外部定义项
    private int _unitFontSize;             // 单位字体大小
    private String _unitStr;               // 单位
    private int _unitColor;                // 单位字体颜色
    private int _gridColor;                // 背景网格颜色
    private int _gridCount;                // 网格几等分，默认10
    private int _realTimeLineColor;        // 实时值的颜色
    private int _maxValueLineColor;        // 最大值的颜色
    private int _minValueLineColor;        // 最小值的颜色
    private int _margin_top;               // 上边距
    private int _margin_bottom;            // 下边距
    private int _margin_left;              // 左边距
    private int _margin_right;             // 右边距
    private int _scaleFontSize;            // 刻度字体大小

    // 内部定义项，画图用
    private int _width;                    // 测量的宽度
    private int _height;                   // 测量的高度
    private Paint _paint;                  // 画笔
    private int _scaleLength;              // 小刻度线长度
    private int _maxValue;                 // 纵轴刻度显示的最大值
    private int _minValue;                 // 纵轴刻度显示的最小值

    // 频谱数据
    private double _frequency;             // 中心频率
    private double _spectrumSpan;          // 频谱带宽
    private float[] _data;                 // 频谱数据

    private boolean _drawMaxValue;         // 绘制最大值
    private boolean _drawMinValue;         // 绘制最小值


    public SpectrumView(Context context, AttributeSet attrs, int defStypeAttr) {
        super(context, attrs, defStypeAttr);
        initView(context, attrs);
    }

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SpectrumView(Context context) {
        super(context);
        initView();
    }

    /**
     * 设置数据
     *
     * @param frequency    中心频率
     * @param spectrunSpan 频谱带宽
     * @param data         频谱数据
     */
    public void setData(double frequency, double spectrunSpan, float[] data) {
        _frequency = frequency;
        _spectrumSpan = spectrunSpan;
        _data = data;
        postInvalidate();
    }

    /**
     * 设置是否绘制最大值
     *
     * @param visible
     */
    public void setMaxValueLineVisible(boolean visible) {
        this._drawMaxValue = visible;
        postInvalidate();
    }

    /**
     * 设置是否绘制最小值
     *
     * @param visible
     */
    public void setMinValueLineVisible(boolean visible) {
        this._drawMinValue = visible;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth() - 1;
        _height = getMeasuredHeight() - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawUnit(canvas);
        drawAxis(canvas);
        drawSpectrum(canvas);

        super.onDraw(canvas);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpectrumView);
        if (typedArray != null) {
            _unitFontSize = typedArray.getInt(R.styleable.SpectrumView_unit_font_size, 20);
            _unitStr = typedArray.getString(R.styleable.SpectrumView_unit_sv);
            _unitColor = typedArray.getColor(R.styleable.SpectrumView_unit_color_sv, Color.argb(200, 0, 255, 0));
            _gridColor = typedArray.getColor(R.styleable.SpectrumView_grid_color_sv, Color.argb(200, 255, 251, 240));
            _gridCount = typedArray.getInt(R.styleable.SpectrumView_grid_count_sv, 10);
            _realTimeLineColor = typedArray.getColor(R.styleable.SpectrumView_realtime_line_color_sv, Color.GREEN);
            _maxValueLineColor = typedArray.getColor(R.styleable.SpectrumView_max_value_line_color_sv, Color.RED);
            _minValueLineColor = typedArray.getColor(R.styleable.SpectrumView_min_value_line_color_sv, Color.BLUE);
            _margin_top = typedArray.getInt(R.styleable.SpectrumView_margin_top_sv, 10);
            _margin_bottom = typedArray.getInt(R.styleable.SpectrumView_margin_bottom_sv, 50);
            _margin_left = typedArray.getInt(R.styleable.SpectrumView_margin_left_sv, 50);
            _margin_right = typedArray.getInt(R.styleable.SpectrumView_margin_right_sv, 10);
            _scaleFontSize = typedArray.getInt(R.styleable.SpectrumView_scale_font_size_sv, 20);

            _paint = new Paint();
            _maxValue = 80;
            _minValue = -20;
            _scaleLength = 10;
            _frequency = 101.7;
            _spectrumSpan = 20;
        } else {
            initView();
        }
    }

    private void initView() {
        _unitFontSize = 20;
        _unitStr = "";
        _unitColor = Color.argb(200, 0, 255, 0);
        _gridColor = Color.argb(200, 255, 251, 240);
        _gridCount = 10;
        _realTimeLineColor = Color.GREEN;
        _maxValueLineColor = Color.RED;
        _minValueLineColor = Color.BLUE;
        _margin_top = 10;
        _margin_bottom = 50;
        _margin_left = 10;
        _margin_right = 10;
        _scaleFontSize = 20;

        _paint = new Paint();
        _maxValue = 80;
        _minValue = -20;
        _scaleLength = 10;
        _frequency = 101.7;
        _spectrumSpan = 20;
    }

    /**
     * 画单位
     *
     * @param canvas
     */
    private void drawUnit(Canvas canvas) {
        if (_unitStr == null || _unitStr.length() == 0)
            return;  // 如果没有设置单位，则不需要绘制

        canvas.rotate(-90);
        canvas.translate(-_height, 0);

        _paint.setColor(_unitColor);
        _paint.setTextSize(_unitFontSize);
        Rect unitRect = new Rect();
        _paint.getTextBounds(_unitStr, 0, _unitStr.length(), unitRect);

        canvas.drawText(_unitStr, _height / 2 - (int) (unitRect.width() / 2), unitRect.height(), _paint);

        canvas.save();
        canvas.translate(_height, 0);
        canvas.rotate(90);
    }

    /**
     * 画背景刻度
     *
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        _paint.setColor(_gridColor);
        _paint.setTextSize(_scaleFontSize);
        int scaleHeight = _height - _margin_top - _margin_bottom;     // 绘制区总高度
        int scaleWidth = _width - _margin_left - _margin_right;       // 绘制区总宽度

        float perScaleHeight = scaleHeight / (float) _gridCount;       // 每一格的高度
        float perScaleWidth = scaleWidth / (float) _gridCount;         // 每一格的宽度
        int perScaleValue = (_maxValue - _minValue) / _gridCount;      // 每一格的刻度值    需要注意是否需要加绝对值符号

        Rect scaleRect = new Rect();
        _paint.getTextBounds(_maxValue + "", 0, (_maxValue + "").length(), scaleRect);

        for (int i = 0; i <= _gridCount; i++) {
            // 从上往下画，先画刻度值，然后再画横轴很纵轴
            int height = (int) (i * perScaleHeight) + _margin_top;
            int width = _margin_left + _scaleLength + (int) (i * perScaleWidth);
            int startWidth = _margin_left;                         // 起始位置 X
            int textHeight = 0;

            if ((i + 1) % 2 != 0) {
                if (i == 0) {
                    textHeight += scaleRect.height();

                    // 画横轴的起始文本
                } else if (i == _gridCount) {
                    // 画横轴的终止文本

                } else {
                    textHeight += scaleRect.height() / 2;
                }

                // 画纵轴刻度值
                int scaleValue = _maxValue - i * perScaleValue;
                float scaleTextLen = _paint.measureText(scaleValue + "");
                canvas.drawText(scaleValue + "", startWidth - scaleTextLen, height + textHeight, _paint);
            } else {
                startWidth += _scaleLength;

                if (i == _gridCount / 2) {
                    canvas.drawText(_frequency + "", _width - _margin_right - scaleWidth / 2 - scaleRect.width() / 2, _height - _margin_bottom + scaleRect.height(), _paint);
                }
            }

            // 画横轴
            canvas.drawLine(startWidth, height, _width - _margin_right, height, _paint);
            // 画纵轴
            canvas.drawLine(width, _margin_top, width, _height - _margin_bottom, _paint);
        }
    }

    /**
     * 画频谱
     *
     * @param canvas
     */
    private void drawSpectrum(Canvas canvas) {

    }
}
