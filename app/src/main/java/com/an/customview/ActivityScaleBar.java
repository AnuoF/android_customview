package com.an.customview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.an.view.ScaleBar;

import java.util.Random;

public class ActivityScaleBar extends AppCompatActivity {

    private ScaleBar _scaleBar;
    private boolean _runing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 启动线程，设置 Value
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _runing = false;
    }

    private void initView() {
        _scaleBar = (ScaleBar) findViewById(R.id.scale_bar);
        _runing = true;
        new Thread() {
            @Override
            public void run() {
                super.run();

                while (_runing) {
                    Random rand = new Random();
                    int value = rand.nextInt(100);
                    _scaleBar.setValue(value);
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
