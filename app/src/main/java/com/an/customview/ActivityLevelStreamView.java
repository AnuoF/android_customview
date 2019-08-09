package com.an.customview;

import android.os.Bundle;

import com.an.view.LevelStreamView;

import java.util.Random;

public class ActivityLevelStreamView extends BaseActivity {

    private LevelStreamView levelStreamView1;
    private LevelStreamView levelStreamView2;
    private LevelStreamView levelStreamView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_stream_view);

        initView();
    }

    private void initView() {
        levelStreamView1 = (LevelStreamView) findViewById(R.id.level_stream_view1);
        levelStreamView2 = (LevelStreamView) findViewById(R.id.level_stream_view2);
        levelStreamView3 = (LevelStreamView) findViewById(R.id.level_stream_view3);
        _runing = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                Random random = new Random();

                while (_runing) {
                    float level = random.nextInt(70);
                    levelStreamView1.setLevel(level);
                    levelStreamView2.setLevel(level);
                    levelStreamView3.setLevel(level);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
