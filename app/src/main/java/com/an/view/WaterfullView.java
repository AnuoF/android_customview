package com.an.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 频谱瀑布图（语谱图）
 */
public class WaterfullView extends View {

    private int _width;
    private int _height;


    public WaterfullView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WaterfullView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterfullView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this._width = getMeasuredWidth();
        this._height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {


        super.onDraw(canvas);
    }
}
