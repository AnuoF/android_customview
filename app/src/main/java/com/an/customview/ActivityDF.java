package com.an.customview;

import android.os.Bundle;

import com.an.view.DF;

import java.util.Random;

public class ActivityDF extends BaseActivity {

    private final Random rand = new Random();
    private DF _df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_df);

        initView();
    }

    private void initView() {
        _runing = true;
        _df = (DF) findViewById(R.id.df_view);

        new Thread() {
            @Override
            public void run() {
                super.run();
                float angle = 0;

                while (_runing) {
                    float quality = 80 + (rand.nextInt(199 - (-200) + 1) + (-200)) / 10;
                    float azimuth = 160 + (rand.nextInt(150 - (-150) + 1) + (-150)) / 10;
                    angle += 2;
                    if (angle >= 360) {
                        angle = 0;
                    }

                    _df.setData(azimuth, quality, angle);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
