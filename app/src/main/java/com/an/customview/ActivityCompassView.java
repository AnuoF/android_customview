package com.an.customview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.an.view.CompassView;

import java.util.Random;

public class ActivityCompassView extends AppCompatActivity {

    private CompassView _compassView1;
    private CompassView _compassView2;
    private CompassView _compassView3;

    private boolean _runing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _runing = false;
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
