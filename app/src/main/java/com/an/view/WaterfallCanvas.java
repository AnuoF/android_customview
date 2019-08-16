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
    }

    public void setData(OnDrawFinishedListener callback, double frequency, double span, float[] data) {
        _endIndex = data.length;

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

        synchronized (_lockObj) {
            drawWaterfall();
        }

        callback.onSpectrumFinished();
    }

    public void clear() {
        _data.clear();
    }

    /**
     * 画频谱
     */
    private void drawSpectrum() {
        if (_data == null || _data.size() == 0)
            return;

        float perWidth = (_width) / (float) _pointCount;       // 每个方格的 宽
        float perHeight = (_height) / (float) _rainRow;        // 每个方格的 高

        for (int v = 0; v < _data.size(); v++) {
            // 先横后竖
            for (int h = 0; h < _pointCount; h++) {
                int width = (int) (h * perWidth);
                int height = (int) (v * perHeight);
                _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(v)[h]]);
                _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
            }
        }
    }

    /**
     * 绘制瀑布图。为保证效率，不需要每次全部重绘，而是绘制新增的数据即可。
     */
    private void drawWaterfall() {
        if (_data == null || _data.size() == 0) {
            _rainPaint.setColor(_backgroundColor);
            _canvas.drawRect(0, 0, _width, _height, _rainPaint);
            return;
        }

        float perWidth = (_width) / (float) _pointCount;       // 每个方格的 宽
        float perHeight = (_height) / (float) _rainRow;        // 每个方格的 高

        if (_indexChanged) {
            // 如果Index已改变，则需要全部重绘
            for (int v = 0; v < _data.size(); v++) {
                // 先横后竖
                for (int h = _startIndex; h < _endIndex; h++) {
                    int width = (int) (h * perWidth);
                    int height = (int) (v * perHeight);
                    _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(v)[h]]);
                    _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
                }
            }
            _canvas.save();
            _indexChanged = false;
        } else {
            // 如果Index没有改变，则只需要绘制新到的数据即可，现在是到一包画一次，所以不需判断哪些是新的数据
            if (_data.size() <= 1) {
                // 只有 1 包数据，则直接画
                for (int h = _startIndex; h < _endIndex; h++) {
                    int width = (int) (h * perWidth);
                    int height = (int) perHeight;
                    _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(_data.size() - 1)[h]]);   // 只会最后一包
                    _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
                }
            } else {
                if (_waterFallView == null || _waterFallView._bitmap == null)
                    return;

                // 先绘制之前的 Bitmap，然后再画新的数据
//                Bitmap bitmap = _waterFallView._bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap bitmap = Bitmap.createBitmap(_waterFallView._bitmap, 0, (int) perHeight, _width, (int) ((_data.size() - 1) * perHeight));   // perHeight 必须 >= 1，也就是 _rainRow 必须 <= _height
                _canvas.drawBitmap(bitmap, 0, 0, _rainPaint);
                bitmap.recycle();

                for (int h = _startIndex; h < _endIndex; h++) {
                    if (h >= _endIndex)
                        break;

                    int width = (int) (h * perWidth);
                    int height = (int) ((_data.size() - 1) * perHeight);
                    try {
                        _rainPaint.setColor(_colors[_colors.length - 1 - _data.get(_data.size() - 1)[h]]);   // 只画最后一包
                        _canvas.drawRect(width, height, width + perWidth, height + perHeight, _rainPaint);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
