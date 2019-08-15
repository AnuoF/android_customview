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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义频谱瀑布图控件（语谱图）
 */
public class WaterfallView extends View {

    private int _marginTop;               // 上边距
    private int _marginBottom;            // 下边距
    private int _marginLeft;              // 左边距
    private int _marginRight;             // 右边距

    private int _rainRow;                 // 雨点图行数，Y轴数据量
    private short _ZAxisMax;              // Z轴最大值
    private short _ZAxisMin;              // Z轴最小智


    private List<byte[]> _data;            // 数据映射到颜色的二维数组
    private double _frequency;
    private double _spectrumSpan;
    private int _pointCount;

    private int[] _colors = new int[13];   // 色带

    private int _width;
    private int _height;
    private Paint _paint;
    private Paint _rainPaint;
    private boolean _bRool;                 // 是否滚动


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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this._width = getMeasuredWidth();
        this._height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Utils.checkValid()) {
            drawGradientRect(canvas);
            if (_data != null && _data.size() > 0) {
                drawSpectrum(canvas);
                drawSelectRect(canvas);
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
    public void setData(double frequency, double span, float[] data) {
        if (_data.size() == 0) {
            _frequency = frequency;
            _spectrumSpan = span;
            _pointCount = data.length;
        } else if (frequency != _frequency || span != _spectrumSpan || data.length != _pointCount) {
            clear();
            _frequency = frequency;
            _spectrumSpan = span;
            _pointCount = data.length;
        }

        byte[] colors = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
//            int index = (int) (data[i] * _colors.length / 255);
            byte index;
            float value = data[i];
            if (value <= _ZAxisMin) {
                index = (byte) (_bRool ? 1 : 0);
            } else if (value >= _ZAxisMax) {
                index = (byte) (_colors.length - 1);
            } else {
                index = (byte) ((value - _ZAxisMin) / (_ZAxisMax - _ZAxisMin) * (_colors.length - 1));
            }

            colors[i] = index;
        }

        if (_data.size() >= _rainRow) {
            _data.remove(0);
        }
        _data.add(colors);

        postInvalidate();
    }

    /**
     * 清除数据和图形
     */
    public void clear() {
        _data.clear();
        postInvalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterfallView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.WaterfallView_marginTop_Wv, 0);
            _marginBottom = typedArray.getInt(R.styleable.WaterfallView_marginBottom_Wv, 0);
            _marginLeft = typedArray.getInt(R.styleable.WaterfallView_marginLeft_Wv, 60);
            _marginRight = typedArray.getInt(R.styleable.WaterfallView_marginRight_Wv, 10);
            _rainRow = typedArray.getInt(R.styleable.WaterfallView_rainRow_Wv, 50);
//            _ZAxisMax = (short) (typedArray.getInt(R.styleable.WaterfallView_zAxisMax_Wv, 80) * 100);
//            _ZAxisMin = (short) (typedArray.getInt(R.styleable.WaterfallView_zAxizMin_Wv, -20) * 100);
            _ZAxisMax = (short) typedArray.getInt(R.styleable.WaterfallView_zAxisMax_Wv, 80);
            _ZAxisMin = (short) typedArray.getInt(R.styleable.WaterfallView_zAxizMin_Wv, -20);

            _paint = new Paint();
            _rainPaint = new Paint();
            _data = new ArrayList<>();
            initColorGradient();
            _bRool = true;

        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 0;
        _marginBottom = 0;
        _marginLeft = 60;
        _marginRight = 10;    // 这里的取值主要是对应频谱图
        _rainRow = 50;
        _ZAxisMax = 80;
        _ZAxisMin = -20;

        _paint = new Paint();
        _rainPaint = new Paint();
        _rainPaint.setStyle(Paint.Style.FILL);
        _data = new ArrayList<>();
        initColorGradient();
        _bRool = true;

    }

    /**
     * 初始化色带
     */
    private void initColorGradient() {
        //    4A0000,750000,9F0000,C60000,FF0000,FE6D16,FFFF00,1E90FF,000091,000050,000030,000020,000000
//        _colors[0] = Color.rgb(74, 0, 0);
//        _colors[1] = Color.rgb(117, 0, 0);
//        _colors[2] = Color.rgb(159, 0, 0);
//        _colors[3] = Color.rgb(198, 0, 0);
//        _colors[4] = Color.rgb(255, 0, 0);
//        _colors[5] = Color.rgb(254, 109, 22);
//        _colors[6] = Color.rgb(255, 255, 0);
//        _colors[7] = Color.rgb(30, 144, 255);
//        _colors[8] = Color.rgb(0, 0, 145);
//        _colors[9] = Color.rgb(0, 0, 80);
//        _colors[10] = Color.rgb(0, 0, 48);
//        _colors[11] = Color.rgb(0, 0, 32);
//        _colors[12] = Color.rgb(0, 0, 0);

        // G
//    <color name="colorAccent">#20CE26</color>   32,206,38
//    <color name="colorAccent">#1DD54F</color>   29,213,79
//    <color name="colorAccent">#18E191</color>   24,225,145
//    <color name="colorAccent">#15E7B7</color>   21,231,183
//    <color name="colorAccent">#12EEDE</color>    18,238,222
//    <color name="colorAccent">#11E9E1</color>    17,233,225
//    <color name="colorAccent">#15B9B3</color>    21,185,179
//    <color name="colorAccent">#179B96</color>    23,155,150
//    <color name="colorAccent">#198581</color>    25,133,129
//    <color name="colorAccent">#1C6865</color>    28,104,101
//    <color name="colorAccent">#1D514F</color>    29,81,79
//    <color name="colorAccent">#1F3534</color>    31,53,52
//    <color name="colorAccent">#203030</color>    32,48,48

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

    /**
     * 画频谱
     *
     * @param canvas
     */
    private void drawSpectrum(Canvas canvas) {
        if (_data == null || _data.size() == 0)
            return;

        float perWidth = (_width - _marginLeft - _marginRight) / (float) _pointCount;      // 每个方格的 宽
        float perHeight = (_height - _marginTop - _marginBottom) / (float) _rainRow;       // 每个方格的 高

        for (int v = 0; v < _data.size(); v++) {
            // 先横后竖
            for (int h = 0; h < _pointCount; h++) {
                int width = _marginLeft + (int) (h * perWidth);
                int height = _marginTop + (int) (v * perHeight);
                try {
                    _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(v)[h]]);
                    canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 画选择区域
     *
     * @param canvas
     */
    private void drawSelectRect(Canvas canvas) {

    }

}
