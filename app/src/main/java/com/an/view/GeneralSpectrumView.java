/**
 * @Title: GeneralSpectrumView.java
 * @Package: com.an.view
 * @Description: 频谱图和瀑布图的组合控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.17 08:27
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.an.customview.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 频谱图和瀑布图的组合控件
 */
public class GeneralSpectrumView extends View implements View.OnTouchListener, OnDrawFinishedListener {

    // View定义字段
    private int _marginTop;                   // 上边距
    private int _marginBottom;                // 下边距
    private int _marginLeft;                  // 左边距
    private int _marginRight;                 // 右边距
    private int _gridColor;                   // 网格颜色
    private int _gridCount;                   // 网格数，几等分，默认10
    private String _unitText;                 // 频谱图Y轴单位
    private int _unitSize;                    // 单位字体大小
    private int _unitColor;                   // 单位字体颜色
    private int _maxValue;                    // Y轴显示的最大值
    private int _minValue;                    // Y轴显示的最小值
    private int _selectZoneColor;             // 框选区域的颜色
    private ShowMode _showMode;               // 显示模式 0-Both    1-Spectrum   2-Waterfall
    private int _realTimeLineColor;           // 实时值线条颜色
    private int _maxValueLineColor;           // 最大值线条颜色
    private int _minValueLineColor;           // 最小值线条颜色
    private int _scaleFontSize;               // 刻度字体大小
    private int _spectrumMarginBottom;        // 频谱图底部预留的间距
    private int _rainRow;                     // 瀑布图显示的行数

    private int _width;                       // 测量 宽
    private int _height;                      // 测量 高
    private boolean _initFinished;            // 是否初始化完成

    private Paint _paint;                     // onDraw画所有图形，以及选择区域绘制
    private Paint _drawPaint;                 // 主动绘制画笔
    private Paint _spectrumPaint;             // 频谱图画笔
    private int _scaleLineLength;             // 小刻度线长度
    private boolean _drawMaxLine;             // 绘制最大值
    private boolean _drawMinLine;             // 绘制最小值

    private Paint _gradientPaint;             // 色带画笔
    private Paint _waterfallPaint;            // 瀑布图画笔
    private int[] _gradientColors;            // 色带渐变色
    private boolean _bRool = true;            // 瀑布图是否滚动

    // 频谱数据
    private double _frequency;                // 中心频率
    private double _spectrumSpan;             // 频谱带宽
    private float[] _data;                    // 频谱数据
    private float[] _maxData;                 // 最大值
    private float[] _minData;                 // 最小值

    private int _startIndex;                  // 提取数据的起始位置（用于数据缩放）
    private int _endIndex;                    // 提取数据的终止位置（用于数据缩放）

    private HandleType _handelType;           // 手势处理类型
    private float _startY;                    // 单点触控时的起始点 Y，频谱图Y轴拖动时用
    private float _startX;                    // 单点触控时的起始点 X，频谱缩放时用
    private float _endX;                      // 最后点 X， X 轴放大缩小用
    private int _offsetY;                     // 单点拖动时的Y轴偏移
    private float _oldDistanceY;              // 多点触控时，最初的距离，用于频谱纵向缩放
    private int _zoomOffsetY;                 // 控制Y轴缩放

    private ExecutorService _executorService;       // 线程池服务，开启线程来进行图形绘制，避免占用主线程
    private Bitmap _waterfallBmp;                   // 瀑布图，不含渐变色带
    private Canvas _waterfallCanvas;                // 瀑布图画布
    private Bitmap _spectrumBmp;                    // 频谱图
    private Canvas _spectrumCanvas;                 // 频谱图画布

    private Bitmap _showBmp;                   // 显示到频谱的图像
    private Canvas _showCanvas;
    private static final Object _lockObj = new Object();


