package com.an.customview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.an.view.SpectrumView;

import java.util.Random;

public class ActivitySpectrumView extends BaseActivity implements View.OnClickListener {

    private SpectrumView _spectrumView1;
    private SpectrumView _spectrumView2;
    private final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectrum_view);

        initView();
    }

    private void initView() {
        _spectrumView1 = (SpectrumView) findViewById(R.id.spectrum_view_1);
        _spectrumView2 = (SpectrumView) findViewById(R.id.spectrum_view_2);
        _spectrumView2.setMaxValueLineVisible(true);
        _spectrumView2.setMinValueLineVisible(true);

        Button btnZoomInY = (Button) findViewById(R.id.btn_zoom_in_sv);
        Button btnZoomOutY = (Button) findViewById(R.id.btn_zoom_out_sv);
        Button btnOffsetUp = (Button) findViewById(R.id.btn_offset_up_sv);
        Button btnOffsetDown = (Button) findViewById(R.id.btn_offset_down_sv);
        Button btnClear = (Button) findViewById(R.id.btn_clear_sv);
        Button btnAuto = (Button) findViewById(R.id.btn_auto_sv);
        CheckBox cbMax = (CheckBox) findViewById(R.id.cb_max);
        CheckBox cbMin = (CheckBox) findViewById(R.id.cb_min);

        btnZoomInY.setOnClickListener(this);
        btnZoomOutY.setOnClickListener(this);
        btnOffsetUp.setOnClickListener(this);
        btnOffsetDown.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnAuto.setOnClickListener(this);

        cbMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _spectrumView2.setMaxValueLineVisible(b);
            }
        });
        cbMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _spectrumView2.setMinValueLineVisible(b);
            }
        });

        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    float[] data = getSpectremData();

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

    private float[] getSpectremData() {
        float[] data = new float[801];

        for (int i = 0; i < 801; i++) {
            data[i] = (rand.nextInt(50 - (-150) + 1) + (-150)) / 10;
        }

        data[399] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
        data[400] = 47 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
        data[401] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

        return data;
    }
}
