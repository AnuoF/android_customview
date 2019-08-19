/**
 * @Title: WaterfallView.java
 * @Package: com.an.view
 * @Description: 自定义频谱瀑布图控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.14 11:50
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
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.an.customview.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 自定义频谱瀑布图控件（语谱图）
 */
public class WaterfallView extends View implements View.OnTouchListener, OnDrawFinishedListener, GradientColorDialog.OnItemClickListener {

    private int _rainRow;                   // 雨点图行数，Y轴数据量，应 <= _height
    private short _zAxisMax;                // Z轴最大值
    private short _zAxisMin;                // Z轴最小智
    private int[] _colors;                  // 色带

    private int _marginTop;                 // 上边距
    private int _marginBottom;              // 下边距
    private int _marginLeft;                // 左边距
    private int _marginRight;               // 右边距

    private int _width;                     // View 宽
    private int _height;                    // View 高
    private Paint _paint;                   // 色带画笔
    private Paint _rectPaint;               // 用于绘制选择区域
    public Bitmap _bitmap;                  // 雨点图缓存， _wCanvas 需要对其进行剪切
    private WaterfallCanvas _wCanvas;       // 瀑布图绘制类
    private boolean _bRool;                 // 是否滚动

    private ExecutorService _executorService;      // 线程池服务
    private GradientColorDialog _dialog;           // 色带选择对话框

    private float _startX;                    // 横向放大的起始点 X
    private float _endX;                      // 横向放大的终止点 X，开始时应与 _starX 相等
    private int _startIndex;
    private int _endIndex;

    private double _frequency;
    private double _spectrumSpan;
    private int _dataLength;                  // 数据长度

    private HandleType _handleType;         // 手势操作类型

    public WaterfallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public WaterfallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WaterfallView(Context context) {
        super(context);
        initView();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterfallView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.WaterfallView_marginTop_Wv, 0);
            _marginBottom = typedArray.getInt(R.styleable.WaterfallView_marginBottom_Wv, 0);
            _marginLeft = typedArray.getInt(R.styleable.WaterfallView_marginLeft_Wv, 60);
            _marginRight = typedArray.getInt(R.styleable.WaterfallView_marginRight_Wv, 0);
            _rainRow = typedArray.getInt(R.styleable.WaterfallView_rainRow_Wv, 100);
            _zAxisMax = (short) typedArray.getInt(R.styleable.WaterfallView_zAxisMax_Wv, 80);
            _zAxisMin = (short) typedArray.getInt(R.styleable.WaterfallView_zAxizMin_Wv, -20);
            int colorIndex = typedArray.getInt(R.styleable.WaterfallView_colorIndex_Wv, 3);

            _paint = new Paint();
            _rectPaint = new Paint();
            initColorGradient(colorIndex);
            _bRool = true;
            _executorService = Executors.newFixedThreadPool(5);
            setOnTouchListener(this);
            _handleType = HandleType.NONE;
        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 0;
        _marginBottom = 0;
        _marginLeft = 60;
        _marginRight = 0;
        _rainRow = 200;
        _zAxisMin = -20;
        _zAxisMax = 80;

