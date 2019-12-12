package com.tcc.serviceapp.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.helper.ValidaDados;
import com.tcc.serviceapp.helper.ValidaPermissoes;
import com.tcc.serviceapp.model.Usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    // Atributos
    private EditText dataNascimento, cpf, nome, sobrenome, email, telefone, senha, confirmarSenha;
    private RadioButton masculino, feminino, outro;
    private CircleImageView fotoPerfil;
    private Calendar calendar;
    private Uri imagemSelecionada;
    private DatabaseReference usuariosRef;
    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Define o título da barra superior:
        getSupportActionBar().setTitle("Sevice - Cadastre seus dados");

        // Validando permissão da galeria de fotos, utilizando método da classe ValidaPermissoes
        ValidaPermissoes.validarPermissoes(permissoes, this,1);

        // Inicialização de componentes necessários da interface
        inicializarComponentes();

        // Método que faz máscaras (formatação padão) para os campos de CPF, data de nascimento e telefone
        formatMascara();

        // Referencias para o banco de dados do Firebase
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        // Configurações do campo de data de nascimento
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                dataNascimento.setText(sdf.format(calendar.getTime()));
            }
        };

        //Adiciona um evento do tipo listener ao campo 'data de nascimento'.
        // Quando estiver focado, um calendário será exibido para seleção da data
        dataNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.isFocused()){
                    new DatePickerDialog(CadastroUsuarioActivity.this, date,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    // Inicializa componentes necessários da interface ao criar nova instância de cadastro
    private void inicializarComponentes() {
        nome = findViewById(R.id.nome);
        sobrenome = findViewById(R.id.sobrenome);
        dataNascimento = findViewById(R.id.dataNascimento);
        cpf = findViewById(R.id.cpf);
        masculino = findViewById(R.id.masculino);
        feminino = findViewById(R.id.feminino);
        outro = findViewById(R.id.outro);
        email = findViewById(R.id.email);
        telefone = findViewById(R.id.telefone);
        senha = findViewById(R.id.senha);
        confirmarSenha = findViewById(R.id.confirmeSenha);
        calendar = Calendar.getInstance();
        fotoPerfil = findViewById(R.id.fotoPerfil);
        fotoPerfil.setOnClickListener(this);

    }


    // Sobreescreve o método onClick para tratamento da imagem selecionada
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fotoPerfil){
            carregarImagem(1);
        }
    }

    // Abre a galeria de fotos para seleção
    private void carregarImagem(int requestCode){
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode);
    }

    // Executado com a seleção da imagem dentro da galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se a imagem foi selecionada corretamente
        if (resultCode == RESULT_OK) {

            // Recupera endereço da imagem
            imagemSelecionada = data.getData();

            //Configura a imagem no CircleImageView
            fotoPerfil.setImageURI(imagemSelecionada);

            // Esconde o texto "Carregar imagem" abaixo da foto de perfil
            findViewById(R.id.textView_carregarImagem).setVisibility(View.GONE);
        }
    }

    // Formatação da data de nascimento para um padrão
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

    // Verifica se o formato do e-mail digitado pertence ao padrão
    private boolean validateEmailFormat(final String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    // Define máscaras (formatação padrão) para os campos de CPF, data de nascimento e telefone
    private void formatMascara() {

        SimpleMaskFormatter mascaraCpf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher formatCpf = new MaskTextWatcher(cpf, mascaraCpf);
        cpf.addTextChangedListener(formatCpf);

        SimpleMaskFormatter mascaraDataNascimento = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher formatDataNascimento = new MaskTextWatcher(dataNascimento, mascaraDataNascimento);
        dataNascimento.addTextChangedListener(formatDataNascimento);

        SimpleMaskFormatter mascaraTel = new SimpleMaskFormatter("(NN) N NNNN-NNNN");
        MaskTextWatcher formatTel = new MaskTextWatcher(telefone, mascaraTel);
        telefone.addTextChangedListener(formatTel);
    }

    // Executado ao clicar no botão da interface, chama a validação de dados antes de abrir a próxima tela
    public void abrirEndereco(View view) throws ParseException {
        String campoNome = nome.getText().toString();
        String campoSobrenome = sobrenome.getText().toString();
        String campoCpf = cpf.getText().toString();
        String campoDataNascimento = dataNascimento.getText().toString();
        String campoEmail = email.getText().toString();
        String campoTelefone = telefone.getText().toString();
        String campoSenha = senha.getText().toString();
        String campoConfirmarSenha = confirmarSenha.getText().toString();
        String campoSexo = validarSexo();

        if (validarCampos(campoNome, campoSobrenome, campoCpf,campoDataNascimento,campoEmail,
                campoTelefone, campoSenha, campoConfirmarSenha) == "N") {

            Date dataNascimento = formatDate(campoDataNascimento);

            final Usuario usuario = new Usuario();
            usuario.setNome(campoNome);
            usuario.setSobrenome(campoSobrenome);
            usuario.setCpf(campoCpf);
            usuario.setDataNascimento(dataNascimento);
            usuario.setSexo(campoSexo);
            usuario.setEmail(campoEmail);
            usuario.setTelefone(campoTelefone);
            usuario.setSenha(campoSenha);
            usuario.setConfirmaSenha(campoConfirmarSenha);
            usuario.setIdFoto("fotoUrl");

            // Envia objeto usuario e endereço local da imagem selecionada para a proxima activity
            Intent intent = new Intent(getApplicationContext(), EnderecoActivity.class);
            intent.putExtra("imagemSelecionada", imagemSelecionada);
            intent.putExtra("usuario", usuario);

            // Query para verificação da existência ou não do CPF informado no banco de dados
            Query usuarioCpf = usuariosRef.orderByChild("cpf").equalTo(campoCpf);
            usuarioCpf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cpf.setError("Esse CPF já esta cadastrado");
                    } else {
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    // Atribui a escolha de sexo para uma String
    private String validarSexo(){

        String sexo = "outro";

        if (masculino.isChecked()){
            sexo = "masc";
        }
        else if (feminino.isChecked()){
            sexo = "fem";
        }
        return sexo;
    }

    // Verifica as senhas digitadas e retorna se estão de acordo ou não
    private String validarSenha(String senha, String confirmaSenha){

        String retornaErro = "N";

        if( !senha.isEmpty()){
            if( !confirmaSenha.isEmpty()){
                if(senha.equals(confirmaSenha)){
                }else{
                    Toast.makeText( CadastroUsuarioActivity.this,
                            "As senhas digitadas não correspondem, digite a mesma senha nos dois campos !",
                            Toast.LENGTH_LONG).show();

                    retornaErro = "S";
                }
            }else{
                Toast.makeText( CadastroUsuarioActivity.this,
                        "Confirme a senha !",
                        Toast.LENGTH_SHORT).show();

                retornaErro = "S";
            }
        }else{
            Toast.makeText( CadastroUsuarioActivity.this,
                    "Preencha a senha !",
                    Toast.LENGTH_SHORT).show();

            retornaErro = "S";
        }
        return retornaErro;
    }

    // Verifica o CPF digitado e por meio da classe ValidaDados, calcula se a numeração é provável
    private String validarCpf(String cpf){
        String cpfTransform = cpf.replaceAll("[^0-9]", "");
        String retornaErro = "N";

        if (!ValidaDados.validarCpf(cpfTransform)){
            retornaErro = "S";
        }
        return retornaErro;
    }

    // Faz a verificação e respectiva validação do preenchimento de cada campo da interface
    private String validarCampos(String campoNome, String campoSobrenome, String campoCpf, String campoDataNascimento,
                                 String campoEmail, String campoTelefone, String campoSenha,
                                 String campoConfirmarSenha ) {
        String retornoErro = "S";

        if(!campoNome.isEmpty()){
            if(!campoSobrenome.isEmpty()){
                if(campoCpf.length() == 14){
                    if( campoDataNascimento.length() == 10 ){
                        if( masculino.isChecked()||
                                feminino.isChecked()||
                                outro.isChecked()){
                            if( !campoEmail.isEmpty()){
                                if( campoTelefone.length() == 16){
                                    if (validarSenha(campoSenha,campoConfirmarSenha).equals("N")) {
                                        if (validateEmailFormat(campoEmail)){
                                            if (validarCpf(campoCpf).equals("N")){
                                                if (ValidaDados.validarData(campoDataNascimento).equals("N")) {
                                                    retornoErro = "N";
                                                }else{
                                                    Toast.makeText(CadastroUsuarioActivity.this,
                                                            "Digite uma data de nascimento válida !",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText( CadastroUsuarioActivity.this,
                                                        "Digite um CPF válido !",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText( CadastroUsuarioActivity.this,
                                                    "O e-mail digitado é inválido !",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else{
                                    Toast.makeText( CadastroUsuarioActivity.this,
                                            "Preencha o telefone !",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText( CadastroUsuarioActivity.this,
                                        "Preencha seu e-mail !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText( CadastroUsuarioActivity.this,
                                    "Preencha seu sexo !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText( CadastroUsuarioActivity.this,
                                "Preencha a data de nascimento !",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText( CadastroUsuarioActivity.this,
                            "Preencha seu CPF !",
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText( CadastroUsuarioActivity.this,
                        "Preencha seu sobrenome !",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText( CadastroUsuarioActivity.this,
                    "Preencha seu nome !",
                    Toast.LENGTH_SHORT).show();
        }

        return retornoErro;
    }

    // Trata o retorno da solicitação de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertarValidacaoPermissao();
            }
        }
    }

    // Mostra um alerta ao usuário quando não houver a permissão de acesso ao armazenamento
    private void alertarValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão negada");
        builder.setMessage("Para utilizar o Sevice, será necessário aceitar a permissão de acesso para carregar fotos");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Finaliza a CadastroUsuarioActivity caso o usuário não concorde
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
