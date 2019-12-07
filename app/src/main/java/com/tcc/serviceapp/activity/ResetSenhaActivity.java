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

    private FirebaseAuth autenticacao;
    private EditText email;
    private Button recuperarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);
        inicializaComponente();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        recuperarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                autenticacao.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        email.setText("");
                                        Toast.makeText(ResetSenhaActivity.this,
                                                "Email para Redefinição de Senha Enviado com Sucesso !",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ResetSenhaActivity.this,
                                                "Falha ao Enviar Email para Redefinição de Senha !",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

            }
        });
    }

    public void inicializaComponente() {
        email            = findViewById(R.id.emailReset);
        recuperarEmail   = findViewById(R.id.recSenha);
    }

}
