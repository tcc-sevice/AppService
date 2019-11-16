package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Endereco;

public class EnderecoActivity extends AppCompatActivity {

    private EditText cidade, rua, bairro, numero, complemento, cep;
    private ImageView fotoPerfil;
    private Endereco endereco;
    private String idEndereco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        // Define o título da barra superior:
        getSupportActionBar().setTitle("Sevice - Cadastre seus dados");
        inicializaComponente();
        formatMascara();
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        idEndereco =  intent.getParcelableExtra("idEndereco");
        fotoPerfil.setImageBitmap(bitmap);
    }

    public void inicializaComponente() {

        cidade = findViewById(R.id.cidade);
        rua = findViewById(R.id.rua);
        bairro = findViewById(R.id.bairro);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        cep = findViewById(R.id.cep);
        fotoPerfil = findViewById(R.id.fotoPerfil);

    }

    private void formatMascara() {

        SimpleMaskFormatter mascaraCep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher formatCep = new MaskTextWatcher(cep, mascaraCep);
        cep.addTextChangedListener(formatCep);
    }

    public void cadastrarEndereco(View view) {

        String campoCidade = cidade.getText().toString();
        String campoBairro = bairro.getText().toString();
        String campoRua = rua.getText().toString();
        String campoNumero = numero.getText().toString();
        String campoCep = cep.getText().toString();
        String campoComplemento = complemento.getText().toString();

        validaCampos(campoCidade, campoBairro, campoRua, campoNumero, campoCep, campoComplemento);

        Endereco enderecoPersisty = new Endereco();
        enderecoPersisty = preenchaEndereco(campoCidade, campoBairro, campoRua, campoNumero, campoCep, campoComplemento);

        try {
            enderecoPersisty.setId(idEndereco);
            enderecoPersisty.Salvar(enderecoPersisty);

            Toast.makeText(this,
                    "Cadastro realizado com sucesso !",
                    Toast.LENGTH_SHORT).show();

            Intent login = new Intent(EnderecoActivity.this, LoginActiviy.class);
            startActivity(login);

        } catch (Exception e) {

            Toast.makeText(this,
                    "Erro ao cadastrar endereço !",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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

    private void validaCampos(String campoCidade, String campoBairro, String campoRua, String campoNumero,
                              String campoCep, String campoComplemento) {

        if (!campoCidade.isEmpty()) {
            if (!campoBairro.isEmpty()) {
                if (!campoRua.isEmpty()) {
                    if (!campoNumero.isEmpty()) {
                        if (!campoCep.isEmpty()) {
                            if (!campoComplemento.isEmpty()) {
                                Intent intent = new Intent(EnderecoActivity.this,
                                        LoginActiviy.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(EnderecoActivity.this,
                                        "Preencha o Complemento !",
                                        Toast.LENGTH_SHORT).show();
                            }
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
    }
}
