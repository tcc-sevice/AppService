package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.model.Usuario;

public class LoginActiviy extends AppCompatActivity {

    // Atributos de manipulação dos componentes da interface
    private EditText email, senha;
    private TextView resetSenha;
    private Button logar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Esconde a barra superior
        getSupportActionBar().hide();

        // Configurações iniciais
        inicializarComponente();

        // Adiciona evento de clique no link de recuperação de senha na interface
        resetSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Direciona para a tela de recuperação
                startActivity(new Intent(getApplicationContext(), ResetSenhaActivity.class));
            }
        });

        // Método chamado com o clique do botão "Entrar" da tela de login
        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String campoEmail = email.getText().toString();
                String campoSenha = senha.getText().toString();

                // Validações de campos não vazios
                if (!campoEmail.isEmpty()) {
                    if (!campoSenha.isEmpty()) {
                        Usuario usuario = new Usuario();
                        usuario.setEmail(campoEmail);
                        usuario.setSenha(campoSenha);
                        validarLogin(usuario);
                    }
                    else {
                        Toast.makeText(LoginActiviy.this,
                                "Digite sua senha !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActiviy.this,
                            "Digite seu e-mail !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Inicializa atributos dessa classe com os componentes da interface
    public void inicializarComponente() {
        email        = findViewById(R.id.emailLogin);
        senha        = findViewById(R.id.senhaLogin);
        logar        = findViewById(R.id.login);
        resetSenha   = findViewById(R.id.esquecisenha);
    }

    // Método para fazer a validação dos dados para login
    public void validarLogin(Usuario usuario){
        // Objeto para manipulação da autenticação do Firebase
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        // Usa o método do Firebase de autenticação por email e senha
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActiviy.this,
                                    "Seja bem-vindo !",
                                    Toast.LENGTH_SHORT).show();

                            // Direciona para a tela principal ao logar
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            // Finaliza a instância da tela de login
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActiviy.this,
                                    "Erro ao fazer login !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    // Método para abrir a tela de cadastro ao clicar no botão "Cadastrar-se"
    public void abrirCadastro(View view){
        startActivity(new Intent(getApplicationContext(), CadastroUsuarioActivity.class));
    }

}
