/**
 * @Title: SpectrumView.java
 * @Package: com.an.view
 * @Description: 自定义频谱图控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.09 20:27
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义频谱图控件
 */
public class SpectrumView extends View {

    private String _unitStr;               // 单位
    private int _unitColor;                // 单位字体颜色
    private int _gridColor;                // 背景网格颜色
    private int _gridCount;                // 网格几等分，默认10
    private int _realTimeLineColor;        // 实时值的颜色
    private int _maxValueLineColor;        // 最大值的颜色
    private int _minValueLineColor;        // 最小值的颜色

    private int _width;                    // 测量的宽度
    private int _height;                   // 测量的高度

    private double _frequency;             // 中心频率
    private double _spectrumSpan;          // 频谱带宽
    private float[] _data;                 // 频谱数据

    private boolean _drawMaxValue;         // 绘制最大值
    private boolean _drawMinValue;         // 绘制最小值


    public SpectrumView(Context context, AttributeSet attrs, int defStypeAttr) {
        super(context, attrs, defStypeAttr);
        initView(context, attrs);
    }

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SpectrumView(Context context) {
        super(context);
        initView();
    }

    /**
     * 设置数据
     *
     * @param frequency    中心频率
     * @param spectrunSpan 频谱带宽
     * @param data         频谱数据
     */
    public void setData(double frequency, double spectrunSpan, float[] data) {
        _frequency = frequency;
        _spectrumSpan = spectrunSpan;
        _data = data;

        postInvalidate();
    }

    /**
     * 设置是否绘制最大值
     *
     * @param visible
     */
    public void setMaxValueLineVisible(boolean visible) {
        this._drawMaxValue = visible;
        postInvalidate();
    }

    /**
     * 设置是否绘制最小值
     *
     * @param visible
     */
    public void setMinValueLineVisible(boolean visible) {
        this._drawMinValue = visible;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth() - 1;
        _height = getMeasuredHeight() - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawUnit(canvas);
        drawAxis(canvas);
        drawSpectrum(canvas);

        super.onDraw(canvas);
    }

    private void initView(Context context, AttributeSet attrs) {

    }

    private void initView() {

    }

    /**
     * 画单位
     *
     * @param canvas
     */
    private void drawUnit(Canvas canvas) {

    }

    /**
     * 画背景刻度
     *
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {

    }

    /**
     * 画频谱
     *
     * @param canvas
     */
    private void drawSpectrum(Canvas canvas) {

    }
}