    public GeneralSpectrumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public GeneralSpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GeneralSpectrumView(Context context) {
        super(context);
        initView();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GeneralSpectrumView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.GeneralSpectrumView_marginTop, 5);
            _marginBottom = typedArray.getInt(R.styleable.GeneralSpectrumView_marginBottom, 5);
            _marginLeft = typedArray.getInt(R.styleable.GeneralSpectrumView_marginLeft, 60);
            _marginRight = typedArray.getInt(R.styleable.GeneralSpectrumView_marginRight, 5);
            _gridColor = typedArray.getColor(R.styleable.GeneralSpectrumView_gridCount, Color.argb(200, 255, 255, 255));
            _gridCount = typedArray.getInt(R.styleable.GeneralSpectrumView_gridCount, 10);
            _unitText = typedArray.getString(R.styleable.GeneralSpectrumView_unitText);
            _unitSize = typedArray.getInt(R.styleable.GeneralSpectrumView_unitSize, 20);
            _unitColor = typedArray.getColor(R.styleable.GeneralSpectrumView_unitColor, Color.WHITE);
            _maxValue = typedArray.getInt(R.styleable.GeneralSpectrumView_maxValue, 80);
            _minValue = typedArray.getInt(R.styleable.GeneralSpectrumView_minValue, -20);
            _selectZoneColor = typedArray.getColor(R.styleable.GeneralSpectrumView_selectZoneColor, Color.argb(50, 255, 0, 0));
            int mode = typedArray.getInt(R.styleable.GeneralSpectrumView_showMode, 0);
            _showMode = ShowMode.values()[mode];
            int gradientColorIndex = typedArray.getInt(R.styleable.GeneralSpectrumView_gradientColorIndex, 3);
            initColorGradient(gradientColorIndex);
            _realTimeLineColor = typedArray.getColor(R.styleable.GeneralSpectrumView_realTimeLineColor, Color.GREEN);
            _maxValueLineColor = typedArray.getColor(R.styleable.GeneralSpectrumView_maxValueLineColor, Color.RED);
            _minValueLineColor = typedArray.getColor(R.styleable.GeneralSpectrumView_minValueLineColor, Color.BLUE);
            _scaleFontSize = typedArray.getInt(R.styleable.GeneralSpectrumView_scaleFontSize, 20);
            _spectrumMarginBottom = typedArray.getInt(R.styleable.GeneralSpectrumView_spectrumMarginBottom, 30);
            _rainRow = typedArray.getInt(R.styleable.GeneralSpectrumView_rainRow, 200);

            _executorService = Executors.newFixedThreadPool(1);
            _scaleLineLength = 15;
            _spectrumPaint = new Paint();
            _waterfallPaint = new Paint();
            _gradientPaint = new Paint();
            _paint = new Paint();
            _drawPaint = new Paint();
            _initFinished = false;

            _data = new float[0];
            _maxData = new float[0];
            _minData = new float[0];
            _drawMaxLine = false;
            _drawMinLine = false;
            this.setOnTouchListener(this);
        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 5;
        _marginBottom = 5;
        _marginLeft = 60;
        _marginRight = 5;
        _gridColor = Color.argb(200, 255, 255, 255);
        _gridCount = 10;
        _unitText = "";
        _unitSize = 20;
        _unitColor = Color.WHITE;
        _maxValue = 80;
        _minValue = -20;
        _selectZoneColor = Color.argb(50, 255, 0, 0);
        _showMode = ShowMode.values()[0];
        initColorGradient(3);
        _realTimeLineColor = Color.GREEN;
        _maxValueLineColor = Color.RED;
        _minValueLineColor = Color.BLUE;
        _scaleFontSize = 20;
        _spectrumMarginBottom = 30;
        _rainRow = 200;

        _executorService = Executors.newFixedThreadPool(1);
        _scaleLineLength = 15;
        _spectrumPaint = new Paint();
        _waterfallPaint = new Paint();
        _paint = new Paint();
        _drawPaint = new Paint();
        _initFinished = false;