        _paint = new Paint();
        _rectPaint = new Paint();
        initColorGradient(3);
        _bRool = true;
        _executorService = Executors.newFixedThreadPool(5);
        setOnTouchListener(this);
        _handleType = HandleType.NONE;
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
                _colors = new int[]{
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
                _colors = new int[]{
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
                _colors = new int[]{
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
                _colors = new int[]{
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
     * 在创建对象后初始化，设置对象的值
     */
    private void initCanvas() {
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            _wCanvas._backgroundColor = colorDrawable.getColor();
        }

        if (_rainRow > _height) {
            _rainRow = _height;
        } else if (_rainRow <= 0) {
            _rainRow = _height / 2;
        }

        _wCanvas._rainRow = _rainRow;
        _wCanvas._ZAxisMax = _zAxisMax;
        _wCanvas._ZAxisMin = _zAxisMin;
        _wCanvas._colors = _colors;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this._width = getMeasuredWidth();
        this._height = getMeasuredHeight();

        if (_bitmap == null) {
            _bitmap = Bitmap.createBitmap(_width - _marginLeft - _marginRight, _height - _marginTop - _marginBottom, Bitmap.Config.ARGB_8888);
            _wCanvas = new WaterfallCanvas(new Canvas(_bitmap), this);
            initCanvas();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (_bitmap != null) {
            _wCanvas = null;
            _bitmap.recycle();
            _bitmap = null;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (Utils.checkValid()) {
            drawGradientRect(canvas);
            if (_bitmap != null) {
                canvas.drawBitmap(_bitmap, _marginLeft, _marginTop, _paint);
            }
            drawSelectRect(canvas);

            super.onDraw(canvas);
        }
    }

    /**
     * 设置频谱数据
     *
     * @param frequency 中心频率 MHz
     * @param span      带宽 kHz
     * @param data      数据
     */
    public void setData(final double frequency, final double span, final float[] data) {
        if (frequency != _frequency || span != _spectrumSpan) {
            _startIndex = 0;
            _endIndex = data.length;
            _frequency = frequency;
            _spectrumSpan = span;
            _dataLength = data.length;
        }

        if (_wCanvas != null) {
            _executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (_wCanvas != null) {
                        _wCanvas.setData(frequency, span, data);
                    }
                }
            });
        }
    }

    /**
     * 清除数据和图形
     */
    public void clear() {
        _dataLength = 0;
        _spectrumSpan = 0;

        if (_wCanvas != null) {
            _wCanvas.clear();
        }
    }

    /**
     * 画色带
     *
     * @param canvas
     */
    private void drawGradientRect(Canvas canvas) {
        LinearGradient linearGradient = new LinearGradient(0, 0, _marginLeft, _height, _colors, null, Shader.TileMode.CLAMP);
        _paint.setShader(linearGradient);
        canvas.drawRect(0, 0, _marginLeft, _height, _paint);
    }

    private void drawSelectRect(Canvas canvas) {
        if (_handleType == HandleType.ZONE) {
            if (_endX == _startX) {
                // 如果相等，则绘制当前点的频谱和垂直线条
                _rectPaint.setColor(Color.RED);
                _rectPaint.setStyle(Paint.Style.STROKE);
                _rectPaint.setStrokeWidth(2);
                canvas.drawLine(_startX, _marginTop, _startX, _height - _marginBottom, _rectPaint);

                float perScaleLength = (_width - _marginLeft - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft) / perScaleLength);
                double perFreq = _spectrumSpan / _dataLength / 1000;
                String currentFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + "MHz";     // 当前点的频率
                _rectPaint.setStyle(Paint.Style.FILL);
                Rect rect = new Rect();
                _rectPaint.getTextBounds(currentFreqStr, 0, currentFreqStr.length(), rect);
                canvas.drawText(currentFreqStr, _startX, _marginTop + rect.height() + 2, _rectPaint);
            } else if (_endX < _startX) {
                // 缩小
                _rectPaint.setColor(Color.argb(50, 255, 0, 0));
                _rectPaint.setStrokeWidth(4);
                canvas.drawLine(_startX, _marginTop, _endX, _height - _marginBottom, _rectPaint);
                canvas.drawLine(_endX, _marginTop, _startX, _height - _marginBottom, _rectPaint);
                canvas.drawRect(_endX, _marginTop + 1, _startX, _height - _marginBottom, _rectPaint);
            } else {
                // 放大，绘制矩形和计算起始、终止频率以及实时带宽
                _rectPaint.setColor(Color.argb(50, 255, 0, 0));
                canvas.drawRect(_startX, _marginTop + 1, _endX, _height - _marginBottom, _rectPaint);

                float perScaleLength = (_width - _marginLeft - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft) / perScaleLength);
                double perFreq = _spectrumSpan / _dataLength / 1000;
                String startFreqStr = String.format("%.3f", tempStartIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String endFreqStr = String.format("%.3f", tempEndIndex * perFreq + (_frequency - _spectrumSpan / 2 / 1000)) + " MHz";
                String spanStr = String.format("%.3f", perFreq * (tempEndIndex - tempStartIndex) * 1000) + " kHz";

                Rect rect = new Rect();
                _rectPaint.getTextBounds(spanStr, 0, spanStr.length(), rect);
                _rectPaint.setColor(Color.WHITE);
                canvas.drawText(startFreqStr, _startX, _marginTop + rect.height() + 5, _rectPaint);
                canvas.drawText(endFreqStr, _startX, _marginTop + rect.height() * 2 + 8, _rectPaint);
                canvas.drawText(spanStr, _startX, _marginTop + rect.height() * 3 + 11, _rectPaint);
            }
        }
    }

    @Override
    public void onDrawFinished() {
        // 回调完成之后，调用重绘
        postInvalidate();
    }

    @Override
    public void OnClick(GradientColorDialog dialog, View view) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionPointerDown(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionPointerUp(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                actionUpCancel(motionEvent);
                break;
        }

        return true;
    }

    private void actionDown(MotionEvent event) {
        if (Utils.IsPointInRect(0, 0, _marginLeft, _height, (int) event.getX(), (int) event.getY())) {
            _handleType = HandleType.LONG_PRESS;
        } else {
            _handleType = HandleType.ZONE;
            _startX = _endX = event.getX();
        }
    }

    private void actionMove(MotionEvent event) {
        if (_handleType == HandleType.ZONE) {
            _endX = event.getX();
            if (_endX < _marginLeft) {
                _endX = _marginLeft;
            } else if (_endX > _width - _marginRight) {
                _endX = _width - _marginRight;
            }  // 此处的判断是为了防止Rect越界

            postInvalidate();
        }
    }

    private void actionPointerDown(MotionEvent event) {

    }

    private void actionPointerUp(MotionEvent event) {

    }

    private void actionUpCancel(MotionEvent event) {
        if (_handleType == HandleType.ZONE) {
            if (_startX > _endX) {
                // 缩小
                _startIndex = 0;
                _endIndex = _dataLength;
                _wCanvas.zoneRange(0, _dataLength);
            } else if (_startX < _endX) {
                // 放大。 根据 _startX 和 _endX 来确定 _startIndex 和 _endIndex，以及中心频率和带宽
                if (_dataLength == 0 || _endIndex - _startIndex <= 2) {  // 没有数据，或者只要小于2个点时，不再放大
                    _handleType = HandleType.NONE;
                    return;
                }

                float perScaleLength = (_width - _marginLeft - _marginRight) / (float) (_endIndex - _startIndex); //  一格的距离
                // 在放大的基础上再次放大，巧妙啊，佩服我自己了，哈哈哈
                int tempEndIndex = _startIndex + (int) ((_endX - _marginLeft) / perScaleLength);
                int tempStartIndex = _startIndex + (int) ((_startX - _marginLeft) / perScaleLength);
                if (tempEndIndex > tempStartIndex) {   // 保证至少有2个点（一条直线）
                    _endIndex = tempEndIndex;
                    _startIndex = tempStartIndex;

                    _wCanvas.zoneRange(_startIndex, _endIndex);
                }
            }

            _handleType = HandleType.NONE;
        }
    }

    /**
     * 显示色带对话框
     */
    private void showGradientColorDialog() {
        if (_dialog == null) {
            _dialog = new GradientColorDialog(this.getContext(), R.layout.layout_gradient_color_dialog, new int[]{});
            _dialog.setOnItemClickListener(this);
        } else {
            _dialog.show();
        }
    }

    private void showInfo(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 手势处理类型枚举
     */
    private enum HandleType {
        NONE,           // 无任何操作
        LONG_PRESS,     // 长按操作
        ZONE            // 频谱放大缩小，显示区间
    }
}
