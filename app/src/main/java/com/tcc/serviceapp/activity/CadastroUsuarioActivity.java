package com.tcc.serviceapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.helper.ValidaDados;
import com.tcc.serviceapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText dataNascimento, cpf, nome, sobrenome, email, telefone, senha, confirmarSenha;
    private RadioButton masculino, feminino, outro;
    private CircleImageView fotoPerfil;
    private static final int SELECAO_GALERIA = 200;
    private FirebaseAuth autenticacao;
    private Calendar calendar;
    private Usuario usuario;
    private Uri url;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Define o título da barra superior:
        getSupportActionBar().setTitle("Sevice - Cadastre seus dados");

        // Inicialização de componentes necessários da interface
        inicializarComponentes();

        // Método que faz máscaras (formatação padão) para os campos de CPF, data de nascimento e telefone
        formatMascara();
        //
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        //
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
        //
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

        //Adiciona um evento do tipo listener ao campo 'data de nascimento'. Quando estiver focado,
        // um calendário será exibido para seleção da data
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
        fotoPerfil = findViewById(R.id.fotoPerfil);
        calendar = Calendar.getInstance();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        url = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(getContentResolver(), url);
                        break;
                }
                if (imagem != null) {

                    fotoPerfil.setImageBitmap(imagem);
                    // Esconde o texto "Carregar imagem" abaixo da foto de perfil
                    findViewById(R.id.textView_carregarImagem).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public void abrirEndereco(View view) throws ParseException {
        String campoNome = nome.getText().toString();
        String campoSobrenome = sobrenome.getText().toString();
        String campoCpf = cpf.getText().toString();
        String campoDataNascimento = dataNascimento.getText().toString();
        String campoEmail = email.getText().toString();
        String campoTelefone = telefone.getText().toString();
        String campoSenha = senha.getText().toString();
        String campoConfirmarSenha = confirmarSenha.getText().toString();
        String campoSexo = validaSexo();

        if (validaCampos( campoNome, campoSobrenome, campoCpf,campoDataNascimento,campoEmail, campoTelefone, campoSenha, campoConfirmarSenha) == "N") {

            Date dataNascimento = formatDate(campoDataNascimento);

            Drawable drawable = fotoPerfil.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();

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

            Intent intent = new Intent(getApplicationContext(), EnderecoActivity.class);
            intent.putExtra("BitmapImage", imageInByte);
            intent.putExtra("usuario", usuario);
            intent.putExtra("url", url);
            Query clienteCnpj = usuariosRef.orderByChild("cpf").equalTo(campoCpf);

            clienteCnpj.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(CadastroUsuarioActivity.this,
                                "Cpf já Cadastrado !",
                                Toast.LENGTH_LONG).show();
                        cpf.setError("Digite um novo Cpf");
                    } else {
                        startActivity(intent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private String validaSexo(){

        String sexo = "outro";

        if (masculino.isChecked()){
            sexo = "masc";
        }
        else if (feminino.isChecked()){
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

    private String validaCpf(String cpf){
        String cpfTransform = cpf.replaceAll("[^0-9]", "");
        String retornaErro = "N";

        if (!ValidaDados.validaCpf(cpfTransform)){
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
                if(campoCpf.length() == 14){
                    if( campoDataNascimento.length() == 10 ){
                        if( masculino.isChecked()||
                                feminino.isChecked()||
                                outro.isChecked()){
                            if( !campoEmail.isEmpty()){
                                if( campoTelefone.length() == 16){
                                    if (validaSenha(campoSenha,campoConfirmarSenha).equals("N")) {
                                        if (validateEmailFormat(campoEmail)){
                                            if (validaCpf(campoCpf).equals("N")){
                                                if (ValidaDados.validadeData(campoDataNascimento).equals("N")) {
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
}
