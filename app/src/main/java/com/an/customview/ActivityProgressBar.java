package com.an.customview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.an.view.ProgressBar;

public class ActivityProgressBar extends AppCompatActivity {

    private ProgressBar _progressBar1;
    private ProgressBar _progressBar2;
    private ProgressBar _progressBar3;
    private ProgressBar _progressBar4;

    private boolean _runing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

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
