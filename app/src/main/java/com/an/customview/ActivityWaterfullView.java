package com.an.customview;

import android.os.Bundle;

import com.an.view.WaterfallView;

import java.util.Random;

public class ActivityWaterfullView extends BaseActivity {

    private WaterfallView _waterfallView;
    private final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfull_view);

        initView();
    }

    private void initView() {
        _waterfallView = (WaterfallView) findViewById(R.id.waterfull_view_1);

        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    float[] data = getSpectremData();

                    _waterfallView.setData(101.7, 2000, data);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private float[] getSpectremData() {
        int len = 801;
        float[] data = new float[len];

        for (int i = 0; i < len; i++) {
            data[i] = (rand.nextInt(50 - (-150) + 1) + (-150)) / 10;
        }

        data[399] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
        data[400] = 47 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
        data[401] =  27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

//        data[99] = 27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;
//        data[100] = 47 + (rand.nextInt(10 - (-10) + 1) + (-10)) / 10;
//        data[101] =  27 + (rand.nextInt(25 - (-25) + 1) + (-25)) / 10;

//        for (int i = 0; i < len; i++) {
//            data[i] = rand.nextInt(20 - (-20) + 1) + (-20);
//        }

        return data;
    }
}
