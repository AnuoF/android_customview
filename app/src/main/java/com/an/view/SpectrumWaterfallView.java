/**
 * @Title: SpectrumWaterfallView.java
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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.an.customview.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 频谱图和瀑布图的组合控件
 */
public class SpectrumWaterfallView extends View implements View.OnTouchListener, OnDrawFinishedListener {

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
    private int _gradientColorIndex;          // 瀑布图颜色索引  取值0-3
    private int _realTimeLineColor;           // 实时值线条颜色
    private int _maxValueLineColor;           // 最大值线条颜色
    private int _minValueLineColor;           // 最小值线条颜色
    private int _scaleFontSize;               // 刻度字体大小

    private int _width;                       // 测量 宽
    private int _height;                      // 测量 高

    private Paint _spectrumPaint;             // 频谱图画笔
    private int _scaleLineLength;             // 小刻度线长度
    private boolean _drawMaxLine;             // 绘制最大值
    private boolean _drawMinLine;             // 绘制最小值

    private Paint _waterfallPaint;            // 瀑布图画笔
    private int[] _gradientColors;            // 色带渐变色

    // 频谱数据
    private double _frequency;                // 中心频率
    private double _spectrumSpan;             // 频谱带宽
    private float[] data;                     // 频谱数据
    private float[] _max;                     // 最大值
    private float[] _min;                     // 最小值

    private int _startIndex;                  // 提取数据的起始位置（用于数据缩放）
    private int _endIndex;                    // 提取数据的终止位置（用于数据缩放）

    private HandleType _handelType;           // 手势处理类型
    private int _startY;                      // 单点触控时的起始点 Y，频谱图Y轴拖动时用
    private float _startX;                    // 单点触控时的起始点 X，频谱缩放时用
    private int _endX;                        // 最后点 X， X 轴放大缩小用
    private int _offsetY;                     // 单点拖动时的Y轴偏移
    private float _oldDistanceY;              // 多点触控时，最初的距离，用于频谱纵向缩放
    private int _zoomOFFsetY;                 // 控制Y轴缩放

    private ExecutorService _executorService;      // 线程池服务，开启线程来进行图形绘制，避免占用主线程
    private Bitmap _panoramaBmp;             // 全景Bitmap，所有的图形集中到这张图上面
    private Bitmap _waterfallBmp;             // 瀑布图，不含渐变色带


    public SpectrumWaterfallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public SpectrumWaterfallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SpectrumWaterfallView(Context context) {
        super(context);
        initView();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.SpectrumWaterfallView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.SpectrumWaterfallView_marginTop, 5);
            _marginBottom = typedArray.getInt(R.styleable.SpectrumWaterfallView_marginBottom, 5);
            _marginLeft = typedArray.getInt(R.styleable.SpectrumWaterfallView_marginLeft, 50);
            _marginRight = typedArray.getInt(R.styleable.SpectrumWaterfallView_marginRight, 5);
            _gridColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_gridCount, Color.argb(200, 255, 255, 255));
            _gridCount = typedArray.getInt(R.styleable.SpectrumWaterfallView_gridCount, 10);
            _unitText = typedArray.getString(R.styleable.SpectrumWaterfallView_unitText);
            _unitSize = typedArray.getInt(R.styleable.SpectrumWaterfallView_unitSize, 20);
            _unitColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_unitColor, Color.WHITE);
            _maxValue = typedArray.getInt(R.styleable.SpectrumWaterfallView_maxValue, 80);
            _minValue = typedArray.getInt(R.styleable.SpectrumWaterfallView_minValue, -20);
            _selectZoneColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_selectZoneColor, Color.argb(50, 255, 0, 0));
            int mode = typedArray.getInt(R.styleable.SpectrumWaterfallView_showMode, 0);
            _showMode = ShowMode.values()[mode];
            _gradientColorIndex = typedArray.getInt(R.styleable.SpectrumWaterfallView_gradientColorIndex, 3);
            _realTimeLineColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_realTimeLineColor, Color.GREEN);
            _maxValueLineColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_maxValueLineColor, Color.RED);
            _minValueLineColor = typedArray.getColor(R.styleable.SpectrumWaterfallView_minValueLineColor, Color.BLUE);
            _scaleFontSize = typedArray.getInt(R.styleable.SpectrumWaterfallView_scaleFontSize, 20);

            _executorService = Executors.newFixedThreadPool(5);
            _scaleLineLength = 5;
            _spectrumPaint = new Paint();
            _waterfallPaint = new Paint();


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
        _gradientColorIndex = 3;
        _realTimeLineColor = Color.GREEN;
        _maxValueLineColor = Color.RED;
        _minValueLineColor = Color.BLUE;
        _scaleFontSize = 20;

        _executorService = Executors.newFixedThreadPool(5);
        _scaleLineLength = 5;
        _spectrumPaint = new Paint();
        _waterfallPaint = new Paint();

    }

    public void setData(double frequency, double spectrumSpan, float[] data) {
        if (_frequency != frequency || spectrumSpan != _spectrumSpan) {
            _startIndex = 0;
            _endIndex = data.length;
            _frequency = frequency;
            _spectrumSpan = spectrumSpan;
        }

        _executorService.execute(new Runnable() {
            @Override
            public void run() {


                postInvalidate();
            }
        });
    }

    /**
     * 设置了显示模式需要重绘图形
     *
     * @param mode
     */
    public void setShowMode(ShowMode mode) {
        if (mode == _showMode)
            return;

        _showMode = mode;
        drawBackground();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        _width = w;
        _height = h;
        drawBackground();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (_panoramaBmp != null) {
            canvas.drawBitmap(_panoramaBmp, 0, 0, _spectrumPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return true;
    }

    @Override
    public void onSpectrumFinished() {
        postInvalidate();   // 图形绘制完成之后通知重绘
    }

    /**
     * 画背景。包括坐标轴、网格和渐变色带
     */
    private void drawBackground() {
        if (_panoramaBmp != null) {
            _panoramaBmp.recycle();
            _panoramaBmp = null;
        }
        if (_waterfallBmp != null) {
            _waterfallBmp.recycle();
            _waterfallBmp = null;
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
