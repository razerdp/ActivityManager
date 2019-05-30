package com.razerdp.amg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.razerdp.amg.annotation.BeforeClose;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {

    private Button closeActivity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
    }


    @BeforeClose
    public void beforeClose() {
        Toast.makeText(this, "Main3Activity#BeforeClose", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        closeActivity2 = (Button) findViewById(R.id.close_activity_2);

        closeActivity2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_activity_2:
                Bundle bundle = new Bundle();
                bundle.putString("test", "test");
                Amg.getInstance().multiFinish()
                        .append(Main2Activity.class, bundle)
                        .finish();
                break;
        }
    }
}
