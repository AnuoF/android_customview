/**
 * @Title: WaterfullView.java
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

/**
 * 自定义频谱瀑布图控件（语谱图）
 */
public class WaterfullView extends View {

    private int _marginTop;               // 上边距
    private int _marginBottom;            // 下边距
    private int _marginLeft;              // 左边距
    private int _marginRight;             // 右边距

    private float[] _data;
    private double _frequency;
    private double _spectrumSpan;

    private int _width;
    private int _height;
    private Paint _paint;


    public WaterfullView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public WaterfullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WaterfullView(Context context) {
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
            if (_data != null && _data.length > 0) {
                drawSpectrum(canvas);
                drawSelectRect(canvas);
            }
            super.onDraw(canvas);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterfullView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.WaterfullView_marginTop_Wv, 0);
            _marginBottom = typedArray.getInt(R.styleable.WaterfullView_marginBottom_Wv, 0);
            _marginLeft = typedArray.getInt(R.styleable.WaterfullView_marginLeft_Wv, 60);
            _marginRight = typedArray.getInt(R.styleable.WaterfullView_marginRight_Wv, 10);

            _paint = new Paint();
            _data = new float[0];

        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 0;
        _marginBottom = 0;
        _marginLeft = 60;
        _marginRight = 10;    // 这里的取值主要是对应频谱图

        _paint = new Paint();
        _data = new float[0];

    }

    /**
     * 画色带
     *
     * @param canvas
     */
    private void drawGradientRect(Canvas canvas) {

        //    4A0000,750000,9F0000,C60000,FF0000,FE6D16,FFFF00,1E90FF,000091,000050,000030,000020,000000
        int[] colors = new int[13];
        //colors[0] = Color.parseColor("4A0000");
        colors[1] = Color.parseColor("750000");
        colors[2] = Color.parseColor("9F0000");
        colors[3] = Color.parseColor("C60000");
        colors[4] = Color.parseColor("FF0000");
        colors[5] = Color.parseColor("FE6D16");
        colors[6] = Color.parseColor("FFFF00");
        colors[7] = Color.parseColor("1E90FF");
        colors[8] = Color.parseColor("000091");
        colors[9] = Color.parseColor("000050");
        colors[10] = Color.parseColor("000030");
        colors[11] = Color.parseColor("000020");
        colors[12] = Color.parseColor("000000");

        LinearGradient linearGradient = new LinearGradient(0, 0, _marginLeft, _height, new int[]{}, null, Shader.TileMode.CLAMP);
        _paint.setShader(linearGradient);
        canvas.drawRect(0, 0, _marginLeft, _height, _paint);

    }

    /**
     * 画频谱
     *
     * @param canvas
     */
    private void drawSpectrum(Canvas canvas) {


    }

    /**
     * 画选择区域
     *
     * @param canvas
     */
    private void drawSelectRect(Canvas canvas) {

    }

}
