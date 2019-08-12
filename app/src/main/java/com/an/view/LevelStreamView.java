/**
 * @Title: LevelStreamViewB.java
 * @Package: com.an.view
 * @Description: 自定义电平流控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date 2019.08.12 09:00
 * @Version V1.0
 */

package com.an.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.an.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义电平流控件
 */
public class LevelStreamView extends View implements View.OnTouchListener {

    private int _marginTop;                  // 上边距
    private int _marginBottom;               // 下边距
    private int _marginLeft;                 // 左边距
    private int _marginRight;                // 右边距
    private int _scaleLineLength;            // 刻度线长度
    private String _unitStr;                 // 纵轴显示单位
    private int _unitSize;                   // 单位字体大小
    private int _unitColor;                  // 单位字体颜色
    private int _gridCount;                  // 格子数，几等分
    private int _gridColor;                  // 格子颜色
    private int _levelColor;                 // 电平先颜色
    private int _levelCount;                 // 显示的电平点数
    private int _maxValue;                   // 刻度显示的最大值
    private int _minValue;                   // 刻度显示的最小值
    private int _scale_size;                 // 刻度字体大小

    private int _width;
    private int _height;
    private Paint _paint;

    private List<Float> _levels;
    private HandleType _handleType;
    private float _firstY;               // 单点触控时的原点
    private int _offsetY = 0;            // 单点触控时的Y轴偏移值
    private int _zoomOffsetY = 0;        // 多点缩放时的Y轴偏移值
    private float _oldDistanceY;         // 多点缩放时的原始Y轴距离


    public LevelStreamView(Context context, AttributeSet attrs, int defStypeAttr) {
        super(context, attrs, defStypeAttr);
        initView(context, attrs);
    }

    public LevelStreamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LevelStreamView(Context context) {
        super(context);
        initView();
    }

    /**
     * 设置电平值
     *
     * @param level
     */
    public void setLevel(float level) {
        if (_levels.size() > _levelCount) {
            _levels.remove(0);   // 移除最前一个
        }
        _levels.add(level);
        postInvalidate();
    }

    /**
     * 缩放图形
     *
     * @param zoomOffset
     */
    public void zoomLevel(int zoomOffset) {
        if ((_maxValue - zoomOffset) - (_minValue + zoomOffset) >= 5) {
            _maxValue -= zoomOffset;
            _minValue += zoomOffset;
            postInvalidate();
        }
    }

