package com.razerdp.amg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.razerdp.amg.annotation.BeforeClose;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BeforeClose
    public void cc(Bundle bundle) {

    }
}
