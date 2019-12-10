package com.tcc.serviceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.tcc.serviceapp.model.Endereco;
import com.tcc.serviceapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnderecoActivity extends AppCompatActivity {

    // Atributos
    private EditText cidade, rua, bairro, numero, complemento, cep;
    private TextView esqueciCep;
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

        // Inicializa os atributos com os componentes da interface necessários
        inicializarComponente();

        // Referencia do Storage do Firebase para armazenar imagens
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Atribui formatação padrão para o campo de CEP
        formatMascara();

        // Chamado ao clicar no texto "Esqueci meu CEP"
        esqueciCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.buscacep.correios.com.br/sistemas/buscacep")));
            }
        });

        // Chamado ao clicar no botão cadastrar, realiza ultimas validações, cria e salva os dados do usuario
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
                                        // O CPF do usuário será como a primary key no banco de dados
                                        usuario.setId(removerCaracteresEspeciais(usuario.getCpf()));
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

    // Inicializa os atributos com os componentes da interface necessários
    public void inicializarComponente() {
        TextView nomeUsuarioCadastrando = findViewById(R.id.textView_nomeUsuarioCadastrando);
        String nomeCompleto = usuario.getNome() + " " + usuario.getSobrenome();
        nomeUsuarioCadastrando.setText(nomeCompleto);
        cidade = findViewById(R.id.cidade);
        rua = findViewById(R.id.rua);
        bairro = findViewById(R.id.bairro);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        cep = findViewById(R.id.cep);
        CircleImageView fotoPerfil = findViewById(R.id.fotoPerfil);
        fotoPerfil.setImageURI(imagemSelecionada);
        cadastrar = findViewById(R.id.cadastrar);
        esqueciCep = findViewById(R.id.localizaCep);
    }

    // Atribui formatação padrão para o campo de CEP
    private void formatMascara() {
        SimpleMaskFormatter mascaraCep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher formatCep = new MaskTextWatcher(cep, mascaraCep);
        cep.addTextChangedListener(formatCep);
    }

    // Salva os dados de endereço do usuário, após salvar os dados pessoais, e conclui o processo
    public void cadastrarEndereco() {
        String campoCidade = cidade.getText().toString();
        String campoBairro = bairro.getText().toString();
        String campoRua = rua.getText().toString();
        String campoNumero = numero.getText().toString();
        String campoCep = cep.getText().toString();
        String campoComplemento = complemento.getText().toString();

        if (validarCampos(campoCidade, campoBairro, campoRua, campoNumero, campoCep)== "N") {

            Endereco enderecoPersisty = preencherEndereco(campoCidade, campoBairro, campoRua, campoNumero, campoCep, campoComplemento);
            try {
                enderecoPersisty.setId(removerCaracteresEspeciais(usuario.getCpf()));
                enderecoPersisty.Salvar(enderecoPersisty);

                Toast.makeText(this,
                        "Cadastro realizado com sucesso !",
                        Toast.LENGTH_SHORT).show();

                // Direciona para a tela main e finaliza a activity
                startActivity(new Intent(EnderecoActivity.this, MainActivity.class));
                finishAffinity();

            } catch (Exception e) {

                Toast.makeText(this,
                        "Erro ao cadastrar endereço !",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Remove certos elemento da String de CPF para correto upload no banco de dados
    public String removerCaracteresEspeciais(String rmcaracter){
        rmcaracter = rmcaracter.replaceAll("[^a-zZ-Z0-9 ]", "");
        return rmcaracter;
    }

    // Configura o objeto endereço
    private Endereco preencherEndereco(String campoCidade, String campoBairro, String campoRua, String campoNumero,
                                       String campoCep, String campoComplemento) {
        Endereco endereco = new Endereco();
        endereco.setCidade(campoCidade);
        endereco.setBairro(campoBairro);
        endereco.setRua(campoRua);
        endereco.setNumero(Integer.valueOf(campoNumero));
        endereco.setCep(campoCep);
        endereco.setComplemento(campoComplemento);
        return endereco;
    }

    // Verificação de todos os campos digitados
    private String validarCampos(String campoCidade, String campoBairro, String campoRua, String campoNumero,
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
