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
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroActivity extends AppCompatActivity {

    private EditText dataNascimento,cpf,nome,sobrenome,email,telefone,senha, confirmarSenha;
    private RadioButton masculino,feminino,outro;
    private ImageView fotoPerfil;
    private Usuario usuario;
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        inicializaComponente();
        formatMascarData();
        //formatMascarTelefone();
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
        confirmarSenha = findViewById(R.id.confirmeSenha);
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

    public void formatMascarData(){

        SimpleMaskFormatter mascaraData = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher formatData = new MaskTextWatcher(dataNascimento,mascaraData);
        dataNascimento.addTextChangedListener(formatData);
    }

//    public void formatMascarTelefone(){
//
//        SimpleMaskFormatter mascaraTel = new SimpleMaskFormatter("(NN)-NNNNN-NNNN");
//        MaskTextWatcher formatTel = new MaskTextWatcher(telefone,mascaraTel);
//        dataNascimento.addTextChangedListener(formatTel);
//    }

    public void abrirEndereco(View view) throws ParseException {
        String campoNome = nome.getText().toString();
        String campoSobrenome = sobrenome.getText().toString();
        String campoCpf = cpf.getText().toString();
        Date campoDataNascimento = formatDate(dataNascimento.getText().toString());
        String campoEmail = email.getText().toString();
        String campoTelefone = telefone.getText().toString();;
        String campoSenha = senha.getText().toString();
        String campoConfirmarSenha = confirmarSenha.getText().toString();

        validaCampos( campoNome, campoSobrenome, campoCpf, campoDataNascimento,campoEmail, campoTelefone, campoSenha, campoConfirmarSenha);

//        preencheUser( campoNome, campoSobrenome, campoCpf, campoDataNascimento, campoEmail,
//                      campoTelefone, campoSenha, campoConfirmasSenha );


        //enderecoActivity.recebeUser(usuario);

    }

    private void preencheUser(String campoNome, String campoSobrenome, String campoCpf, Date campoDataNascimento, String campoEmail, String campoTelefone, String campoSenha, String campoConfirmasSenha) {
        usuario.setNome(campoNome);
        usuario.setSobrenome(campoSobrenome);
        usuario.setCpf(Float.valueOf(campoCpf));
        usuario.setDataNascimento(campoDataNascimento);
        usuario.setSexo(validaSexo());
        usuario.setEmail(campoEmail);
        usuario.setTelefone(campoTelefone);
        usuario.setSenha(campoSenha);
        usuario.setConfirmasSenha(campoConfirmasSenha);
    }

    private String validaSexo(){

        String sexo = "outro";

        if (masculino.isChecked()){
            sexo = "masc";
        }else if (feminino.isChecked()){
            sexo = "fem";
        }

        return sexo;
    }

    private String validaSenha(String senha,String confirmaSenha){

        String retornaErro = "N";

        if( !senha.isEmpty()){
            if( !confirmaSenha.isEmpty()){
                if(senha.equals(confirmaSenha)){
                }else{
                    Toast.makeText( CadastroActivity.this,
                            "as senhas não corresponde, confirme a senha igual a preenchida anteriomente !",
                            Toast.LENGTH_SHORT).show();

                    retornaErro = "S";
                }
            }else{
                Toast.makeText( CadastroActivity.this,
                        "confirme a senha !",
                        Toast.LENGTH_SHORT).show();

                retornaErro = "S";
            }
        }else{
            Toast.makeText( CadastroActivity.this,
                    "preencha a senha !",
                    Toast.LENGTH_SHORT).show();

            retornaErro = "S";
        }

        return retornaErro;
    }
    private void validaCampos( String campoNome, String campoSobrenome, String campoCpf, Date campoDataNascimento,
                               String campoEmail,String campoTelefone, String campoSenha,
                               String campoConfirmarSenha ) {
        if(!campoNome.isEmpty()){
            if(!campoSobrenome.isEmpty()){
                if(!campoCpf.isEmpty()){
                    if( campoDataNascimento == null){
                        if( masculino.isChecked()||
                            feminino.isChecked()||
                            outro.isChecked()){
                              if( !campoEmail.isEmpty()){
                                  if( !campoTelefone.isEmpty()){
                                      if (validaSenha(campoSenha,campoConfirmarSenha)== "N") {
                                          Intent intent = new Intent(CadastroActivity.this, EnderecoActivity.class);
                                          EnderecoActivity enderecoActivity = new EnderecoActivity();
                                          startActivity(intent);
                                      }
                                  }else{
                                      Toast.makeText(CadastroActivity.this,
                                                     "preencha o Telefone !",
                                                      Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(CadastroActivity.this,
                                                   "preencha o email !",
                                                    Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText( CadastroActivity.this,
                                        "preencha o sexo !",
                                        Toast.LENGTH_SHORT).show();
                            }
                    }else{
                        Toast.makeText( CadastroActivity.this,
                                "preencha a data de nascimento !",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText( CadastroActivity.this,
                            "preencha o Cpf !",
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText( CadastroActivity.this,
                        "preencha o Sobrenome !",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText( CadastroActivity.this,
                            "preencha o nome !",
                            Toast.LENGTH_SHORT).show();
        }
    }

}
