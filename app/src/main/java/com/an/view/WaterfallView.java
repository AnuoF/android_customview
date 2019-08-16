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
public class WaterfallView extends View implements View.OnTouchListener, OnDrawFinishedListener {

    private int _rainRow;                   // 雨点图行数，Y轴数据量，应 <= _height
    private short _zAxisMax;                // Z轴最大值
    private short _zAxisMin;                // Z轴最小智
    private int[] _colors;                  // 色带

    private int _marginTop;                // 上边距
    private int _marginBottom;             // 下边距
    private int _marginLeft;               // 左边距
    private int _marginRight;              // 右边距

    private int _width;                    // View 宽
    private int _height;                   // View 高
    private Paint _paint;                  // 色带画笔
    public Bitmap _bitmap;                 // 雨点图缓存， _wCanvas 需要对其进行剪切
    private WaterfallCanvas _wCanvas;      // 瀑布图绘制类
    private int[][] _colorArray;           // 默认的 4 个色带
    private boolean _bRool;                // 是否滚动

    private ExecutorService _executorService;      // 线程池服务


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
            _rainRow = typedArray.getInt(R.styleable.WaterfallView_rainRow_Wv, 200);
            _zAxisMax = (short) typedArray.getInt(R.styleable.WaterfallView_zAxisMax_Wv, 80);
            _zAxisMin = (short) typedArray.getInt(R.styleable.WaterfallView_zAxizMin_Wv, -20);

            _paint = new Paint();
            initColorGradient();
            _bRool = true;
            _executorService = Executors.newFixedThreadPool(5);
            setOnTouchListener(this);

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
        initColorGradient();
        _bRool = true;
        _executorService = Executors.newFixedThreadPool(5);
        setOnTouchListener(this);
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
        if (_wCanvas != null) {
            _executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (_wCanvas != null)
                        _wCanvas.setData(WaterfallView.this, frequency, span, data);
                }
            });
        }
    }

    /**
     * 清除数据和图形
     */
    public void clear() {
        if (_wCanvas != null) {
            _wCanvas.clear();
        }
    }

    /**
     * 初始化色带
     */
    private void initColorGradient() {
        _colorArray = new int[4][13];
        _colorArray[0] = new int[]{
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
        _colorArray[1] = new int[]{
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
        _colorArray[2] = new int[]{
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
        _colorArray[3] = new int[]{
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

        _colors = _colorArray[3];
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

    @Override
    public void onSpectrumFinished() {
        // 回调完成之后，调用重绘
        postInvalidate();
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

        } else {

        }
    }

    private void actionMove(MotionEvent event) {

    }

    private void actionPointerDown(MotionEvent event) {

    }

    private void actionPointerUp(MotionEvent event) {

    }

    private void actionUpCancel(MotionEvent event) {

    }

    private void showInfo(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