        _data = new float[0];
        _maxData = new float[0];
        _minData = new float[0];
        _drawMaxLine = false;
        _drawMinLine = false;
        this.setOnTouchListener(this);
    }

    public void setData(double frequency, double spectrumSpan, float[] data) {
        if (_initFinished == false) return;

        if (_frequency != frequency || spectrumSpan != _spectrumSpan) {
            _startIndex = 0;
            _endIndex = data.length;
            _frequency = frequency;
            _spectrumSpan = spectrumSpan;
        }

        _data = data;

        if (_maxData.length != data.length) {
            _maxData = new float[data.length];
            _minData = new float[data.length];
        }

        for (int i = 0; i < data.length; i++) {
            float level = data[i];
            if (level > _maxData[i]) {
                _maxData[i] = level;
            }
            if (level < _minData[i]) {
                _minData[i] = level;
            }
        }

        executeDraw(false);
    }

    /**
     * 设置了显示模式需要重绘图形
     *
     * @param mode
     */
    public void setShowMode(ShowMode mode) {
        if (mode == _showMode)
            return;     // 防止重复设置

        _showMode = mode;
        executeDraw(true);
    }

    /**
     * 设置最大值是否可见
     *
     * @param visible
     */
    public void setMaxValueVisible(boolean visible) {
        if (_drawMaxLine == visible)
            return;

        if (_showMode != ShowMode.Waterfall) {
            _drawMaxLine = visible;
            executeDraw(false);
        }
    }

    /**
     * 设置最小值是否可见
     *
     * @param visible
     */
    public void setMinValueVisible(boolean visible) {
        if (_drawMinLine == visible)
            return;

        if (_showMode != ShowMode.Waterfall) {
            _drawMinLine = visible;
            executeDraw(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
        _initFinished = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        _width = w;
        _height = h;
        executeDraw(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        synchronized (_lockObj) {
            if (_showBmp != null) {
                canvas.drawBitmap(_showBmp, 0, 0, _paint);
            }
        }

        drawSelectZone(canvas);
        super.onDraw(canvas);
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

    @Override
    public void onDrawFinished() {
        postInvalidate();   // 图形绘制完成之后通知重绘
    }

    private void executeDraw(final boolean newBmp) {
        final OnDrawFinishedListener listener = this;
        _executorService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (_lockObj) {

                    draw(newBmp, listener);

                    if (_showBmp == null) {
                        _showBmp = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
                        _showCanvas = new Canvas(_showBmp);
                    } else {
                        _showCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);    // 清空画布
                    }

                    switch (_showMode) {
                        case Both:
                            if (_spectrumBmp != null) {
                                _showCanvas.drawBitmap(_spectrumBmp, 0, 0, _paint);
                            }
                            if (_waterfallBmp != null) {
                                _showCanvas.drawBitmap(_waterfallBmp, 0, _height / 2, _paint);
                            }
                            break;
                        case Spectrum:
                            if (_spectrumBmp != null) {
                                _showCanvas.drawBitmap(_spectrumBmp, 0, 0, _paint);
                            }
                            break;
                        case Waterfall:
                            if (_waterfallBmp != null) {
                                _showCanvas.drawBitmap(_waterfallBmp, 0, 0, _paint);
                            }
                            break;
                    }
                }

                postInvalidate();
            }
        });
    }

    /**
     * 画背景。包括坐标轴、网格和渐变色带
     */
    private void draw(boolean newBmp, OnDrawFinishedListener callback) {
        if (newBmp) {
            if (_spectrumBmp != null) {
                _spectrumBmp.recycle();
                _spectrumBmp = null;
            }
            if (_waterfallBmp != null) {
                _waterfallBmp.recycle();
                _waterfallBmp = null;
            }
        }

        switch (_showMode) {
            case Both:
                if (_spectrumBmp == null) {
                    _spectrumBmp = Bitmap.createBitmap(_width, _height / 2, Bitmap.Config.ARGB_8888);
                    _spectrumCanvas = new Canvas(_spectrumBmp);
                }
                if (_waterfallBmp == null) {
                    _waterfallBmp = Bitmap.createBitmap(_width, _height / 2, Bitmap.Config.ARGB_8888);
                    _waterfallCanvas = new Canvas(_waterfallBmp);
                }

                drawSpectrumAxis();
                drawSpectrumLine();

                drawWaterfall();
                break;
            case Spectrum:
                if (_spectrumBmp == null) {
                    _spectrumBmp = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
                    _spectrumCanvas = new Canvas(_spectrumBmp);
                }
                drawSpectrumAxis();
                drawSpectrumLine();
                break;
            case Waterfall:
                if (_waterfallBmp == null) {
                    _waterfallBmp = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
                    _waterfallCanvas = new Canvas(_waterfallBmp);
                }
                drawWaterfall();
                break;
        }

        callback.onDrawFinished();
    }

    /**
     * 画频谱图的背景
     */
    private void drawSpectrumAxis() {
        _spectrumCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);    // 清空画布

        // 画单位
        int w = _spectrumBmp.getWidth();
        int h = _spectrumBmp.getHeight();

        if (_unitText != null && _unitText.length() > 0) {
            _spectrumCanvas.rotate(-90);
            _spectrumCanvas.translate(-h, 0);
            _spectrumPaint.setColor(_unitColor);
            _spectrumPaint.setTextSize(_unitSize);
            _spectrumPaint.setStyle(Paint.Style.FILL);
            Rect unitRect = new Rect();
            _spectrumPaint.getTextBounds(_unitText, 0, _unitText.length(), unitRect);
            _spectrumCanvas.drawText(_unitText, h / 2 - (int) (unitRect.width() / 2), unitRect.height(), _spectrumPaint);
            _spectrumCanvas.save();
            _spectrumCanvas.translate(h, 0);
            _spectrumCanvas.rotate(90);
        }

        if (_showMode != ShowMode.Spectrum) {
            // 把色带画在短刻度线处
            LinearGradient linearGradient = new LinearGradient(_marginLeft, _marginTop, _marginLeft + _scaleLineLength, h - _spectrumMarginBottom, _gradientColors, null, Shader.TileMode.CLAMP);
            _gradientPaint.setShader(linearGradient);
            _spectrumCanvas.drawRect(_marginLeft, _marginTop, _marginLeft + _scaleLineLength, h - _spectrumMarginBottom, _gradientPaint);
        }

        // 画刻度
        _spectrumPaint.setColor(_gridColor);
        _spectrumPaint.setTextSize(_scaleFontSize);

        int scaleHeight = h - _marginTop - _spectrumMarginBottom;   // 绘制区总高度
        int scaleWidth = w - _marginLeft - _marginRight - _scaleLineLength;  // 绘制区总宽度
        int maxValue = _maxValue + _offsetY - _zoomOffsetY;        // 刻度上显示的最大值
        int minValue = _minValue + _offsetY + _zoomOffsetY;        // 刻度上显示的最小值

        float perScaleHeight = scaleHeight / (float) _gridCount;     // 每一格的高度
        float perScaleWidth = scaleWidth / (float) _gridCount;       // 每一格的宽度
        int perScaleValue = (maxValue - minValue) / _gridCount;      // 每一格的刻度值大小

        Rect scaleRect = new Rect();
        _spectrumPaint.getTextBounds(maxValue + "", 0, (maxValue + "").length(), scaleRect);

        for (int i = 0; i <= _gridCount; i++) {
            // 从上往下画，先画刻度值，然后在画横轴和纵轴
            int height = (int) (i * perScaleHeight) + _marginTop;
            int width = _marginLeft + _scaleLineLength + (int) (i * perScaleWidth);
            int startWidth = _marginLeft;     // 起始位置 X
            int textHeight = 0;

            if ((i + 1) % 2 != 0) {
                // 奇数位需要多画 _scaleLineLength 的长度横线
                if (i == 0) {
                    textHeight += scaleRect.height();   // 起始点的刻度在线的上方
                } else if (i == _gridCount) {

                } else {
                    textHeight += scaleRect.height() / 2;  // 其它点在刻度线的中部
                }
                // 画纵轴刻度值
                int scaleValue = maxValue - i * perScaleValue;
                float scaleTextLen = _spectrumPaint.measureText(scaleValue + "");
                _spectrumCanvas.drawText(scaleValue + "", startWidth - scaleTextLen, height + textHeight, _spectrumPaint);
            } else {
                startWidth += _scaleLineLength;
            }

            // 画横轴
            _spectrumCanvas.drawLine(startWidth, height, w - _marginRight, height, _spectrumPaint);
            // 画纵轴
            _spectrumCanvas.drawLine(width, _marginTop, width, h - _spectrumMarginBottom, _spectrumPaint);
        }
    }

    private void drawSpectrumLine() {
        if (_data == null || _data.length == 0 || _startIndex >= _endIndex) {
            return;
        }

        int w = _spectrumBmp.getWidth();
        int h = _spectrumBmp.getHeight();
        _spectrumPaint.setColor(_realTimeLineColor);
        _spectrumPaint.setStyle(Paint.Style.STROKE);
        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;
        int scaleHeight = h - _marginTop - _spectrumMarginBottom;    // 绘制区总高度
        int scaleWidth = w - _marginLeft - _marginRight - _scaleLineLength;  // 绘制区总宽度
        float perHeight = scaleHeight / (float) Math.abs(maxValue - minValue);     // 每格的高度
        float perWidth = scaleWidth / (float) (_endIndex - _startIndex);

        Path realTimePath = new Path();
        Path maxLinePath = null;
        Path minLinePath = null;

        for (int i = _startIndex; i <= _endIndex; i++) {
            if (i >= _data.length)
                continue;   //防止越界

            float level = _data[i];
            int x = (int) ((i - _startIndex) * perWidth) + _marginLeft + _scaleLineLength;
            int y = (int) ((maxValue - level) * perHeight) + _marginTop;
            if (i == _startIndex) {
                realTimePath.moveTo(x, y);
            } else {
                realTimePath.lineTo(x, y);
            }

            if (_drawMaxLine) {
                if (maxLinePath == null) {
                    maxLinePath = new Path();
                }

                float maxLevel = _maxData[i];
                int max_x = (int) ((i - _startIndex) * perWidth) + _marginLeft + _scaleLineLength;
                int max_y = (int) ((maxValue - maxLevel) * perHeight) + _marginTop;
                if (i == _startIndex) {
                    maxLinePath.moveTo(max_x, max_y);
                } else {
                    maxLinePath.lineTo(max_x, max_y);
                }
            }

            if (_drawMinLine) {
                if (minLinePath == null) {
                    minLinePath = new Path();
                }

                float minLevel = _minData[i];
                int min_x = (int) ((i - _startIndex) * perWidth) + _marginLeft + _scaleLineLength;
                int min_y = (int) ((maxValue - minLevel) * perHeight) + _marginTop;
                if (i == _startIndex) {
                    minLinePath.moveTo(min_x, min_y);
                } else {
                    minLinePath.lineTo(min_x, min_y);
                }
            }
        }

        _spectrumCanvas.drawPath(realTimePath, _spectrumPaint);
        if (maxLinePath != null) {
            _spectrumPaint.setColor(_maxValueLineColor);
            _spectrumCanvas.drawPath(maxLinePath, _spectrumPaint);
        }
        if (minLinePath != null) {
            _spectrumPaint.setColor(_minValueLineColor);
            _spectrumCanvas.drawPath(minLinePath, _spectrumPaint);
        }

        // 覆盖上边和下边，时频谱看上去是在指定的区域进行绘制的
        _spectrumPaint.setStyle(Paint.Style.FILL);
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            _spectrumPaint.setColor(color);
            _spectrumCanvas.drawRect(_marginLeft + _scaleLineLength, 0, w - _marginRight, _marginTop, _spectrumPaint);
            _spectrumCanvas.drawRect(_marginLeft + _scaleLineLength, h - _spectrumMarginBottom + 1, w - _marginRight, h, _spectrumPaint);
        }

        // 计算并绘制中心频率和带宽\
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
        _spectrumPaint.setColor(_gridColor);
        _paint.getTextBounds(centerFreqStr, 0, centerFreqStr.length(), freqRect);
        _spectrumCanvas.drawText(centerFreqStr, w - _marginRight - scaleWidth / 2 - freqRect.width() / 2, h - _spectrumMarginBottom + freqRect.height() + 10, _spectrumPaint);
        _spectrumCanvas.drawText(startFreqStr, _marginLeft + _scaleLineLength, h - _spectrumMarginBottom + freqRect.height() + 10, _spectrumPaint);
        _spectrumCanvas.drawText(endFreqStr, w - _marginRight - (float) _spectrumPaint.measureText(endFreqStr), h - _spectrumMarginBottom + freqRect.height() + 10, _spectrumPaint);
    }

    private void drawWaterfall() {
        int w = _waterfallBmp.getWidth();
        int h = _waterfallBmp.getHeight();

        if (_showMode == ShowMode.Waterfall) {
            // 如果只显示瀑布图，那么就把色带和瀑布画在一起
            LinearGradient linearGradient = new LinearGradient(0, _marginTop, _marginLeft, h - _marginBottom, _gradientColors, null, Shader.TileMode.CLAMP);
            _gradientPaint.setShader(linearGradient);
            _waterfallCanvas.drawRect(0, _marginTop, _marginLeft, h - _marginBottom, _gradientPaint);
        }

        if (_data == null || _data.length == 0 || _startIndex >= _endIndex) {
            return;
        }

        int scaleLineLength = _showMode == ShowMode.Waterfall ? 0 : _scaleLineLength;
        float perWidth = ((w - _marginLeft - scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex));
        float perHeight = (h - _marginTop - _marginBottom) / (float) _rainRow;
        byte[] indexArr = levelToColorIndex(_data);    // 电平到颜色索引的映射

//        if (_drawCount == 0) {
//            // 第一次绘制
//            for (int i = _startIndex; i < _endIndex; i++) {
//                int width = (int) ((i - _startIndex) * perWidth) + _marginLeft + scaleLineLength;
//                int height = 0 + _marginTop;
//                _waterfallPaint.setColor(_gradientColors[_gradientColors.length - 1 - indexArr[i]]);
//                _waterfallCanvas.drawRect(width, height, width + perWidth, height + perHeight, _waterfallPaint);
//            }
//            _drawCount++;
//        } else {
//            if (_drawCount < _rainRow) {
//                // 还未画满，则裁剪之前的所有图像
//                Bitmap bitmap = Bitmap.createBitmap(_waterfallBmp, _marginLeft + scaleLineLength, 0 + _marginTop, w - _marginLeft - scaleLineLength - _marginRight, (int) (_drawCount * perHeight));
//                _waterfallCanvas.drawBitmap(bitmap, _marginLeft + scaleLineLength, _marginTop, _waterfallPaint);
//                bitmap.recycle();
//            } else if (_drawCount == _rainRow) {
//                // 如果已画满，则裁剪之前 1到n 高的图像
//                Bitmap bitmap = Bitmap.createBitmap(_waterfallBmp, _marginLeft + scaleLineLength, (int) perHeight + _marginTop, w - _marginLeft - scaleLineLength - _marginRight, (int) ((_drawCount - 1) * perHeight));
//                _waterfallCanvas.drawBitmap(bitmap, _marginLeft + scaleLineLength, _marginTop, _waterfallPaint);
//                bitmap.recycle();
//            }
//            _drawCount++;
//
//            if (_drawCount >= _rainRow) {
//                _drawCount = _rainRow;
//            }
//        }

        // 已优化，每次只画最后一包，然后往前挪，再画新的数据，与上面比较简化了流程
        Bitmap bitmap = Bitmap.createBitmap(_waterfallBmp, (_marginLeft + scaleLineLength), (int) (0 + _marginTop + perHeight), w - _marginLeft - scaleLineLength - _marginRight, (int) (h - _marginTop - _marginBottom - 1 * perHeight));
        _waterfallCanvas.drawBitmap(bitmap, _marginLeft + scaleLineLength, _marginTop, _waterfallPaint);

        for (int i = _startIndex; i < _endIndex; i++) {
            int width = (int) ((i - _startIndex) * perWidth) + _marginLeft + scaleLineLength;
            int height = (int) (h - _marginBottom - perHeight); //(int) ((_drawCount - 1) * perHeight) + _marginTop;
            _waterfallPaint.setColor(_gradientColors[_gradientColors.length - 1 - indexArr[i]]);
            _waterfallCanvas.drawRect(width, height, width + perWidth, height + perHeight, _waterfallPaint);
        }
    }

    /**
     * 绘制框选区域
     *
     * @param canvas
     */
    private void drawSelectZone(Canvas canvas) {
        if (_handelType == HandleType.ZONE) {
            if (_endX == _startX) {
                // 如果相等，则绘制当前点的频率和垂直线条
                _drawPaint.setColor(Color.RED);
                _drawPaint.setStyle(Paint.Style.STROKE);
                _drawPaint.setStrokeWidth(2);
                _drawPaint.setTextSize(30);
                canvas.drawLine(_startX, _marginTop, _startX, _height - _marginBottom, _drawPaint);

                int scaleLineLength = _showMode == ShowMode.Waterfall ? 0 : _scaleLineLength;
                float perScaleLength = (_width - _marginLeft - scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft - scaleLineLength) / perScaleLength);
                double perFreq = _spectrumSpan / _data.length / 1000;
                String currentFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + "MHz";     // 当前点的频率
                _drawPaint.setStyle(Paint.Style.FILL);
                Rect rect = new Rect();
                _drawPaint.getTextBounds(currentFreqStr, 0, currentFreqStr.length(), rect);
                canvas.drawText(currentFreqStr, _startX, _marginTop + rect.height() + 2, _drawPaint);
            } else if (_endX < _startX) {
                // 缩小
                int marginBottom = _showMode == ShowMode.Spectrum ? _spectrumMarginBottom : _marginBottom;
                _drawPaint.setColor(_selectZoneColor);
                _drawPaint.setStrokeWidth(4);
                canvas.drawLine(_startX, _marginTop, _endX, _height - marginBottom, _drawPaint);
                canvas.drawLine(_endX, _marginTop, _startX, _height - marginBottom, _drawPaint);
                canvas.drawRect(_endX, _marginTop + 1, _startX, _height - marginBottom, _drawPaint);
            } else {
                // 放大，绘制矩形和计算起始、终止频率以及实时带宽
                int marginBottom = _showMode == ShowMode.Spectrum ? _spectrumMarginBottom : _marginBottom;
                _drawPaint.setColor(_selectZoneColor);
                canvas.drawRect(_startX, _marginTop + 1, _endX, _height - marginBottom, _drawPaint);

                float perScaleLength = (_width - _marginLeft - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft) / perScaleLength);
                double perFreq = _spectrumSpan / _data.length / 1000;
                String startFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String endFreqStr = String.format("%.3f", tempEndIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String spanStr = String.format("%.3f", perFreq * (tempEndIndex - tempStartIndex) * 1000) + " kHz";

                Rect rect = new Rect();
                _drawPaint.getTextBounds(spanStr, 0, spanStr.length(), rect);
                _drawPaint.setColor(Color.WHITE);
                canvas.drawText(startFreqStr, _startX, _marginTop + rect.height() + 5, _drawPaint);
                canvas.drawText(endFreqStr, _startX, _marginTop + rect.height() * 2 + 8, _drawPaint);
                canvas.drawText(spanStr, _startX, _marginTop + rect.height() * 3 + 11, _drawPaint);
            }
        }
    }

    private void actionDown(MotionEvent event) {
        if (_showMode == ShowMode.Waterfall) {
            // 如果只有瀑布图，那就判断点是否在瀑布图区域
            if (Utils.IsPointInRect(_marginLeft, _marginTop, _width - _marginRight, _height - _marginBottom, (int) event.getX(), (int) event.getY())) {
                _handelType = HandleType.ZONE;
                _startX = _endX = event.getX();
            }
        } else {
            // 如果是频谱图或者频谱和瀑布图，那么需要判断是Y轴还是主区域
            // 频谱图的 Y 轴是拖动
            // 主区域如果是单点，那就是缩放
            if (Utils.IsPointInRect(0, 0, _marginLeft + _scaleLineLength, _spectrumBmp.getHeight(), (int) event.getX(), (int) event.getY())) {
                _handelType = HandleType.DRAG;    // Y轴拖动
                _startY = event.getY();
            } else if (Utils.IsPointInRect(_marginLeft + _scaleLineLength, _marginTop, _width - _marginRight, _height - _marginBottom, (int) event.getX(), (int) event.getY())) {
                _handelType = HandleType.ZONE;    // 频谱框选缩放
                _startX = _endX = event.getX();
            }
        }
    }

    private void actionPointerDown(MotionEvent event) {
        // 如果是多点，那就判断是不是频谱图的区域，频谱图就是Y轴缩放
        if (_showMode == ShowMode.Waterfall)
            return;

        // 两个点都需要在频谱图主区域
        int marginBottom = _showMode == ShowMode.Both ? _spectrumMarginBottom : _marginBottom;
        if (Utils.IsPointInRect(_marginLeft, _marginTop, _width - _marginRight, _spectrumBmp.getHeight() - marginBottom, (int) event.getX(0), (int) event.getY(0)) &&
                Utils.IsPointInRect(_marginLeft, _marginTop, _width - _marginRight, _spectrumBmp.getHeight() - marginBottom, (int) event.getX(1), (int) event.getY(1))) {
            // 频谱缩放
            _handelType = HandleType.ZOOM;
            _oldDistanceY = Math.abs(event.getY(0) - event.getY(1));
        }
    }

    private void actionMove(MotionEvent event) {
        if (_handelType == HandleType.ZONE) {
            _endX = event.getX();
            int scaleLineLength = _showMode == ShowMode.Waterfall ? 0 : _scaleLineLength;
            if (_endX < _marginLeft + scaleLineLength) {
                _endX = _marginLeft + scaleLineLength;
            } else if (_endX > _width - _marginRight) {
                _endX = _width - _marginRight;
            }
            postInvalidate();
        } else {
            int marginBottom = _showMode == ShowMode.Waterfall.Both ? _spectrumMarginBottom : _marginBottom;

            if (_handelType == HandleType.DRAG) {
                float currentY = event.getY();
                int spanScale = (int) ((currentY - _startY) / ((_spectrumBmp.getHeight() - _marginTop - marginBottom) / Math.abs(_maxValue - _minValue)));
                if (spanScale != 0) {
                    _offsetY = spanScale;
                    postInvalidate();
                }
            } else if (_handelType == HandleType.ZOOM && event.getPointerCount() == 2) {
                // 多点触控 Y 轴缩放
                float currentDistanceY = Math.abs(event.getY(0) - event.getY(1));
                float perScaleHeight = (_spectrumBmp.getHeight() - _marginTop - marginBottom) / (float) Math.abs(_maxValue - _minValue);
                int spanScale = (int) ((currentDistanceY - _oldDistanceY) / perScaleHeight);
                if (spanScale != 0 && ((_maxValue - spanScale) - (_minValue + spanScale) >= _gridCount)) {
                    // 防止交叉越界，并且在放大到 总刻度长为 _gridCount 时，不能再放大
                    _zoomOffsetY = spanScale;
                    postInvalidate();
                }
            } else {
                // 单点框选缩放
                _endX = event.getX();
                if (_endX < _marginLeft + _scaleLineLength) {
                    _endX = _marginLeft + _scaleLineLength;
                } else if (_endX > _width - _marginRight) {
                    _endX = _width - _marginRight;
                }
                postInvalidate();
            }
        }
    }

    private void actionPointerUp(MotionEvent event) {
        if (_handelType == HandleType.ZOOM) {
            _maxValue -= _zoomOffsetY;
            _minValue += _zoomOffsetY;
            _zoomOffsetY = 0;
        }

        _handelType = HandleType.NONE;
    }

    private void actionCancelUp(MotionEvent event) {
        if (_handelType == HandleType.ZONE) {
            if (_startX > _endX) {
                // 缩小
                _startIndex = 0;
                _endIndex = _data.length;
                postInvalidate();
            } else if (_startX < _endX) {
                // 放大，根据 _startX 和 _endX 来确定 _startIndex 和 _endIndex，以及中心频率和带宽
                if (_data.length == 0 || _endIndex - _startIndex <= 2) {
                    // 没有数据，或者只要小于2个点时，不再放大
                    _handelType = HandleType.NONE;
                    return;
                }

                int scaleLineLength = _showMode == ShowMode.Waterfall ? 0 : _scaleLineLength;
                float perScaleLength = (_width - _marginLeft - scaleLineLength - _marginRight) / (float) (_endIndex - _startIndex);   // 一格的距离
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft - scaleLineLength) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft - scaleLineLength) / perScaleLength);
                if (tempEndIndex > tempStartIndex) {  // 至少保证有2个点
                    _endIndex = tempEndIndex;
                    _startIndex = tempStartIndex;
                    postInvalidate();
                }
            }
        } else if (_handelType == HandleType.DRAG) {
            _maxValue += _offsetY;
            _minValue += _offsetY;
            _offsetY = 0;
            postInvalidate();
        }

        _handelType = HandleType.NONE;
    }

    /**
     * 频谱幅度值到颜色的映射
     *
     * @param data
     * @return
     */
    private byte[] levelToColorIndex(float[] data) {
        byte[] colors = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byte index;
            float value = data[i];
            if (value <= _minValue) {
                index = (byte) (_bRool ? 1 : 0);
            } else if (value >= _maxValue) {
                index = (byte) (_gradientColors.length - 1);
            } else {
                index = (byte) ((value - _minValue) / (_maxValue - _minValue) * (_gradientColors.length - 1));
            }

            colors[i] = index;
        }

        return colors;
    }

    /**
     * 初始化色带
     */
    private void initColorGradient(int colorIndex) {
        if (colorIndex < 0) {
            colorIndex = 0;
        } else if (colorIndex > 3) {
            colorIndex = 3;
        }

        switch (colorIndex) {
            case 0:
                _gradientColors = new int[]{
                        // RED
                        Color.rgb(217, 67, 54),
                        Color.rgb(224, 102, 80),
                        Color.rgb(230, 132, 102),
                        Color.rgb(238, 170, 128),
                        Color.rgb(248, 222, 167),
                        Color.rgb(236, 236, 177),
                        Color.rgb(172, 172, 132),
                        Color.rgb(161, 161, 125),
                        Color.rgb(129, 129, 102),
                        Color.rgb(114, 114, 90),
                        Color.rgb(85, 85, 70),
                        Color.rgb(55, 55, 49),
                        Color.rgb(38, 38, 37)
                };
                break;
            case 1:
                _gradientColors = new int[]{
                        // GREEN
                        Color.rgb(32, 206, 38),
                        Color.rgb(29, 213, 79),
                        Color.rgb(24, 225, 145),
                        Color.rgb(21, 231, 183),
                        Color.rgb(18, 238, 222),
                        Color.rgb(17, 233, 225),
                        Color.rgb(21, 185, 179),
                        Color.rgb(23, 155, 150),
                        Color.rgb(25, 133, 129),
                        Color.rgb(28, 104, 101),
                        Color.rgb(29, 81, 79),
                        Color.rgb(31, 53, 52),
                        Color.rgb(32, 48, 48)
                };
                break;
            case 2:
                _gradientColors = new int[]{
                        // BLUE
                        Color.rgb(233, 0, 244),
                        Color.rgb(212, 0, 244),
                        Color.rgb(154, 0, 244),
                        Color.rgb(124, 0, 244),
                        Color.rgb(81, 0, 244),
                        Color.rgb(68, 0, 244),
                        Color.rgb(32, 0, 244),
                        Color.rgb(23, 7, 199),
                        Color.rgb(25, 12, 166),
                        Color.rgb(27, 18, 132),
                        Color.rgb(29, 23, 97),
                        Color.rgb(30, 26, 77),
                        Color.rgb(32, 30, 51)
                };
                break;
            case 3:
                _gradientColors = new int[]{
                        // COLOR
                        Color.rgb(208, 26, 1),
                        Color.rgb(221, 105, 1),
                        Color.rgb(237, 206, 1),
                        Color.rgb(184, 227, 1),
                        Color.rgb(122, 231, 1),
                        Color.rgb(30, 236, 1),
                        Color.rgb(28, 236, 72),
                        Color.rgb(47, 234, 131),
                        Color.rgb(71, 206, 197),
                        Color.rgb(57, 143, 137),
                        Color.rgb(45, 91, 88),
                        Color.rgb(46, 96, 93),
                        Color.rgb(36, 47, 47),
                };
                break;
        }
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
