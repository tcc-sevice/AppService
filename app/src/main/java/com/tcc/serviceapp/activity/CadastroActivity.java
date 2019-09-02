package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    private EditText dataNascimento,cpf,nome,sobrenome,email,telefone,senha,confirmasSenha;
    private RadioButton masculino,feminino,outro;
    private ImageView fotoPerfil;
    private Usuario usuario;
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        inicializaComponente();
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                    switch (requestCode){
                        case SELECAO_GALERIA:
                            Uri localImagem = data.getData();
                            imagem = MediaStore.Images
                                     .Media
                                     .getBitmap(getContentResolver(),localImagem);
                            break;
                    }
         if (imagem != null){

             fotoPerfil.setImageBitmap( imagem );
             //ByteArrayOutputStream


         }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void inicializaComponente(){

        nome = findViewById(R.id.nome);
        sobrenome = findViewById(R.id.sobrenome);
        cpf = findViewById(R.id.cpf);
        dataNascimento = findViewById(R.id.dataNascimento);
        masculino = findViewById(R.id.masculino);
        feminino = findViewById(R.id.feminino);
        outro = findViewById(R.id.outro);
        email = findViewById(R.id.email);
        telefone = findViewById(R.id.telefone);
        senha = findViewById(R.id.senha);
        confirmasSenha = findViewById(R.id.confirmeSenha);
        fotoPerfil = findViewById(R.id.fotoPerfil);
    }

    public Date formatDate (String dataNascimento) throws ParseException {

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-mm-dd");

        Date dataFormatada = null;

        try {
            dataFormatada = formato.parse(dataNascimento);

        } catch (ParseException e) {
            return null;
        }

        return dataFormatada;
    }

    public void abrirEndereco(View view) throws ParseException {
        Intent intent = new Intent(CadastroActivity.this, EnderecoActivity.class);
        EnderecoActivity enderecoActivity = new EnderecoActivity();
        String campoNome = nome.getText().toString();
        String campoSobrenome = sobrenome.getText().toString();
        String campoCpf = cpf.getText().toString();
        Date campoDataNascimento = formatDate(dataNascimento.getText().toString());
        String campoMasculino = masculino.getText().toString();
        String campoFeminino = feminino.getText().toString();
        String campoOutro = outro.getText().toString();
        String campoEmail = email.getText().toString();
        String campoTelefone = telefone.getText().toString();;
        String campoSenha = telefone.getText().toString();
        String campoConfirmasSenha = telefone.getText().toString();
        String caminhoFotoPerfil;
        enderecoActivity.recebeUser(usuario);
        startActivity(intent);
    }

}
