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

            default:
                break;
        }
    }
}
