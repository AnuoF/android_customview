package com.an.customview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.an.view.SpectrumView;

import java.util.Random;

public class ActivitySpectrumView extends BaseActivity implements View.OnClickListener {

    private SpectrumView _spectrumView1;
    private SpectrumView _spectrumView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectrum_view);

        initView();
    }

    private void initView() {
        _spectrumView1 = (SpectrumView) findViewById(R.id.spectrum_view_1);
        _spectrumView2 = (SpectrumView) findViewById(R.id.spectrum_view_2);

        Button btnZoomInY = (Button) findViewById(R.id.btn_zoom_in_sv);
        Button btnZoomOutY = (Button) findViewById(R.id.btn_zoom_out_sv);
        Button btnOffsetUp = (Button) findViewById(R.id.btn_offset_up_sv);
        Button btnOffsetDown = (Button) findViewById(R.id.btn_offset_down_sv);
        Button btnClear = (Button) findViewById(R.id.btn_clear_sv);
        Button btnAuto = (Button) findViewById(R.id.btn_auto_sv);

        btnZoomInY.setOnClickListener(this);
        btnZoomOutY.setOnClickListener(this);
        btnOffsetUp.setOnClickListener(this);
        btnOffsetDown.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnAuto.setOnClickListener(this);

        _runing = true;
        final Random random = new Random();

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    float[] data = new float[400];
                    for (int i = 0; i < data.length; i++) {
                        float level = random.nextInt(46);   // 0 - 45 之间的随机数
                        data[i] = level;
                    }

                    _spectrumView1.setData(101.7, 2000, data);
                    _spectrumView2.setData(101.7, 2000, data);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_zoom_in_sv:
                _spectrumView1.zoomY(2);
                _spectrumView2.zoomY(2);
                break;
            case R.id.btn_zoom_out_sv:
                _spectrumView1.zoomY(-2);
                _spectrumView2.zoomY(-2);
                break;
            case R.id.btn_offset_up_sv:
                _spectrumView1.offsetY(-2);
                _spectrumView2.offsetY(-2);
                break;
            case R.id.btn_offset_down_sv:
                _spectrumView1.offsetY(2);
                _spectrumView2.offsetY(2);
                break;
            case R.id.btn_clear_sv:
                _spectrumView1.clear();
                _spectrumView2.clear();
                break;
            case R.id.btn_auto_sv:
                _spectrumView1.autoView();
                _spectrumView2.autoView();
                break;

        }
    }
}
