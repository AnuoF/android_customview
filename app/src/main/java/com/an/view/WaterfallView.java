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


            _paint = new Paint();
            initColorGradient();
            _bRool = true;
            _executorService = Executors.newFixedThreadPool(5);

        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 0;
        _marginBottom = 0;
        _marginLeft = 60;
        _marginRight = 0;

        _paint = new Paint();
        initColorGradient();
        _bRool = true;
        _executorService = Executors.newFixedThreadPool(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this._width = getMeasuredWidth();
        this._height = getMeasuredHeight();

        if (_bitmap == null) {
            _bitmap = Bitmap.createBitmap(_width - _marginLeft - _marginRight, _height - _marginTop - _marginBottom, Bitmap.Config.ARGB_8888);
            _wCanvas = new WaterfallCanvas(new Canvas(_bitmap), this);
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
        // G
        _colors = new int[13];
        _colors[0] = Color.rgb(32, 206, 38);
        _colors[1] = Color.rgb(29, 213, 79);
        _colors[2] = Color.rgb(24, 225, 145);
        _colors[3] = Color.rgb(21, 231, 183);
        _colors[4] = Color.rgb(18, 238, 222);
        _colors[5] = Color.rgb(17, 233, 225);
        _colors[6] = Color.rgb(21, 185, 179);
        _colors[7] = Color.rgb(23, 155, 150);
        _colors[8] = Color.rgb(25, 133, 129);
        _colors[9] = Color.rgb(28, 104, 101);
        _colors[10] = Color.rgb(29, 81, 79);
        _colors[11] = Color.rgb(31, 53, 52);
        _colors[12] = Color.rgb(32, 48, 48);
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

    private void actionDown(MotionEvent event) {
        if (Utils.IsPointInRect(0, 0, _marginLeft, _height, (int) event.getX(), (int) event.getY())) {

        } else {

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    @Override
    public void onSpectrumFinished() {
        // 回调完成之后，调用重绘
        postInvalidate();
    }

    private void showInfo(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
