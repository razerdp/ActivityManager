package com.razerdp.amg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.razerdp.amg.annotation.BeforeClose;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private Button toActivity3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    @BeforeClose
    public void close(Bundle bundle) {
        if (bundle != null) {
            String ddd = bundle.getString("test");
            Toast.makeText(this, ddd, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Main2Activity#BeforeClose", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        toActivity3 = (Button) findViewById(R.id.to_activity_3);

        toActivity3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_activity_3:
                startActivity(new Intent(this, Main3Activity.class));
                break;
        }
    }
}
