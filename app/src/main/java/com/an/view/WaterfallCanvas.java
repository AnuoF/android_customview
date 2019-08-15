package com.an.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class WaterfallCanvas {

    private int _rainRow;                 // 雨点图行数，Y轴数据量
    private short _ZAxisMax;              // Z轴最大值
    private short _ZAxisMin;              // Z轴最小智

    private List<byte[]> _data;            // 数据映射到颜色的二维数组
    private double _frequency;
    private double _spectrumSpan;
    private int _pointCount;

    private int[] _colors = new int[13];   // 色带

    private Canvas _canvas;
    private int _width;
    private int _height;
    private Paint _rainPaint;
    private boolean _bRool;

    public WaterfallCanvas(Canvas canvas) {
        _canvas = canvas;
        _width = _canvas.getWidth();
        _height = _canvas.getHeight();

        _rainPaint = new Paint();
        _rainPaint.setStyle(Paint.Style.FILL);
        _data = new ArrayList<>();
        _rainRow = 50;
        _ZAxisMax = 80;
        _ZAxisMin = -20;
        _bRool = true;

        _colors[0] = Color.rgb(32, 206, 38);
        _colors[1] = Color.rgb(29, 213, 79);
        _colors[2] = Color.rgb(24, 225, 145);
        _colors[3] = Color.rgb(21, 231, 183);
        _colors[4] = Color.rgb(18, 238, 222);
        _colors[5] = Color.rgb(17, 233, 225);
        _colors[6] = Color.rgb(21, 185, 179);
        _colors[7] = Color.rgb(23, 155, 150);
        _colors[8] = Color.rgb(25, 133, 129);
        _colors[9] = Color.rgb(28, 104, 101);
        _colors[10] = Color.rgb(29, 81, 79);
        _colors[11] = Color.rgb(31, 53, 52);
        _colors[12] = Color.rgb(32, 48, 48);
    }

    public void setData(OnDrawFinishedListener callback, double frequency, double span, float[] data) {
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

        drawSpectrum();

        callback.onSpectrumFinished();
    }

    public Canvas getCanvas() {
        return _canvas;
    }

    public void clear(){
        _data.clear();
    }

    /**
     * 画频谱
     */
    private void drawSpectrum() {
        if (_data == null || _data.size() == 0)
            return;

        float perWidth = (_width) / (float) _pointCount;      // 每个方格的 宽
        float perHeight = (_height ) / (float) _rainRow;       // 每个方格的 高

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
}
