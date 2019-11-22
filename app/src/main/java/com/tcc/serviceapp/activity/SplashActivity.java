package com.tcc.serviceapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tcc.serviceapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Esconde a barra superior
        getSupportActionBar().hide();

        // Método que espera por 'x' milisegundos para executar o código
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirTelaPrincipal();
            }
        }, 3000);
    }

    // Abre a tela principal e finaliza a SplashActivity
    private void abrirTelaPrincipal(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
