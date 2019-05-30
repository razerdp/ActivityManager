package com.razerdp.amg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.razerdp.amg.annotation.BeforeClose;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button toActivity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        toActivity2 = (Button) findViewById(R.id.to_activity_2);

        toActivity2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_activity_2:
                startActivity(new Intent(this, Main2Activity.class));
                break;
        }
    }

    @BeforeClose
    public void cc(){

    }
}
