package com.an.customview;

import android.os.Bundle;

import com.an.view.CircleProcessBar;

public class ActivityCircleProcessBar extends BaseActivity {

    private CircleProcessBar cpb1;
    private CircleProcessBar cpb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_process_bar);

        initView();
    }

    private void initView() {
        cpb1 = (CircleProcessBar) findViewById(R.id.cpb_1);
        cpb2 = (CircleProcessBar) findViewById(R.id.cpb_2);

        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();
                int value = 0;

                while (_runing) {
                    cpb1.setValue(value);
                    cpb2.setValue(value);

                    value++;

                    if (value > 100) {
                        value = 0;
                    }

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
