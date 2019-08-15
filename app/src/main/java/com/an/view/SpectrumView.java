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
    private int _marginTop;                // 上边距
    private int _marginBottom;             // 下边距
    private int _marginLeft;               // 左边距
    private int _marginRight;              // 右边距
    private int _scaleFontSize;            // 刻度字体大小
    private int _selectRectColor;          // 选择区域的颜色

    // 内部定义项，画图用
    private int _width;                    // 测量的宽度
    private int _height;                   // 测量的高度

    private Paint _paint;                  // 画笔
    private int _scaleLineLength;          // 小刻度线长度
    private int _maxValue;                 // 纵轴刻度显示的最大值
    private int _minValue;                 // 纵轴刻度显示的最小值

    // 频谱数据
    private double _frequency;             // 中心频率
    private double _spectrumSpan;          // 频谱带宽
    private float[] _data;                 // 频谱数据

    private int _startIndex;               // 提取数据的起始位置
    private int _endIndex;                 // 提取数据的终止位置

    private boolean _drawMaxValue;         // 绘制最大值（暂未实现）
    private boolean _drawMinValue;         // 绘制最小值（暂未实现）

    private HandleType _handleType;        // 手势处理类型
    private float _startY;                 // 单点触控时的起始点Y，纵轴拖动时用
    private float _startX;                 // 单独触控时的起始点X，频谱缩放时用
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
     * @param data         频谱数据（点数与频谱带宽和分辨率有关）
     */
    public void setData(double frequency, double spectrunSpan, float[] data) {
        if (frequency != _frequency || spectrunSpan != _spectrumSpan) {
            _startIndex = 0;
            _endIndex = data.length;
        }

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
        _spectrumSpan = 0;
        postInvalidate();
    }

    /**
     * 视图自动显示
     */
    public void autoView() {
        if (_data == null || _data.length == 0)
            return;  // 没有数据时不响应

        // 获取最大值和最小值，来确定 _maxValue 和 _minValue
        MinMaxValue minMax = getMinMax(_data);
        float average = (minMax.getMax() + minMax.getMin()) / 2;
        int num = 0;

        for (int i = 1; i < 1000; i++) {
            float max = average + 5 * i;
            float min = average - 5 * i;
            if (max > minMax.getMax() && min < minMax.getMin()) {
                if (Math.abs((max - minMax.getMax()) / (float) Math.abs(max - min)) >= 0.25) {
                    num = i;    // 最大值与顶点的距离 >= 1/4
                    break;
                }
            }
        }

        if (num != 0) {
            _maxValue = (int) (average + 5 * num);
            _minValue = (int) (average - 5 * num);
            postInvalidate();
        }
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
            _selectRectColor = typedArray.getColor(R.styleable.SpectrumView_select_rect_color_sv, Color.argb(100, 255, 0, 0));

            _paint = new Paint();
            _maxValue = 80;
            _minValue = -20;
            _scaleLineLength = 10;
            _frequency = 101.7;
            _spectrumSpan = 20;
            setOnTouchListener(this);
            _handleType = HandleType.NONE;
            _data = new float[0];
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
        _selectRectColor = Color.argb(100, 255, 0, 0);

        _paint = new Paint();
        _maxValue = 80;
        _minValue = -20;
        _scaleLineLength = 10;
        _frequency = 101.7;
        _spectrumSpan = 20;
        setOnTouchListener(this);
        _handleType = HandleType.NONE;
        _data = new float[0];
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
        _paint.setStyle(Paint.Style.FILL);
        _paint.setStrokeWidth(1);
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
        int perScaleValue = (maxValue - minValue) / _gridCount;        // 每一格的刻度值    需要注意是否需要加绝对值符号

        Rect scaleRect = new Rect();
        _paint.getTextBounds(maxValue + "", 0, (maxValue + "").length(), scaleRect);

        for (int i = 0; i <= _gridCount; i++) {
            // 从上往下画，先画刻度值，然后再画横轴和纵轴
            int height = (int) (i * perScaleHeight) + _marginTop;
            int width = _marginLeft + _scaleLineLength + (int) (i * perScaleWidth);
            int startWidth = _marginLeft;         // 起始位置 X
            int textHeight = 0;

            if ((i + 1) % 2 != 0) {
                if (i == 0) {
                    textHeight += scaleRect.height();
                } else if (i == _gridCount) {

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
        if (_data.length == 0 || _startIndex >= _endIndex)
            return;  // 没有数据时不需要绘制

        _paint.setColor(_realTimeLineColor);
        _paint.setStyle(Paint.Style.STROKE);

        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;

        int scaleHeight = _height - _marginTop - _marginBottom;     // 绘制区总高度
        int scaleWidth = _width - _marginLeft - _marginRight - _scaleLineLength;       // 绘制区总宽度
        float perHeight = scaleHeight / (float) Math.abs(maxValue - minValue);      // 每一格的高度
        float perWidth = scaleWidth / (float) (_endIndex - _startIndex);

        Path realTimePath = new Path();

        for (int i = _startIndex; i <= _endIndex; i++) {    // 此处需要加上=，确保最后一个点可以绘制
            if (i >= _data.length)  // 防止越界
                continue;

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

        // 覆盖上边和下边，使频谱看上去是在指定区域进行绘制的
        _paint.setStyle(Paint.Style.FILL);
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            _paint.setColor(color);
            canvas.drawRect(_marginLeft + _scaleLineLength, 0, _width - _marginRight, _marginTop, _paint);
            canvas.drawRect(_marginLeft + _scaleLineLength, _height - _marginBottom + 1, _width - _marginRight, _height + 1, _paint);
        }

        // 计算并绘制中心频率和带宽
        double perFreq = _spectrumSpan / _data.length / 1000;
        double span = perFreq * (_endIndex - _startIndex) / 2 * 1000;

        String centerFreqStr, startFreqStr, endFreqStr;
        // 如果是全景，则显示中心频率和带宽，局部缩放则显示起始、终止频率和中心点频率
        if (_startIndex == 0 && _endIndex == _data.length) {
            centerFreqStr = String.format("%.3f", _frequency) + "MHz";
            startFreqStr = "-" + String.format("%.3f", span) + "kHz";
            endFreqStr = "+" + String.format("%.3f", span) + "kHz";
        } else {
            int centerIndex = (_startIndex + (_endIndex - _startIndex) / 2);
            centerFreqStr = String.format("%.3f", centerIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
            startFreqStr = String.format("%.3f", _startIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
            endFreqStr = String.format("%.3f", _endIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
        }

        Rect freqRect = new Rect();
        _paint.setColor(_gridColor);
        _paint.getTextBounds(centerFreqStr, 0, centerFreqStr.length(), freqRect);
        canvas.drawText(centerFreqStr, _width - _marginRight - scaleWidth / 2 - freqRect.width() / 2, _height - _marginBottom + freqRect.height() + 5, _paint);
        canvas.drawText(startFreqStr, _marginLeft + _scaleLineLength, _height - _marginBottom + freqRect.height() + 5, _paint);
        canvas.drawText(endFreqStr, _width - _marginRight - (float) _paint.measureText(endFreqStr), _height - _marginBottom + freqRect.height() + 5, _paint);
    }

    private void drawSelectRect(Canvas canvas) {
        if (_handleType == HandleType.ZONE) {
            if (_endX == _startX) {
                // 如果相等，则绘制当前点的频率和垂直线条
                _paint.setColor(Color.RED);
                _paint.setStyle(Paint.Style.STROKE);
                _paint.setStrokeWidth(2);

                canvas.drawLine(_startX, _marginTop + 1, _startX, _height - _marginBottom, _paint);

                float perScaleLength = (_width - _marginLeft - _scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft - _scaleLineLength) / perScaleLength);
                double perFreq = _spectrumSpan / _data.length / 1000;
                String currentFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + "MHz";     // 当前点的频率
                _paint.setStyle(Paint.Style.FILL);
                Rect rect = new Rect();
                _paint.getTextBounds(currentFreqStr, 0, currentFreqStr.length(), rect);
                canvas.drawText(currentFreqStr, _startX, _marginTop + rect.height() + 2, _paint);
            } else if (_endX < _startX) {
                // 缩小
                _paint.setColor(_selectRectColor);
                _paint.setStrokeWidth(4);
                canvas.drawLine(_startX, _marginTop, _endX, _height - _marginBottom, _paint);
                canvas.drawLine(_endX, _marginTop, _startX, _height - _marginBottom, _paint);
                canvas.drawRect(_endX, _marginTop + 1, _startX, _height - _marginBottom, _paint);
            } else {
                // 放大，绘制矩形和计算起始、终止频率以及实时带宽
                _paint.setColor(_selectRectColor);
                canvas.drawRect(_startX, _marginTop + 1, _endX, _height - _marginBottom, _paint);

                float perScaleLength = (_width - _marginLeft - _scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft - _scaleLineLength) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft - _scaleLineLength) / perScaleLength);
                double perFreq = _spectrumSpan / _data.length / 1000;
                String startFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String endFreqStr = String.format("%.3f", tempEndIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String spanStr = String.format("%.3f", perFreq * (tempEndIndex - tempStartIndex) * 1000) + " kHz";

                Rect rect = new Rect();
                _paint.getTextBounds(spanStr, 0, spanStr.length(), rect);
                _paint.setColor(_realTimeLineColor);
                canvas.drawText(startFreqStr, _startX, _marginTop + rect.height() + 5, _paint);
                canvas.drawText(endFreqStr, _startX, _marginTop + rect.height() * 2 + 8, _paint);
                canvas.drawText(spanStr, _startX, _marginTop + rect.height() * 3 + 11, _paint);
            }
        }
    }

    // 以下部分实现平移、放大、缩小等功能

    private void actionDown(MotionEvent event) {
        if (Utils.IsPointInRect(0, 0, _marginLeft + _scaleLineLength, _height, (int) event.getX(), (int) event.getY())) {
            _handleType = HandleType.DRAG;    // 纵轴拖动
            _startY = event.getY();
        } else if (Utils.IsPointInRect(_marginLeft + _scaleLineLength, _marginTop, _width - _marginRight, _height - _marginBottom, (int) event.getX(), (int) event.getY())) {
            _handleType = HandleType.ZONE;    // 缩放频谱
            _startX = _endX = event.getX();
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
            int spanScale = (int) ((currrentY - _startY) / ((_height - _marginTop - _marginBottom) / Math.abs((_maxValue - _minValue))));
            if (spanScale != 0) {
                _offsetY = spanScale;
                postInvalidate();
            }
        } else if (_handleType == HandleType.ZOOM && event.getPointerCount() == 2) {
            float currentDistanceY = Math.abs(event.getY(0) - event.getY(1));
            float perScaleHeight = (_height - _marginTop - _marginBottom) / (float) Math.abs(_maxValue - _minValue);
            int spanScale = (int) ((currentDistanceY - _oldDistanceY) / perScaleHeight);
            if (spanScale != 0 && ((_maxValue - spanScale) - (_minValue + spanScale) >= _gridCount)) {  // 防止交叉越界，并且在放大到 总刻度长为 _gridCount 时，不能再放大
                _zoomOffsetY = spanScale;
                postInvalidate();
            }
        } else if (_handleType == HandleType.ZONE) {
            _endX = event.getX();
            if (_endX < _marginLeft + _scaleLineLength) {
                _endX = _marginLeft + _scaleLineLength;
            } else if (_endX > _width - _marginRight) {
                _endX = _width - _marginRight;
            }  // 此处的判断是为了防止Rect越界

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
            // 这里需要读取索引
            if (_startX > _endX) {
                // 缩小
                _startIndex = 0;
                _endIndex = _data.length;
                postInvalidate();
            } else if (_startX < _endX) {
                // 放大。 根据 _startX 和 _endX 来确定 _startIndex 和 _endIndex，以及中心频率和带宽
                if (_data.length == 0 || _endIndex - _startIndex <= 2) {  // 没有数据，或者只要小于2个点时，不再放大
                    _handleType = HandleType.NONE;
                    return;
                }

                float perScaleLength = (_width - _marginLeft - _scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                // 在放大的基础上再次放大，巧妙啊，佩服我自己了，哈哈哈
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft - _scaleLineLength) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft - _scaleLineLength) / perScaleLength);
                if (tempEndIndex > tempStartIndex) {   // 保证至少有2个点（一条直线）
                    _endIndex = tempEndIndex;
                    _startIndex = tempStartIndex;
                    postInvalidate();
                }
            }
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

    /**
     * 获取数组最大值和最小值
     *
     * @param arr
     * @return
     */
    private MinMaxValue getMinMax(float[] arr) {
        float max = Integer.MIN_VALUE;
        float min = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
            if (arr[i] < min) {
                min = arr[i];
            }
        }

        MinMaxValue minMax = new MinMaxValue();
        minMax.setMax(max);
        minMax.setMin(min);

        return minMax;
    }

    private class MinMaxValue {
        private float _max;
        private float _min;

        // 构造函数
        public MinMaxValue() {
        }

        public void setMax(float max) {
            _max = max;
        }

        public float getMax() {
            return _max;
        }

        public void setMin(float min) {
            _min = min;
        }

        public float getMin() {
            return _min;
        }
    }
}
