package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tcc.serviceapp.R;

public class LoginActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void abrirCadastro(View view){
        Intent intent = new Intent(LoginActiviy.this, CadastroActivity.class);
        startActivity(intent);
    }
}
