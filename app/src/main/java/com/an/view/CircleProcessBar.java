/**
 * @Title: CircleProcessBar.java
 * @Package: com.an.view
 * @Description: 自定义圆形进度条控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.27 22:36
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

/**
 * 自定义圆形进度条控件
 */
public class CircleProcessBar extends View {


    private int _width;
    private int _height;


    public CircleProcessBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CircleProcessBar(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProcessBar);
        if (typedArray != null) {

        } else {
            initView();
        }
    }

    private void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
