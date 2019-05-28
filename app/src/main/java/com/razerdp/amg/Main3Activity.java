package com.razerdp.amg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.razerdp.amg.annotation.BeforeClose;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    @BeforeClose
    public void dd(){

    }
}
