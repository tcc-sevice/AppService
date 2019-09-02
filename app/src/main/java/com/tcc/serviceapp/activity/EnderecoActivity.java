package com.tcc.serviceapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Usuario;

public class EnderecoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
    }

    public Usuario recebeUser(Usuario user){

        return user;
    }
}
