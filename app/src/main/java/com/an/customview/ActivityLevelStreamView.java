package com.an.customview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.an.view.LevelStreamView;

import java.util.Random;

public class ActivityLevelStreamView extends BaseActivity implements View.OnClickListener {

    private LevelStreamView levelStreamView1;
    private LevelStreamView levelStreamView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_stream_view);

        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_zoom_in:
                levelStreamView1.zoomLevel(2);
                levelStreamView3.zoomLevel(2);
                break;
            case R.id.btn_zoom_out:
                levelStreamView1.zoomLevel(-2);
                levelStreamView3.zoomLevel(-2);
                break;
            case R.id.btn_offset_up:
                levelStreamView1.offsetLevel(-2);
                levelStreamView3.offsetLevel(-2);
                break;
            case R.id.btn_offset_down:
                levelStreamView1.offsetLevel(2);
                levelStreamView3.offsetLevel(2);
                break;
            case R.id.btn_auto:
                levelStreamView3.autoView();
                levelStreamView1.autoView();
                break;
            case R.id.btn_clear:
                levelStreamView1.clear();
                levelStreamView3.clear();
                break;
        }
    }

    private void initView() {
        levelStreamView1 = (LevelStreamView) findViewById(R.id.level_stream_view1);
        levelStreamView3 = (LevelStreamView) findViewById(R.id.level_stream_view3);
        _runing = true;

        Button btnZoomIn = (Button) findViewById(R.id.btn_zoom_in);
        Button btnZoomOut = (Button) findViewById(R.id.btn_zoom_out);
        Button btnOffsetUp = (Button) findViewById(R.id.btn_offset_up);
        Button btnOffsetDown = (Button) findViewById(R.id.btn_offset_down);
        Button btnClear = (Button) findViewById(R.id.btn_clear);
        Button btnAuto = (Button) findViewById(R.id.btn_auto);
        btnZoomIn.setOnClickListener(this);
        btnZoomOut.setOnClickListener(this);
        btnOffsetUp.setOnClickListener(this);
        btnOffsetDown.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnAuto.setOnClickListener(this);

        new Thread() {
            @Override
            public void run() {
                super.run();

                Random random = new Random();

                while (_runing) {
                    float level = random.nextInt(50);
                    levelStreamView1.setLevel(level);
                    levelStreamView3.setLevel(level);

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
