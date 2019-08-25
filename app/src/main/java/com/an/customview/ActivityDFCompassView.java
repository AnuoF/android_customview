package com.an.customview;

import android.os.Bundle;

import com.an.view.DFCompassView;

import java.util.Random;

public class ActivityDFCompassView extends BaseActivity {

    private final Random rand = new Random();
    private DFCompassView _dfCompassView1;
    private DFCompassView _dfCompassView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_df);

        initView();
    }

    private void initView() {
        _runing = true;
        _dfCompassView1 = (DFCompassView) findViewById(R.id.df_view1);
        _dfCompassView2 = (DFCompassView) findViewById(R.id.df_view2);
        _dfCompassView2.setNorthMode(DFCompassView.NorthMode.CAR_HEAD);
        _dfCompassView2.setViewMode(DFCompassView.ViewMode.COMPASS);

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

                    _dfCompassView1.setData(azimuth, quality, angle);
                    _dfCompassView2.setData(azimuth, quality, angle);

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
