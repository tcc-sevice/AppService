package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CadastroActivity extends AppCompatActivity {

    private EditText dataNascimento, cpf, nome, sobrenome, email, telefone, senha, confirmarSenha;
    private RadioButton masculino, feminino, outro;
    private ImageView fotoPerfil;
    private static final int SELECAO_GALERIA = 200;
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private Usuario usuario;
    private String idFoto;
    private Uri url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        inicializaComponente();
        formatMascara();

        Intent i = getIntent();

        idFoto = UUID.randomUUID().toString();

        storageReference = ConfiguracaoFirebase.getStorageReference();

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(getContentResolver(), localImagem);
                        break;
                }
                if (imagem != null) {

                    fotoPerfil.setImageBitmap(imagem);
                    carregaFoto(imagem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void carregaFoto(Bitmap imagem) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        StorageReference imageRef = storageReference.child("Imagens")
                                                    .child("Perfil")
                                                    .child( idFoto + ".jpeg");

        UploadTask uploadTask = imageRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastroActivity.this,
                               "Erro ao fazer Upload da imagem" ,
                                Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(CadastroActivity.this,
                        "Sucesso ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializaComponente() {

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

    private Date formatDate(String dataNascimento) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date dataFormatada = null;

        try {
            dataFormatada = sdf.parse(dataNascimento);

        } catch (ParseException e) {
            return null;
        }

        return dataFormatada;
    }

    private boolean validateEmailFormat(final String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    private void formatMascara() {

        SimpleMaskFormatter mascaraData = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher formatData = new MaskTextWatcher(dataNascimento, mascaraData);
        dataNascimento.addTextChangedListener(formatData);

        SimpleMaskFormatter mascaraTel = new SimpleMaskFormatter("(NN)-NNNNN-NNNN");
        MaskTextWatcher formatTel = new MaskTextWatcher(telefone, mascaraTel);
        telefone.addTextChangedListener(formatTel);

        SimpleMaskFormatter mascaraCpf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher formatCpf = new MaskTextWatcher(cpf, mascaraCpf);
        cpf.addTextChangedListener(formatCpf);
    }

    private String preencheUser(String campoNome, String campoSobrenome, String campoCpf, Date campoDataNascimento, String campoEmail, String campoTelefone, String campoSenha, String campoConfirmasSenha) {
        String sexo = validaSexo();
        final Usuario usuario = new Usuario();
        usuario.setNome(campoNome);
        usuario.setSobrenome(campoSobrenome);
        usuario.setCpf(campoCpf);
        usuario.setDataNascimento(campoDataNascimento);
        usuario.setSexo(sexo);
        usuario.setEmail(campoEmail);
        usuario.setTelefone(campoTelefone);
        usuario.setSenha(campoSenha);
        usuario.setConfirmasSenha(campoConfirmasSenha);
        usuario.setIdFoto(idFoto);

        //FirebaseApp.initializeApp(this);
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){
                            try {
                                String idUsuario =  task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.Salvar(usuario);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {

                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + erroExcecao ,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

        EnderecoActivity enderecoActivity = new EnderecoActivity();
        return usuario.getId();
    }

    public void abrirEndereco(View view) throws ParseException {
        String campoNome = nome.getText().toString();
        String campoSobrenome = sobrenome.getText().toString();
        String campoCpf = cpf.getText().toString();
        String campoDataNascimento = dataNascimento.getText().toString();
        String campoEmail = email.getText().toString();
        String campoTelefone = telefone.getText().toString();;
        String campoSenha = senha.getText().toString();
        String campoConfirmarSenha = confirmarSenha.getText().toString();

        if (validaCampos( campoNome, campoSobrenome, campoCpf,campoDataNascimento,campoEmail, campoTelefone, campoSenha, campoConfirmarSenha) == "N") {

            String id = preencheUser( campoNome, campoSobrenome, campoCpf, formatDate(campoDataNascimento), campoEmail,
                                      campoTelefone, campoSenha, campoConfirmarSenha);

            fotoPerfil.buildDrawingCache();
            Bitmap bitmap = fotoPerfil.getDrawingCache();

            Intent intent = new Intent(CadastroActivity.this, EnderecoActivity.class);
            intent.putExtra("BitmapImage", bitmap);
            intent.putExtra("idEndereco",id);
            startActivity(intent);
        }
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
    private String validaCampos( String campoNome, String campoSobrenome, String campoCpf, String campoDataNascimento,
                                 String campoEmail,String campoTelefone, String campoSenha,
                                 String campoConfirmarSenha ) {
        String retornoErro = "S";

        if(!campoNome.isEmpty()){
            if(!campoSobrenome.isEmpty()){
                if(!campoCpf.isEmpty()){
                    if( !campoDataNascimento.isEmpty()){
                        if( masculino.isChecked()||
                            feminino.isChecked()||
                            outro.isChecked()){
                              if( !campoEmail.isEmpty()){
                                  if( !campoTelefone.isEmpty()){
                                      if (validaSenha(campoSenha,campoConfirmarSenha)== "N") {
                                          if (validateEmailFormat(campoEmail)){
                                              retornoErro = "N";
                                          }else{
                                              Toast.makeText( CadastroActivity.this,
                                                      "email invalido !",
                                                      Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  }else{
                                      Toast.makeText( CadastroActivity.this,
                                                      "preencha o Telefone !",
                                                       Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText( CadastroActivity.this,
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

        return retornoErro;
    }
}