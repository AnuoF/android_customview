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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

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

    private int _textHeight;
    private float _angle ;      // 度，在绘制时需要转成 弧度

    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

}
