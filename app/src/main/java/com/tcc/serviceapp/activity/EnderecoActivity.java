package com.tcc.serviceapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.model.Endereco;
import com.tcc.serviceapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class EnderecoActivity extends AppCompatActivity {

    // Atributos
    private EditText cidade, rua, bairro, numero, complemento, cep;
    private TextView esqueciCep;
    private ImageView fotoPerfil;
    private Endereco endereco;
    private String idFoto;
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private Button cadastrar;
    private Usuario usuario;
    private Uri uri;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        // Define o título da barra superior:
        getSupportActionBar().setTitle("Sevice - Cadastre seu endereço");


        inicializaComponente();
        formatMascara();
        Intent intent = getIntent();
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                try {
                    byte[] imageInByte = bundle.getByteArray("BitmapImage");
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageInByte,0,imageInByte.length);
                    fotoPerfil = findViewById(R.id.fotoPerfil);
                    fotoPerfil.setImageBitmap(bmp);
                }catch (Exception e){}
            }
        }catch (Exception e){
            Toast.makeText(this,""+e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        uri = intent.getParcelableExtra("url");
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        esqueciCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.buscacep.correios.com.br/sistemas/buscacep")));
            }
        });
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario.setId(idFoto);
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.createUserWithEmailAndPassword(
                        usuario.getEmail(),
                        usuario.getSenha()).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    try {
                                        usuario.setId(removeCaracteresEspeciais(usuario.getCpf()));
                                        usuario.Salvar(usuario);
                                        cadastrarEndereco();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    String erroExcecao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        erroExcecao = "Digite uma senha mais forte, use combinações de letras e números !";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        erroExcecao = "Por favor, digite um e-mail válido !";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        erroExcecao = "Este e-mail já está cadastrado !";
                                    } catch (Exception e) {
                                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(EnderecoActivity.this,
                                            "Erro: " + erroExcecao,
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                );
            }
        });
    }

    public void inicializaComponente() {

        cidade = findViewById(R.id.cidade);
        rua = findViewById(R.id.rua);
        bairro = findViewById(R.id.bairro);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        cep = findViewById(R.id.cep);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        idFoto = UUID.randomUUID().toString();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        cadastrar = findViewById(R.id.cadastrar);
        esqueciCep = findViewById(R.id.localizaCep);

    }

    private void formatMascara() {

        SimpleMaskFormatter mascaraCep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher formatCep = new MaskTextWatcher(cep, mascaraCep);
        cep.addTextChangedListener(formatCep);
    }

    public void cadastrarEndereco() {

        String campoCidade = cidade.getText().toString();
        String campoBairro = bairro.getText().toString();
        String campoRua = rua.getText().toString();
        String campoNumero = numero.getText().toString();
        String campoCep = cep.getText().toString();
        String campoComplemento = complemento.getText().toString();

        if (validaCampos(campoCidade, campoBairro, campoRua, campoNumero, campoCep)== "N") {

            Endereco enderecoPersisty = preenchaEndereco(campoCidade, campoBairro, campoRua, campoNumero, campoCep, campoComplemento);

            try {
                enderecoPersisty.setId(removeCaracteresEspeciais(usuario.getCpf()));
                enderecoPersisty.Salvar(enderecoPersisty);

                Toast.makeText(this,
                        "Cadastro realizado com sucesso !",
                        Toast.LENGTH_SHORT).show();
                if (uri != null)
                    carregaFoto(uri);

                Intent login = new Intent(EnderecoActivity.this, MainActivity.class);
                startActivity(login);
                finishAffinity();

            } catch (Exception e) {

                Toast.makeText(this,
                        "Erro ao cadastrar endereço !",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void carregaFoto(Uri uri) {

        StorageReference imageRef = storageReference.child("Imagens")
                .child("Perfil")
                .child( idFoto + ".jpeg");
        imageRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnderecoActivity.this,
                        "Erro ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                /*Toast.makeText(CadastroUsuarioActivity.this,
                        "Sucesso ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    public String removeCaracteresEspeciais (String rmcaracter){
        rmcaracter = rmcaracter.replaceAll("[^a-zZ-Z0-9 ]", "");
        return rmcaracter;
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
                Toast.makeText(EnderecoActivity.this,
                        "Erro ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Esconde o texto "Carregar imagem" abaixo da foto de perfil
                findViewById(R.id.textView_carregarImagem).setVisibility(View.GONE);

                /*Toast.makeText(CadastroUsuarioActivity.this,
                        "Sucesso ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }
    
    private String preencheUser(String campoNome, String campoSobrenome, String campoCpf, Date campoDataNascimento, String campoEmail, String campoTelefone, String campoSenha, String campoConfirmarSenha, String campoSexo) {

        final Usuario usuario = new Usuario();
        usuario.setNome(campoNome);
        usuario.setSobrenome(campoSobrenome);
        usuario.setCpf(campoCpf);
        usuario.setDataNascimento(campoDataNascimento);
        usuario.setSexo(campoSexo);
        usuario.setEmail(campoEmail);
        usuario.setTelefone(campoTelefone);
        usuario.setSenha(campoSenha);
        usuario.setConfirmaSenha(campoConfirmarSenha);


        //FirebaseApp.initializeApp(this);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
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
                                erroExcecao = "Digite uma senha mais forte, use combinações de letras e números !";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido !";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este e-mail já está cadastrado !";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(EnderecoActivity.this,
                                    "Erro: " + erroExcecao ,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

        return usuario.getId();
    }

    private Endereco preenchaEndereco(String campoCidade, String campoBairro, String campoRua, String campoNumero,
                                      String campoCep, String campoComplemento) {
        endereco = new Endereco();
        endereco.setCidade(campoCidade);
        endereco.setBairro(campoBairro);
        endereco.setRua(campoRua);
        endereco.setNumero(Integer.valueOf(campoNumero));
        endereco.setCep(campoCep);
        endereco.setComplemento(campoComplemento);
        return endereco;
    }

    private String validaCampos(String campoCidade, String campoBairro, String campoRua, String campoNumero,
                              String campoCep) {
        String retornoErro = "S";
        if (!campoCidade.isEmpty()) {
            if (!campoBairro.isEmpty()) {
                if (!campoRua.isEmpty()) {
                    if (!campoNumero.isEmpty()) {
                        if ( campoCep.length()== 9) {
                                 retornoErro = "N";
                        } else {
                            Toast.makeText(EnderecoActivity.this,
                                    "Preencha o Cep !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EnderecoActivity.this,
                                "Preencha o número !",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EnderecoActivity.this,
                            "Preencha a rua !",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EnderecoActivity.this,
                        "Preencha o bairro !",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EnderecoActivity.this,
                    "Preencha a cidade !",
                    Toast.LENGTH_SHORT).show();
        }
      return retornoErro;
    }
}
