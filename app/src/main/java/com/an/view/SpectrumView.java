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
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.an.customview.R;

/**
 * 自定义频谱图控件
 */
public class SpectrumView extends View implements View.OnTouchListener {

    // 外部定义项
    private int _unitFontSize;             // 单位字体大小
    private String _unitStr;               // 单位
    private int _unitColor;                // 单位字体颜色
    private int _gridColor;                // 背景网格颜色
    private int _gridCount;                // 网格几等分，默认10
    private int _realTimeLineColor;        // 实时值的颜色
    private int _maxValueLineColor;        // 最大值的颜色
    private int _minValueLineColor;        // 最小值的颜色
    private int _marginTop;               // 上边距
    private int _marginBottom;            // 下边距
    private int _marginLeft;              // 左边距
    private int _marginRight;             // 右边距
    private int _scaleFontSize;            // 刻度字体大小

    // 内部定义项，画图用
    private int _width;                    // 测量的宽度
    private int _height;                   // 测量的高度

    private Paint _paint;                  // 画笔
    private int _scaleLineLength;              // 小刻度线长度
    private int _maxValue;                 // 纵轴刻度显示的最大值
    private int _minValue;                 // 纵轴刻度显示的最小值

    // 频谱数据
    private double _frequency;             // 中心频率
    private double _spectrumSpan;          // 频谱带宽
    private float[] _data;                 // 频谱数据

    private int _startIndex;               // 提取数据的起始位置
    private int _endIndex;                 // 提取数据的终止位置

    private boolean _drawMaxValue;         // 绘制最大值
    private boolean _drawMinValue;         // 绘制最小值

    private HandleType _handleType;        // 手势处理类型
    private float _firstY;                 // 单点触控时的起始点Y，纵轴拖动时用
    private float _firstX;                 // 单独触控时的起始点X，频谱缩放时用
    private float _endX;                   // 最后点
    private int _offsetY;                  // 单点拖动时的Y轴偏移
    private float _oldDistanceY;           // 多点触控时，最初的距离
    private int _zoomOffsetY;              // 控制Y轴缩放


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
     * @param frequency    中心频率 MHz
     * @param spectrunSpan 频谱带宽 kHz
     * @param data         频谱数据
     */
    public void setData(double frequency, double spectrunSpan, float[] data) {
        _frequency = frequency;
        _spectrumSpan = spectrunSpan;
        _data = data;
        postInvalidate();
    }

    /**
     * 纵轴平移：上移或下移
     *
     * @param offset
     */
    public void offsetY(int offset) {
        _maxValue += offset;
        _minValue += offset;
        postInvalidate();
    }

    /**
     * 纵轴放大、缩小
     *
     * @param zoom
     */
    public void zoomY(int zoom) {
        _maxValue -= zoom;
        _minValue += zoom;
        postInvalidate();
    }

    /**
     * 清空图形和数据
     */
    public void clear() {
        _data = new float[0];
        postInvalidate();
    }

