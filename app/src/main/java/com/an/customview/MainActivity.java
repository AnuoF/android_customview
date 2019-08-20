package com.an.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        Button btnScaleBar = (Button) findViewById(R.id.btn_scale_bar);
        btnScaleBar.setOnClickListener(this);
        Button btnCompassView = (Button) findViewById(R.id.btn_compass_view);
        btnCompassView.setOnClickListener(this);
        Button btnProgressBar = (Button) findViewById(R.id.btn_progress_bar);
        btnProgressBar.setOnClickListener(this);
        Button btnLevelStreamView = (Button) findViewById(R.id.btn_level_stream_view);
        btnLevelStreamView.setOnClickListener(this);
        Button btnSpectrumView = (Button) findViewById(R.id.btn_spectrum_view);
        btnSpectrumView.setOnClickListener(this);
        Button btnWaterfallView = (Button) findViewById(R.id.btn_waterfull_view);
        btnWaterfallView.setOnClickListener(this);
        Button btnBothView = (Button) findViewById(R.id.btn_both_view);
        btnBothView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scale_bar:
                startActivity(new Intent(this, ActivityScaleBar.class));
                break;
            case R.id.btn_compass_view:
                startActivity(new Intent(this, ActivityCompassView.class));
                break;
            case R.id.btn_progress_bar:
                startActivity(new Intent(this, ActivityProgressBar.class));
                break;
            case R.id.btn_level_stream_view:
                startActivity(new Intent(this, ActivityLevelStreamView.class));
                break;
            case R.id.btn_spectrum_view:
                startActivity(new Intent(this, ActivitySpectrumView.class));
                break;
            case R.id.btn_waterfull_view:
                startActivity(new Intent(this, ActivityWaterfullView.class));
                break;
            case R.id.btn_both_view:
                startActivity(new Intent(this, ActivityGeneralSpectrumChart.class));
                break;

            default:
                break;
        }
    }
}
