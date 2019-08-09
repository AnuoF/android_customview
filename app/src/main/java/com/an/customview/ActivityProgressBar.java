package com.an.customview;

import android.os.Bundle;

import com.an.view.ProgressBar;

public class ActivityProgressBar extends BaseActivity {

    private ProgressBar _progressBar1;
    private ProgressBar _progressBar2;
    private ProgressBar _progressBar3;
    private ProgressBar _progressBar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        initView();
    }

    private void initView() {
        _progressBar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        _progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);
        _progressBar3 = (ProgressBar) findViewById(R.id.progress_bar3);
        _progressBar4 = (ProgressBar) findViewById(R.id.progress_bar4);

        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    for (int i = 0; i <= 100; i++) {
                        _progressBar1.setValue(i);
                        _progressBar2.setValue(i);
                        _progressBar3.setValue(i);
                        _progressBar4.setValue(i);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }
}
