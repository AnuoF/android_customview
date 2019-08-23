package com.an.customview;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.an.view.GeneralSpectrumView;
import com.an.view.ShowMode;

import java.util.Random;

public class ActivityGeneralSpectrumView extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private GeneralSpectrumView _spectrumWaterfall_1;
    private final Random rand = new Random();
    private RadioGroup _radioGroup;
    private CheckBox _chMax;
    private CheckBox _cbMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_spectrum_view);

        initView();
    }

    private void initView() {
        _spectrumWaterfall_1 = (GeneralSpectrumView) findViewById(R.id.spectrum_waterfall_view);
        _radioGroup = (RadioGroup) findViewById(R.id.rg_mode);
        _radioGroup.setOnCheckedChangeListener(this);
        _chMax = (CheckBox) findViewById(R.id.cb_max_line);
        _cbMin = (CheckBox) findViewById(R.id.cb_min_line);
        _chMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _spectrumWaterfall_1.setMaxValueVisible(b);
            }
        });
        _cbMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _spectrumWaterfall_1.setMinValueVisible(b);
            }
        });

        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    _spectrumWaterfall_1.setData(101.7, 20000, getSpectrumData());

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private float[] getSpectrumData() {
        float[] data = new float[801];

        for (int i = 0; i < 801; i++) {
            data[i] = (rand.nextInt(50 - (-150) + 1) + (-150)) / 10;
        }

        data[199] = 17 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
        data[200] = 27 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
        data[201] = 17 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

        data[399] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
        data[400] = 47 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
        data[401] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

        data[599] = 17 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
        data[600] = 27 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
        data[601] = 17 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

        return data;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        ShowMode mode = ShowMode.Both;
        switch (i) {
            case R.id.rbt_both:
                mode = ShowMode.Both;
                break;
            case R.id.rbt_spectrum:
                mode = ShowMode.Spectrum;
                break;
            case R.id.rbt_waterfall:
                mode = ShowMode.Waterfall;
                break;
        }

        _spectrumWaterfall_1.setShowMode(mode);
    }

}