    /**
     * 上下拖动图形
     *
     * @param offset
     */
    public void offsetLevel(int offset) {
        _maxValue += offset;
        _minValue += offset;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _width = getMeasuredWidth();
        _height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawUnit(canvas);
        drawAxis(canvas);
        drawLevel(canvas);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 单点触控
                actionDown(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // & MotionEvent.ACTION_MASK   才能得到 ACTION_POINTER_DOWN
                // 多点触控
                actionPointerDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动：缩放或拖动
                actionMove(motionEvent);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 多点触控抬起
                actionPointerUp(motionEvent);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 取消或抬起
                actionCancelUp(motionEvent);
                break;
        }

        return true;
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LevelStreamView);
        if (typedArray != null) {
            _marginTop = typedArray.getInt(R.styleable.LevelStreamView_margin_top_level, 10);
            _marginBottom = typedArray.getInt(R.styleable.LevelStreamView_margin_bottom_level, 10);
            _marginLeft = typedArray.getInt(R.styleable.LevelStreamView_margin_left_level, 50);
            _marginRight = typedArray.getInt(R.styleable.LevelStreamView_margin_right_level, 10);
            _scaleLineLength = typedArray.getInt(R.styleable.LevelStreamView__scale_line_length_level, 5);
            _unitStr = typedArray.getString(R.styleable.LevelStreamView_unit_str_level);
            _unitSize = typedArray.getInt(R.styleable.LevelStreamView_unit_size_level, 20);
            _unitColor = typedArray.getColor(R.styleable.LevelStreamView_unit_color_level, Color.rgb(255, 251, 240));
            _gridCount = typedArray.getInt(R.styleable.LevelStreamView_grid_count_level, 10);
            _gridColor = typedArray.getInt(R.styleable.LevelStreamView_grid_color_level, Color.argb(200, 255, 251, 240));
            _levelColor = typedArray.getColor(R.styleable.LevelStreamView_level_color_level, Color.GREEN);
            _levelCount = typedArray.getInt(R.styleable.LevelStreamView_level_count_level, 201);
            _maxValue = typedArray.getInt(R.styleable.LevelStreamView_max_value_level, 80);
            _minValue = typedArray.getInt(R.styleable.LevelStreamView_min_value_level, -20);
            _scale_size = typedArray.getInt(R.styleable.LevelStreamView_scale_size_level, 20);

            _levels = new ArrayList<>();
            _paint = new Paint();
            this.setOnTouchListener(this);
        } else {
            initView();
        }
    }

    private void initView() {
        _marginTop = 10;
        _marginBottom = 10;
        _marginLeft = 50;
        _marginRight = 10;
        _scaleLineLength = 5;
        _unitStr = null;
        _unitSize = 20;
        _unitColor = Color.rgb(255, 251, 240);
        _gridCount = 10;
        _gridColor = Color.argb(200, 255, 251, 240);
        _levelColor = Color.GREEN;
        _levelCount = 201;
        _maxValue = 80;
        _minValue = -20;
        _scale_size = 20;

        _levels = new ArrayList<>();
        _paint = new Paint();
        this.setOnTouchListener(this);
    }

    private void drawUnit(Canvas canvas) {
        if (_unitStr == null || _unitStr.length() == 0)
            return;

        canvas.rotate(-90);
        canvas.translate(-_height, 0);

        _paint.setColor(_unitColor);
        _paint.setTextSize(_unitSize);
        Rect unitRect = new Rect();
        _paint.getTextBounds(_unitStr, 0, _unitStr.length(), unitRect);
        canvas.drawText(_unitStr, _height / 2 - (unitRect.width() / 2), unitRect.height(), _paint);
        canvas.save();
        canvas.translate(_height, 0);
        canvas.rotate(90);
    }

    private void drawAxis(Canvas canvas) {
        _paint.setColor(_gridColor);
        _paint.setTextSize(_scale_size);

        int scaleHeight = _height - _marginTop - _marginBottom;                    // 绘制去总高度
        int scaleWidth = _width - _marginLeft - _marginRight - _scaleLineLength;   // 绘制去总宽度

        float perScaleHeight = scaleHeight / (float) _gridCount;      // 每一格的高度
        float perScaleWidth = scaleWidth / (float) _gridCount;        // 每一格的宽度
        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;

        Rect scaleRect = new Rect();
        _paint.getTextBounds(maxValue + "", 0, (maxValue + "").length(), scaleRect);

        for (int i = 0; i <= _gridCount; i++) {
            int height = (int) (i * perScaleHeight) + _marginTop;
            int width = _marginLeft + _scaleLineLength + (int) (i * perScaleWidth);
            int textHeight = 0;
            int startWidth = _marginLeft;

            if ((i + 1) % 2 != 0) {
                if (i == 0) {
                    textHeight += scaleRect.height();
                } else if (i == _gridCount) {

                } else {
                    textHeight += scaleRect.height() / 2;
                }

                int scaleValue = maxValue - i * (maxValue - minValue) / _gridCount;
                float scaleTextLen = _paint.measureText(scaleValue + "");
                canvas.drawText(scaleValue + "", startWidth - scaleTextLen, height + textHeight, _paint);
            } else {
                startWidth += _scaleLineLength;

            }

            // 画横轴
            canvas.drawLine(startWidth, height, _width - _marginRight, height, _paint);
            // 画纵轴
            canvas.drawLine(width, _marginTop, width, _height - _marginBottom, _paint);
        }
    }

    private void drawLevel(Canvas canvas) {
        if (_levels.size() == 0)
            return;

        _paint.setColor(_levelColor);
        _paint.setStyle(Paint.Style.STROKE.STROKE);
        Path path = new Path();

        int maxValue = _maxValue + _offsetY - _zoomOffsetY;
        int minValue = _minValue + _offsetY + _zoomOffsetY;
        float perHeight = (_height - _marginTop - _marginBottom) / (float) Math.abs(maxValue - minValue);
        float perWidth = (_width - _marginLeft - _scaleLineLength - _marginRight) / (float) _levelCount;

        for (int i = 0; i < _levels.size(); i++) {
            float level = _levels.get(i);
            int x = _marginLeft + _scaleLineLength + (int) (i * perWidth);
            int y = _marginTop + (int) ((maxValue - level) * perHeight);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        canvas.drawPath(path, _paint);
    }

    // 以下为手势拖动和缩放实现
    private void actionDown(MotionEvent motionEvent) {
        // 判断点是否在纵轴刻度上
        if (Utils.IsPointInRect(0, 0, _marginLeft + _scaleLineLength, _height, (int) motionEvent.getX(), (int) motionEvent.getY())) {
            _handleType = HandleType.DRAG;
            _firstY = motionEvent.getY();
        } else {
            _handleType = HandleType.NONE;
        }
    }

    private void actionPointerDown(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() == 2) {
            _handleType = HandleType.ZOOM;
            float y1 = motionEvent.getY(0);
            float y2 = motionEvent.getY(1);
            _oldDistanceY = Math.abs(y1 - y2);
        }
    }

    private void actionMove(MotionEvent motionEvent) {
        if (_handleType == HandleType.DRAG) {
            float currentY = motionEvent.getY();

            float spanY = currentY - _firstY;
            // 实际在屏幕上滑动的距离，映射到坐标轴Y上的距离
            int spanScale = (int) (spanY / ((_height - _marginTop - _marginBottom) / Math.abs(_maxValue - _minValue)));
            if (spanScale != 0) {
                _offsetY = spanScale;
                postInvalidate();
            }
        } else if (_handleType == HandleType.ZOOM && motionEvent.getPointerCount() == 2) {    //  需要加一个判断不然会报错
            float y1 = motionEvent.getY(0);
            float y2 = motionEvent.getY(1);
            float currentDistanceY = Math.abs(y1 - y2);

            float perScaleHeight = (_height - _marginTop - _marginBottom) / (float) Math.abs(_maxValue - _minValue);

            int spanScale = (int) ((currentDistanceY - _oldDistanceY) / perScaleHeight);
            if (spanScale != 0 && ((_maxValue - spanScale) - (_minValue + spanScale)) >= 5) {    // 防止交叉越界，并且在放大到 总刻度长为 2 时，不能缩小
                _zoomOffsetY = spanScale;
                postInvalidate();
            }
        }
    }

    private void actionPointerUp(MotionEvent motionEvent) {
        if (_handleType == HandleType.ZOOM) {
            _maxValue -= _zoomOffsetY;
            _minValue += _zoomOffsetY;
            _zoomOffsetY = 0;
        }

        _handleType = HandleType.NONE;
    }

    private void actionCancelUp(MotionEvent motionEvent) {
        if (_handleType == HandleType.DRAG) {
            _maxValue += _offsetY;
            _minValue += _offsetY;
            _offsetY = 0;
        }

        _handleType = HandleType.NONE;
    }

    /**
     * 手势处理类型枚举
     */
    private enum HandleType {
        NONE,
        DRAG,
        ZOOM
    }

}
