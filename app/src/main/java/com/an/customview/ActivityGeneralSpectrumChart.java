package com.an.customview;

import android.os.Bundle;

import com.an.view.GeneralSpectrumView;

import java.util.Random;

public class ActivityGeneralSpectrumChart extends BaseActivity {

    private GeneralSpectrumView _spectrumWaterfall_1;
    private final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectrum_waterfall);

        initView();
    }

    private void initView() {
        _spectrumWaterfall_1 = (GeneralSpectrumView) findViewById(R.id.spectrum_waterfall_view);
        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    _spectrumWaterfall_1.setData(101.7, 20000, getSpectremData());

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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
