package com.tcc.serviceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    // Atributos de manipulação dos componentes da interface
    private EditText email, senha;
    private Button logar;
    //
    private Usuario usuario;
    // Atributo para manipulação da autenticação do Firebase
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Esconde a barra superior:
        getSupportActionBar().hide();
        //
        inicializaComponente();
        // Método chamado com o clique do botão "Logar" da tela de login
        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String campoEmail = email.getText().toString();
                String campoSenha = senha.getText().toString();

                // Validações para campos não vazios
                if (!campoEmail.isEmpty()) {
                    if (!campoSenha.isEmpty()) {
                        usuario = new Usuario();
                        usuario.setEmail(campoEmail);
                        usuario.setSenha(campoSenha);
                        validarLogin(usuario);
                    } else {
                        // O Toast é uma pequena mensagem momentânea no rodapé do App
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

    // Método para fazer a validação dos dados para login
    public void validarLogin(Usuario usuario){
        // Recebe instancia do serviço de autenticação do Firebase
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
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
                                    "Login com sucesso !",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActiviy.this, MainActivity.class);
                            startActivity(intent);
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

    // Método para abrir a tela de cadastro ao clicar no botão "Cadastrar-se" da tela de Login
    public void abrirCadastro(View view){
        Intent intent = new Intent(LoginActiviy.this, CadastroActivity.class);
        startActivity(intent);
    }

    // Inicializa atributos dessa classe com os dados dos componentes da interface
    public void inicializaComponente() {
        email = findViewById(R.id.emailLogin);
        senha = findViewById(R.id.senhaLogin);
        logar = findViewById(R.id.login);
    }

}
