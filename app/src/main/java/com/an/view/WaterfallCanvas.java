package com.an.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

class WaterfallCanvas {

    public int _rainRow;                   // 雨点图行数，Y轴数据量
    public short _ZAxisMax;                // Z轴最大值
    public short _ZAxisMin;                // Z轴最小智
    public int _backgroundColor;           // 背景颜色
    public int[] _colors;                  // 色带
    // 以上变量由外部赋值

    private List<byte[]> _data;            // 数据映射到颜色的二维数组
    private double _frequency;
    private double _spectrumSpan;
    private int _pointCount;

    private Canvas _canvas;
    private WaterfallView _waterFallView;
    private int _width;
    private int _height;
    private Paint _rainPaint;

    private boolean _indexChanged;
    private int _startIndex;
    private int _endIndex;

    private boolean _bRool;
    private final Object _lockObj = new Object();
    private OnDrawFinishedListener _callback;


    public WaterfallCanvas(Canvas canvas, WaterfallView waterfallView) {
        _canvas = canvas;
        _waterFallView = waterfallView;
        _width = _canvas.getWidth();
        _height = _canvas.getHeight();

        _rainPaint = new Paint();
        _rainPaint.setStyle(Paint.Style.FILL);
        _data = new ArrayList<>();
        _rainRow = 500;
        _ZAxisMax = 80;
        _ZAxisMin = -20;
        _bRool = true;

        _backgroundColor = Color.BLACK;
        _startIndex = 0;
        _indexChanged = false;
        _callback = (OnDrawFinishedListener) waterfallView;
    }

    public void setData(double frequency, double span, float[] data) {
        synchronized (_lockObj) {
            if (_endIndex == 0) {
                _startIndex = 0;
                _endIndex = data.length;
            }

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

            drawWaterfall();
        }

        _callback.onDrawFinished();
    }

    public void zoneRange(int startIndex, int endIndex) {
        if (_startIndex == startIndex && _endIndex == endIndex)
            return;

        synchronized (_lockObj) {
            _startIndex = startIndex;
            _endIndex = endIndex;
            _indexChanged = true;

            drawWaterfall();
        }

        _callback.onDrawFinished();
    }

    public void clear() {
        synchronized (_lockObj) {
            _startIndex = 0;
            _endIndex = 0;
            _data.clear();

            drawWaterfall();
        }

        _callback.onDrawFinished();
    }

    /**
     * 绘制瀑布图。为保证效率，不需要每次全部重绘，而是绘制新增的数据即可。
     */
    private void drawWaterfall() {
        if (_data == null || _data.size() == 0)
            return;
        if (_waterFallView == null || _waterFallView._bitmap == null)
            return;

        float perWidth = (_width) / (float) (_endIndex - _startIndex);       // 每个方格的 宽
        float perHeight = (_height) / (float) _rainRow;        // 每个方格的 高

        if (_data.size() == 1) {
            for (int h = _startIndex; h < _endIndex; h++) {
                int width = (int) ((h - _startIndex) * perWidth);
                int height = 0;
                _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(0)[h]]);
                _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
            }
        }else {
            // 先绘制之前的 Bitmap，然后再画新的数据
            Bitmap bitmap = Bitmap.createBitmap(_waterFallView._bitmap, 0, (int) perHeight, _width, (int) ((_data.size() - 1) * perHeight));   // perHeight 必须 >= 1，也就是 _rainRow 必须 <= _height
            _canvas.drawBitmap(bitmap, 0, 0, _rainPaint);
            bitmap.recycle();

            for (int h = _startIndex; h < _endIndex; h++) {
                if (h >= _endIndex)
                    break;

                int width = (int) ((h - _startIndex) * perWidth);
                int height = (int) ((_data.size() - 1) * perHeight);
                _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(_data.size() - 1)[h]]);   // 只画最后一包
                _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
            }
        }
    }
}
