package com.an.customview;

import android.os.Bundle;

import com.an.view.ScaleBar;

import java.util.Random;

public class ActivityScaleBar extends BaseActivity {

    private ScaleBar _scaleBar1;
    private ScaleBar _scaleBar2;
    private ScaleBar _scaleBar3;
    private ScaleBar _scaleBar4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_bar);

        // 启动线程，设置 Value
        initView();
    }

    private void initView() {
        _scaleBar1 = (ScaleBar) findViewById(R.id.scale_bar_1);
        _scaleBar2 = (ScaleBar) findViewById(R.id.scale_bar_2);
        _scaleBar3 = (ScaleBar) findViewById(R.id.scale_bar_3);
        _scaleBar4 = (ScaleBar) findViewById(R.id.scale_bar_4);

        _runing = true;
        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    Random rand = new Random();
                    int value = rand.nextInt(100);
                    _scaleBar1.setValue(value);
                    _scaleBar2.setValue(value);
                    _scaleBar3.setValue(value);
                    _scaleBar4.setValue(value);

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
