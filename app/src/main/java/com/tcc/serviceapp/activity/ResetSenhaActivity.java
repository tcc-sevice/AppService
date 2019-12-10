package com.tcc.serviceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

public class ResetSenhaActivity extends AppCompatActivity {

    // Atributos
    private FirebaseAuth autenticacao;
    private EditText email;
    private Button recuperarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);

        // Título no action bar
        getSupportActionBar().setTitle("Recuperação de senha");

        // Inicialização dos componentes
        email = findViewById(R.id.emailReset);
        recuperarEmail = findViewById(R.id.recSenha);

        // Referencia do serviço de autenticaçao do Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Evento de clique no botao da interface
        recuperarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Método de reset de senha do serviço de autenticação do Firebase
                autenticacao.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        email.setText("");
                                        Toast.makeText(ResetSenhaActivity.this,
                                                "E-mail para redefinição de senha enviado para o endereço informado !",
                                                Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(ResetSenhaActivity.this,
                                                "Falha ao enviar requisição para redefinição de senha, certifique-se que o endereço de e-mail é válido !",
                                                Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

            }
        });
    }
}
