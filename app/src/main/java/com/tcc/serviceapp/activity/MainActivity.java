package com.tcc.serviceapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tcc.serviceapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
    }
}
