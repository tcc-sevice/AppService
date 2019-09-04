package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.model.Usuario;

public class LoginActiviy extends AppCompatActivity {

    private EditText email,senha;
    private Button logar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializaComponente();

        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String campoEmail = email.getText().toString();
                String campoSenha = senha.getText().toString();

                if (!campoEmail.isEmpty()) {
                    if (!campoEmail.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmail(campoEmail);
                        usuario.setSenha(campoSenha);
                        validarLogin(usuario);
                    } else {
                        Toast.makeText(LoginActiviy.this,
                                "Preencha a senha !",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActiviy.this,
                            "Preencha o email !",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void validarLogin(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(LoginActiviy.this,
                                    "Login com Sucesso !",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActiviy.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActiviy.this,
                                    "Erro ao Fazer Login !",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
     });
    }
    public void abrirCadastro(View view){
        Intent intent = new Intent(LoginActiviy.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void inicializaComponente() {

        email = findViewById(R.id.emailLogin);
        senha = findViewById(R.id.senhaLogin);
        logar = findViewById(R.id.login);
    }

}
