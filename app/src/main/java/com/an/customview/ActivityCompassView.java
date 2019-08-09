package com.an.customview;

import android.os.Bundle;

import com.an.view.CompassView;

import java.util.Random;

public class ActivityCompassView extends BaseActivity {

    private CompassView _compassView1;
    private CompassView _compassView2;
    private CompassView _compassView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_view);

        initView();
    }

    private void initView() {
        _compassView1 = (CompassView) findViewById(R.id.compass_view1);
        _compassView2 = (CompassView) findViewById(R.id.compass_view2);
        _compassView3 = (CompassView) findViewById(R.id.compass_view3);

        _runing = true;
        final Random random = new Random();

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    int value = random.nextInt(360);
                    _compassView1.setAngle(value);
                    _compassView2.setAngle(value);
                    _compassView3.setAngle(value);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
