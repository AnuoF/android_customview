/**
 * @Title: LevelStreamView.java
 * @Package: com.an.view
 * @Description: 自定义电平流图形控件
 * @Author: AnuoF
 * @QQ/WeChat: 188512936
 * @Date: 2019.08.09 08:31
 * @Version: V1.0
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
import android.view.View;

import com.an.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义电平流图形控件
 */
public class LevelStreamView extends View {

    private int _gridCount;             // 几等分，默认10
    private int _gridColor;             // 格子颜色
    private String _unitStr;
    private int _unitColor;             // 单位颜色
    private int _fontSize;              // 字体大小
    private int _scrollCount;           // 滚屏点数
    private int _levelColor;            // 电平线条颜色

    private Paint _paint;
    private int _width;
    private int _height;
    private int _maxValue = 80;
    private int _minValue = -20;
    private final int _unitWidth = 30;
    private final int _margin = 10;       // 与各边的距离
    private final int _scaleLen = 15;     // 刻度线长度

    private int _startWidthY;             // 纵轴起始位置
    private int _startWidthX;             // 横轴起始位置
    private int _scaleHeight;             // 总的刻度区域高
    private int _scaleWidth;              // 总的刻度区域宽

    private List<Float> _levels;

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

    public void setLevel(float level) {
        if (_levels.size() > _scrollCount) {
            _levels.remove(0);   // 移除最前一个
        }
        _levels.add(level);
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
        drawLevel(canvas);

        super.onDraw(canvas);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LevelStreamView);
        if (typedArray != null) {
            _gridCount = typedArray.getInt(R.styleable.LevelStreamView_grid_count_lsv, 10);
            _gridColor = typedArray.getColor(R.styleable.LevelStreamView_grid_color_lsv, Color.argb(100, 0, 255, 0));
            _unitStr = typedArray.getString(R.styleable.LevelStreamView_unit_lsv);
            if (_unitStr == null || _unitStr.length() == 0) {
                _unitStr = "电平(dBuV)";
            }
            _unitColor = typedArray.getColor(R.styleable.LevelStreamView_unit_color_lsv, Color.GREEN);
//            _fontSize = typedArray.getInt(R.styleable.LevelStreamView_font_size, 20);
            _fontSize = 20;
            _scrollCount = typedArray.getInt(R.styleable.LevelStreamView_scroll_count_lsv, 101);
            _levelColor = typedArray.getColor(R.styleable.LevelStreamView_level_line_color_lsv, Color.WHITE);
        }

        _levels = new ArrayList<>();
        _paint = new Paint();
        _paint.setTextSize(_fontSize);
    }

    private void initView() {
        _gridCount = 10;
        _gridColor = Color.argb(100, 0, 255, 0);
        _unitColor = Color.GREEN;
        _unitStr = "电平(dBuV)";
        _fontSize = 20;
        _scrollCount = 101;
        _levelColor = Color.WHITE;

        _levels = new ArrayList<>();
        _paint = new Paint();
        _paint.setTextSize(_fontSize);
    }

    /**
     * 绘制单位
     *
     * @param canvas
     */
    private void drawUnit(Canvas canvas) {
        _paint.setColor(_unitColor);
        canvas.rotate(-90);
        canvas.translate(-(_height + 1), 0);
        float textLen = _paint.measureText(_unitStr);
        canvas.drawText(_unitStr, _height / 2 - (int) (textLen / 2), _unitWidth, _paint);

        canvas.save();
        canvas.translate(_height + 1, 0);
        canvas.rotate(90);
    }

    /**
     * 绘制坐标轴和刻度
     *
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        _paint.setColor(_gridColor);
        Rect textRect = new Rect();
        _paint.getTextBounds("" + _maxValue, 0, ("" + _maxValue).length(), textRect);

        int maxValueLen = (int) _paint.measureText("" + _maxValue);   // 刻度最大值的文本长度

        _startWidthY = _unitWidth + _margin;                        // 纵轴起始位置
        _startWidthX = _startWidthY + maxValueLen + _scaleLen;      // 横轴起始位置
        _scaleHeight = _height - 2 * _margin - textRect.height();   // 总的刻度区域高
        _scaleWidth = _width - _startWidthX - _margin;              // 总的刻度区域宽

        for (int i = 0; i <= _gridCount; i++) {
            // 纵轴
            int height = _margin + i * (int) (_scaleHeight / _gridCount);      // 横轴的起始 Y
            int width = _startWidthX + i * _scaleWidth / _gridCount;           // 纵轴的起始 X

            if ((i + 1) % 2 != 0) {
                canvas.drawLine(_startWidthY + maxValueLen, height, _width - _margin, height, _paint);

                if (i == 0) {
                    height = _margin + 20;
                    canvas.drawText("0.00s", width, _height - _margin, _paint);
                } else if (i == _gridCount) {
                    canvas.drawText("0.00s", _width - _paint.measureText("0.00s"), _height - _margin, _paint);
                } else {
                    height = height + textRect.height() / 2;
                }

                String scaleText = "" + (_maxValue - i * (int) ((_maxValue - _minValue) / _gridCount));
                float scaleTextLen = _paint.measureText(scaleText);
                canvas.drawText(scaleText, _startWidthY - scaleTextLen / 2 + _margin, height, _paint);
            } else {
                canvas.drawLine(_startWidthY + maxValueLen + _scaleLen, height, _width - _margin, height, _paint);
            }

            // 纵轴
            canvas.drawLine(width, _margin, width, _scaleHeight + _margin, _paint);
        }
    }

    /**
     * 绘制电平流线
     *
     * @param canvas
     */
    private void drawLevel(Canvas canvas) {
        if (_levels.size() == 0)
            return;

        float perHeight = _scaleHeight / (float) (_maxValue - _minValue);
        float perWidth = _scaleWidth / (float) _scrollCount;

        _paint.setColor(_levelColor);
        _paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();

        for (int i = 0; i < _levels.size(); i++) {
            float level = _levels.get(i);
            int x = _startWidthX + (int) (i * perWidth);
            int y = _startWidthY + (int) (Math.abs(level - _maxValue) * perHeight);

            if (i == 0) {
                path.moveTo(x, y);             // 起始点
            } else {
                path.lineTo(x, y);
            }
        }

        canvas.drawPath(path, _paint);
    }
}