    /**
     * 视图自动显示
     */
    public void autoView() {

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

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean valid = Utils.checkValid();
        if (valid) {
            drawUnit(canvas);
            drawAxis(canvas);
            drawSpectrum(canvas);
            drawSelectRect(canvas);

            super.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionPointerDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionPointerUp(motionEvent);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                actionCancelUp(motionEvent);
                break;
        }

        return true;
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
            _marginTop = typedArray.getInt(R.styleable.SpectrumView_margin_top_sv, 10);
            _marginBottom = typedArray.getInt(R.styleable.SpectrumView_margin_bottom_sv, 30);
            _marginLeft = typedArray.getInt(R.styleable.SpectrumView_margin_left_sv, 50);
            _marginRight = typedArray.getInt(R.styleable.SpectrumView_margin_right_sv, 10);
            _scaleFontSize = typedArray.getInt(R.styleable.SpectrumView_scale_font_size_sv, 20);

            _paint = new Paint();
            _maxValue = 80;
            _minValue = -20;
            _scaleLineLength = 10;
            _frequency = 101.7;
            _spectrumSpan = 20;
            setOnTouchListener(this);
            _handleType = HandleType.NONE;
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
        _marginTop = 10;
        _marginBottom = 30;
        _marginLeft = 10;
        _marginRight = 10;
        _scaleFontSize = 20;

        _paint = new Paint();
        _maxValue = 80;
        _minValue = -20;
        _scaleLineLength = 10;
        _frequency = 101.7;
        _spectrumSpan = 20;
        setOnTouchListener(this);
        _handleType = HandleType.NONE;
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

        int scaleHeight = _height - _marginTop - _marginBottom;     // 绘制区总高度
        int scaleWidth = _width - _marginLeft - _marginRight - _scaleLineLength;       // 绘制区总宽度

        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;

        float perScaleHeight = scaleHeight / (float) _gridCount;       // 每一格的高度
        float perScaleWidth = scaleWidth / (float) _gridCount;         // 每一格的宽度
        int perScaleValue = (maxValue - minValue) / _gridCount;      // 每一格的刻度值    需要注意是否需要加绝对值符号

        Rect scaleRect = new Rect();
        _paint.getTextBounds(maxValue + "", 0, (maxValue + "").length(), scaleRect);

        for (int i = 0; i <= _gridCount; i++) {
            // 从上往下画，先画刻度值，然后再画横轴和纵轴
            int height = (int) (i * perScaleHeight) + _marginTop;
            int width = _marginLeft + _scaleLineLength + (int) (i * perScaleWidth);
            int startWidth = _marginLeft;                         // 起始位置 X
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
                int scaleValue = maxValue - i * perScaleValue;
                float scaleTextLen = _paint.measureText(scaleValue + "");
                canvas.drawText(scaleValue + "", startWidth - scaleTextLen, height + textHeight, _paint);
            } else {
                startWidth += _scaleLineLength;
            }

            // 画横轴
            canvas.drawLine(startWidth, height, _width - _marginRight, height, _paint);
            // 画纵轴
            canvas.drawLine(width, _marginTop, width, _height - _marginBottom, _paint);
        }
    }

    /**
     * 画频谱
     *
     * @param canvas
     */
    private void drawSpectrum(Canvas canvas) {
        if (_data.length == 0)
            return;  // 没有数据时不需要绘制

        _paint.setColor(_realTimeLineColor);
        _paint.setStyle(Paint.Style.STROKE);

        _startIndex = 0;
        _endIndex = _data.length;

        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;

        int scaleHeight = _height - _marginTop - _marginBottom;     // 绘制区总高度
        int scaleWidth = _width - _marginLeft - _marginRight - _scaleLineLength;       // 绘制区总宽度
        float perHeight = scaleHeight / (float) Math.abs(maxValue - minValue);      // 每一格的高度
        float perWidth = scaleWidth / (float) (_endIndex - _startIndex);

        Path realTimePath = new Path();

        for (int i = _startIndex; i < _endIndex; i++) {
            float level = _data[i];
            int x = (int) ((i - _startIndex) * perWidth) + _marginLeft + _scaleLineLength;
            int y = (int) ((maxValue - level) * perHeight) + _marginTop;

            if (i == _startIndex) {
                realTimePath.moveTo(x, y);
            } else {
                realTimePath.lineTo(x, y);
            }
        }

        canvas.drawPath(realTimePath, _paint);

        // 覆盖上边和下边
        _paint.setStyle(Paint.Style.FILL);
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            _paint.setColor(color);
            canvas.drawRect(_marginLeft + _scaleLineLength, 0, _width - _marginRight, _marginTop, _paint);
            canvas.drawRect(_marginLeft + _scaleLineLength, _height - _marginBottom + 1, _width - _marginRight, _height + 1, _paint);
        }

        // 绘制中心频率
        Rect freqRect = new Rect();
        String freqStr = _frequency + "MHz";
        String startSpanStr = "-" + _spectrumSpan / 2 + "kHz";
        String stopSpanStr = "+" + _spectrumSpan / 2 + "kHz";
        _paint.setColor(_gridColor);
        _paint.getTextBounds(freqStr, 0, freqStr.length(), freqRect);
        canvas.drawText(freqStr, _width - _marginRight - scaleWidth / 2 - freqRect.width() / 2, _height - _marginBottom + freqRect.height() + 5, _paint);
        canvas.drawText(startSpanStr, _marginLeft + _scaleLineLength, _height - _marginBottom + freqRect.height() + 5, _paint);
        canvas.drawText(stopSpanStr, _width - _marginRight - (float) _paint.measureText(stopSpanStr), _height - _marginBottom + freqRect.height() + 5, _paint);
    }

    private void drawSelectRect(Canvas canvas) {
        if (_handleType == HandleType.ZONE) {
            _paint.setColor(Color.argb(50, 0, 255, 0));

            if (_endX < _firstX) {
                canvas.drawLine(_firstX, _marginTop, _endX, _height - _marginBottom, _paint);
                canvas.drawLine(_endX, _marginTop, _firstX, _height - _marginBottom, _paint);
                canvas.drawRect(_endX, _marginTop, _firstX, _height - _marginBottom, _paint);
            } else {
                canvas.drawRect(_firstX, _marginTop, _endX, _height - _marginBottom, _paint);
            }
        }
    }

    // 以下部分实现平移、放大、缩小等功能

    private void actionDown(MotionEvent event) {
        if (Utils.IsPointInRect(0, 0, _marginLeft + _scaleLineLength, _height, (int) event.getX(), (int) event.getY())) {
            _handleType = HandleType.DRAG;    // 纵轴拖动
            _firstY = event.getY();
        } else if (Utils.IsPointInRect(_marginLeft + _scaleLineLength, _marginTop, _width - _marginRight, _height - _marginBottom, (int) event.getX(), (int) event.getY())) {
            _handleType = HandleType.ZONE;    // 缩放频谱
            _firstX = event.getX();
        }
    }

    private void actionPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            _handleType = HandleType.ZOOM;
            _oldDistanceY = Math.abs(event.getY(0) - event.getY(1));
        }
    }

    private void actionMove(MotionEvent event) {
        if (_handleType == HandleType.DRAG) {
            float currrentY = event.getY();
            int spanScale = (int) ((currrentY - _firstY) / ((_height - _marginTop - _marginBottom) / Math.abs((_maxValue - _minValue))));
            if (spanScale != 0) {
                _offsetY = spanScale;
                postInvalidate();
            }
        } else if (_handleType == HandleType.ZOOM && event.getPointerCount() == 2) {
            float currentDistanceY = Math.abs(event.getY(0) - event.getY(1));
            float perScaleHeight = (_height - _marginTop - _marginBottom) / (float) Math.abs(_maxValue - _minValue);
            int spanScale = (int) ((currentDistanceY - _oldDistanceY) / perScaleHeight);
            if (spanScale != 0 && ((_maxValue - spanScale) - (_minValue + spanScale) >= _gridCount)) {  // 防止交叉越界，并且在放大到 总刻度长为 _gridCount 时，不能缩小
                _zoomOffsetY = spanScale;
                postInvalidate();
            }
        } else if (_handleType == HandleType.ZONE) {
            _endX = event.getX();
            postInvalidate();
        }
    }

    private void actionPointerUp(MotionEvent event) {
        if (_handleType == HandleType.ZOOM) {
            _maxValue -= _zoomOffsetY;
            _minValue += _zoomOffsetY;
            _zoomOffsetY = 0;
        }

        _handleType = HandleType.NONE;
    }

    private void actionCancelUp(MotionEvent event) {
        if (_handleType == HandleType.DRAG) {
            _maxValue += _offsetY;
            _minValue += _offsetY;
            _offsetY = 0;
        } else if (_handleType == HandleType.ZONE) {
            // 这里需要设置索引
            _firstX = 0;
            _endX = 0;

        }

        _handleType = HandleType.NONE;
    }

    /**
     * 手势处理类型枚举
     */
    private enum HandleType {
        NONE,     // 无任何操作
        DRAG,     // 纵轴拖动平移
        ZOOM,     // 纵轴放大缩小
        ZONE      // 频谱放大缩小，显示区间
    }
}
