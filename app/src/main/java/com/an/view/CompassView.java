/**
 * @Title: CompassView.java
 * @Package com.an.view
 * @Description: 指南针控件
 * @author AnuoF
 * @date 2019.08.02 08:23
 * @version V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.an.customview.R;

/**
 * 自定义罗盘、指南针控件，用于显示方位角
 */
public class CompassView extends View {

    private float _bearing = 0;   // 显示的方向
    private Paint _markerPaint;
    private Paint _textPaint;
    private Paint _circlePaint;
    private Paint _centerCirclePaint;

    private final String _northString = "N";
    private final String _eastString = "E";
    private final String _southString = "S";
    private final String _westString = "W";

    private float _angle;      // 度，在绘制时需要转成 弧度
    private float _angleOut;

    // 绘图计算用
    private int _textHeight;
    private int _measureWidth;
    private int _messureHeight;
    private int _px;
    private int _py;
    private int _radius;

    public CompassView(Context context) {
        super(context);
        initView();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 指南针是一个尽可能填充更多空间的园，通过设置最短的边界，高度或者宽度来设置测量尺寸
        int measureWidth = measure(widthMeasureSpec);
        int measureHeight = measure(heightMeasureSpec);

        int d = Math.min(measureWidth, measureHeight);
        setMeasuredDimension(d, d);

        _measureWidth = getMeasuredWidth();
        _messureHeight = getMeasuredHeight();

        _px = _messureHeight / 2;
        _py = _messureHeight / 2;
        _radius = Math.min(_px, _py);   // 取最小值为半径
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawScaleText(canvas);
        drawCenterCircleAndDirectionLine(canvas);

        super.onDraw(canvas);
    }

    /**
     * 设置角度，单位度
     *
     * @param angle
     */
    public void setAngle(float angle) {
        _angleOut = angle;
        this._angle = angle * 2 * (float) Math.PI / 360;
        postInvalidate();
    }

    /**
     * 获取当前角度，单位度
     *
     * @return
     */
    public float getAngle() {
        return _angleOut;
    }

    /**
     * 初始控件
     */
    private void initView() {
        _markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _markerPaint.setColor(Color.GREEN);

        _circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _circlePaint.setColor(Color.argb(100, 0, 255, 0));
        _circlePaint.setStrokeWidth(1);
        _circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        _centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _centerCirclePaint.setColor(Color.RED);
        _centerCirclePaint.setStrokeWidth(2);
        _centerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        _textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _textPaint.setColor(Color.GREEN);
        _textHeight = (int) _textPaint.measureText("yY");
    }

