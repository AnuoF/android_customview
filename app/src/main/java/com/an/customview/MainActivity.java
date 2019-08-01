package com.an.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.an.view.ScaleBar;

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scale_bar:
                startActivity(new Intent(MainActivity.this, ScaleBar.class));
                break;

            default:
                break;
        }
    }
}
