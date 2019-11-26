package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuração inicial do objeto de autenticação do Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    // Carrega os menus do actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Manipula estados dos menus do actionBar
    // (chamado antes do menu ser exibido, toda vez que a activity é carregada)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Verifica se o usuário está logado e a partir disso, mostra o menu correspondente
        if (autenticacao.getCurrentUser() == null){
            menu.setGroupVisible(R.id.group_deslogado, true);
        }
        else {
            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // Manipula as funções dos menus do actionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_login:
                // Direciona para outra tela
                startActivity(new Intent(getApplicationContext(), LoginActiviy.class));
                break;

            case R.id.menu_servicos:
                // Direciona para outra tela
                startActivity(new Intent(getApplicationContext(), MeusServicosActivity.class));
                break;

            case R.id.menu_sair:
                // Método de desligamento de usuário do Firebase
                autenticacao.signOut();
                // Mensagem momentânea no rodapé
                Toast.makeText(MainActivity.this,
                        "Deslogado !",
                        Toast.LENGTH_SHORT).show();
                // Invalida o método onCreateOptionsMenu para atualizar a exibição dos menus
                invalidateOptionsMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
