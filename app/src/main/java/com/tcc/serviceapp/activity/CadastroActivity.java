package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tcc.serviceapp.R;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
    }

    public void abrirEndereco(View view){
        Intent intent = new Intent(CadastroActivity.this, EnderecoActivity.class);
        startActivity(intent);
    }
}
