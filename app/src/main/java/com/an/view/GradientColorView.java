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

class GradientColorView extends View {

    public int _index;
    private int[] _colors;

    private int _width;
    private int _height;
    private Paint _paint;

    public GradientColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public GradientColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GradientColorView(Context context) {
        super(context);
        initView();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.GradientColorView);
        if (typedArray != null) {
            _paint = new Paint();
            _index = typedArray.getInt(R.styleable.GradientColorView_gradientColors, 0);
            initColors();
        } else {
            _paint = new Paint();
            _index = 0;
            initColors();
        }
    }

    private void initView() {
        _paint = new Paint();
        _index = 0;
        initColors();
    }

    private void initColors() {
        if (_index < 0) {
            _index = 0;
        } else if (_index > 3) {
            _index = 3;
        }

        switch (_index) {
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
                        Color.rgb(38, 38, 37)};
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGradientColor(canvas);
        super.onDraw(canvas);
    }

    private void drawGradientColor(Canvas canvas) {
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, _height, _colors, null, Shader.TileMode.CLAMP);
        _paint.setShader(linearGradient);
        canvas.drawRect(0, 0, _width, _height, _paint);
    }

}
