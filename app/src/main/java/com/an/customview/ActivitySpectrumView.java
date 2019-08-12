package com.an.customview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.an.view.SpectrumView;

import java.util.Random;

public class ActivitySpectrumView extends BaseActivity {

    private SpectrumView _spectrumView1;
    private SpectrumView _spectrumView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectrum_view);

        initView();
    }

    private void initView() {
        _spectrumView1 = (SpectrumView) findViewById(R.id.spectrum_view_1);
        _spectrumView2 = (SpectrumView) findViewById(R.id.spectrum_view_2);

        _runing = true;
        final Random random = new Random();

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    float[] data = new float[400];
                    for (int i = 0; i < data.length; i++) {
                        float level = random.nextInt(46);   // 0 - 45 之间的随机数
                        data[i] = level;
                    }

                    _spectrumView1.setData(101.7, 20000, data);
                    _spectrumView2.setData(101.7, 20000, data);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
