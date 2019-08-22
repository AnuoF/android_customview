package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

public class DF extends View {
    private int _neswColor;                       // NESW颜色
    private int _neswSize;                        // NESW字体大小
    private ViewMode _viewMode;                   // 视图模式
    private int crossLineColor;                   // 十字交叉线条颜色



    private int _width;
    private int _height;

    private Bitmap _bitmap;
    private Paint _paint;


    public DF(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public DF(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DF);
        if (typedArray != null) {

            _paint = new Paint();

        } else {
            initView();
        }
    }

    private void initView() {


        _paint = new Paint();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (_bitmap != null) {
            canvas.drawBitmap(_bitmap, 0, 0, _paint);
        }

        super.onDraw(canvas);
    }


    /**
     * 视图模式
     */
    private enum ViewMode {
        DIRECTION,  // 方位视图
        CLOCK       // 钟表视图
    }
}
