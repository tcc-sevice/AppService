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
import com.google.android.gms.tasks.Continuation;
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
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnderecoActivity extends AppCompatActivity {

    // Atributos
    private EditText cidade, rua, bairro, numero, complemento, cep;
    private TextView esqueciCep;
    private CircleImageView fotoPerfil;
    private Endereco endereco;
    private FirebaseAuth autenticacao;
    private StorageReference storage;
    private Button cadastrar;
    private Usuario usuario;
    private Uri imagemSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        // Define o título da barra superior:
        getSupportActionBar().setTitle("Sevice - Cadastre seu endereço");

        // Recebe e atribui elementos vindos da activity anterior
        Intent intent = getIntent();
        imagemSelecionada = intent.getParcelableExtra("imagemSelecionada");
        usuario = (Usuario) intent.getSerializableExtra("usuario");

        //
        inicializaComponente();

        //
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //
        formatMascara();

        // Chamado ao clicar no texto "Esqueci meu CEP"
        esqueciCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.buscacep.correios.com.br/sistemas/buscacep")));
            }
        });

        // Chamado ao clicar no botão cadastrar
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        salvarFotoStorage();

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

    // Salva a foto de perfil no Storage do Firebase
    private void salvarFotoStorage(){
        // Cria nó no Storage
        StorageReference imagemUsuario = storage
                .child("Imagens")
                .child("Perfil")
                .child(usuario.getId());

        // Faz o upload do arquivo de imagem
        UploadTask uploadTask = imagemUsuario.putFile(imagemSelecionada);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return imagemUsuario.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUrl = task.getResult();
                    String path = downloadUrl.toString();
                    usuario.setIdFoto(path);
                    usuario.Salvar();
                    cadastrarEndereco();
                }
            }
        });
    }

    //
    public void inicializaComponente() {
        cidade = findViewById(R.id.cidade);
        rua = findViewById(R.id.rua);
        bairro = findViewById(R.id.bairro);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        cep = findViewById(R.id.cep);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        fotoPerfil.setImageURI(imagemSelecionada);
        cadastrar = findViewById(R.id.cadastrar);
        esqueciCep = findViewById(R.id.localizaCep);
    }

    //
    private void formatMascara() {
        SimpleMaskFormatter mascaraCep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher formatCep = new MaskTextWatcher(cep, mascaraCep);
        cep.addTextChangedListener(formatCep);
    }

    //
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

    //
    public String removeCaracteresEspeciais (String rmcaracter){
        rmcaracter = rmcaracter.replaceAll("[^a-zZ-Z0-9 ]", "");
        return rmcaracter;
    }

    //
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

    //
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