    /**
     * 初始化控件
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CompassView);
        if (typedArray != null) {
            _markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            _markerPaint.setColor(typedArray.getColor(R.styleable.CompassView_marker_color, Color.GREEN));

            _circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            _circlePaint.setColor(typedArray.getColor(R.styleable.CompassView_circle_color, Color.argb(100, 0, 255, 0)));
            _circlePaint.setStrokeWidth(1);
            _circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

            _centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            _centerCirclePaint.setColor(typedArray.getColor(R.styleable.CompassView_center_circle_color, Color.RED));
            _centerCirclePaint.setStrokeWidth(2);
            _centerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

            _textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            _textPaint.setColor(typedArray.getColor(R.styleable.CompassView_text_color, Color.GREEN));
            _textHeight = (int) _textPaint.measureText("yY");
        }
    }

    /**
     * 解码数据值
     *
     * @param measureSpec
     * @return
     */
    private int measure(int measureSpec) {
        int result = 0;
        //对测量说明进行解码
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //如果没有指定界限，则返回默认大小200
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            //由于是希望填充可用的空间，所以总是返回整个可用的边界
            result = specSize;
        }
        return result;
    }

    /**
     * 绘制背景园
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        // 绘制背景圆
        canvas.drawCircle(_px, _py, _radius, _circlePaint);
        canvas.save();
        canvas.rotate(_bearing, _px, _py);
    }

    /**
     * 绘制刻度和文本
     *
     * @param canvas
     */
    private void drawScaleText(Canvas canvas) {
        _textPaint.setTextSize(22);
        int textWidth = (int) _textPaint.measureText("W");
        int cadinalX = _px - textWidth / 2;
        int cadinalY = _py - _radius + _textHeight;

        // 每15度绘制一个标记，每45度绘制一个文本
        for (int i = 0; i < 24; i++) {
            // 绘制一个标记
            canvas.drawLine(_px, _px - _radius, _py, _py - _radius + 10, _markerPaint);
            canvas.save();
            canvas.translate(0, _textHeight);

            // 绘制基本方位
            if (i % 6 == 0) {
                String dirString = "";
                switch (i) {
                    case 0:
                        dirString = _northString;
                        int arrowY = 2 * _textHeight;
                        canvas.drawLine(_px, arrowY, _px - 5, 3 * _textHeight, _markerPaint);
                        canvas.drawLine(_px, arrowY, _px + 5, 3 * _textHeight, _markerPaint);
                        canvas.drawLine(_px - 5, 3 * _textHeight, _px + 5, 3 * _textHeight, _markerPaint);
                        break;
                    case 6:
                        dirString = _eastString;
                        break;
                    case 12:
                        dirString = _southString;
                        break;
                    case 18:
                        dirString = _westString;
                        break;
                    default:
                        dirString = _westString;
                        break;
                }
                canvas.drawText(dirString, cadinalX, cadinalY, _textPaint);
            } else if (i % 3 == 0) {
                // 每个45度绘制文本
                String angle = String.valueOf(i * 15);
                float angleTextWidth = _textPaint.measureText(angle);

                int angleTextX = (int) (_px - angleTextWidth / 2);
                int angelTextY = _py - _radius + _textHeight;
                canvas.drawText(angle, angleTextX, angelTextY, _textPaint);
            }
            canvas.restore();
            canvas.rotate(15, _px, _py);
        }
    }

    /**
     * 绘制中心圆和示向线
     *
     * @param canvas
     */
    private void drawCenterCircleAndDirectionLine(Canvas canvas) {
        canvas.drawCircle(_px, _py, _radius / 20, _centerCirclePaint);

        int centerRadius = _radius / 5 * 4;
        double x = _px;
        double y = _py;

        if (_angle == 0) {
            x = _px;
            y = _py - centerRadius;
        } else if (_angle == 90) {
            x = _px + centerRadius;
            y = _py;
        } else if (_angle == 180) {
            x = _px;
            y = _py + centerRadius;
        } else if (_angle == 270) {
            x = _px - centerRadius;
            y = _py;
        } else if (_angle == 360) {
            x = _px;
            y = _py - centerRadius;
        } else if (_angle > 0 && _angle < 90) {
            // 第一象限
            double x1 = Math.sin(_angle) * centerRadius;
            double y1 = Math.cos(_angle) * centerRadius;
            x = _px + x1;
            y = _py - y1;
        } else if (_angle > 90 && _angle < 180) {
            // 第二象限
            float x1 = (float) Math.sin(180 - _angle) * centerRadius;
            float y1 = (float) Math.cos(180 - _angle) * centerRadius;
            x = _px + x1;
            y = _py + y1;
        } else if (_angle > 180 && _angle < 270) {
            // 第三象限
            float x1 = (float) Math.sin(_angle - 180) * centerRadius;
            float y1 = (float) Math.cos(_angle - 180) * centerRadius;
            x = _px - x1;
            y = _py + y1;
        } else if (_angle > 270 && _angle < 360) {
            // 第四象限
            float x1 = (float) Math.sin(360 - _angle) * centerRadius;
            float y1 = (float) Math.cos(360 - _angle) * centerRadius;
            x = _px - x1;
            y = _py - y1;
        }

        _centerCirclePaint.setStrokeWidth(6f);
        canvas.drawLine((float) _px, (float) _py, (float) x, (float) y, _centerCirclePaint);
        canvas.restore();
    }
}
