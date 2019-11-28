package com.tcc.serviceapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.helper.ValidaPermissoes;
import com.tcc.serviceapp.model.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastroServicoActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoLocalidade, campoCategoria;
    private EditText campoNomeServico, campoDescricaoServico;
    private CurrencyEditText campoValorServico;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>(); // Endereços do armaz. interno
    private List<String> listaUrlFotos = new ArrayList<>(); // Endereços de fotos salvas no Firebase

    private Servico servico;
    private StorageReference storage;

    private android.app.AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_servico);

        // Define o título da barra superior:
        getSupportActionBar().setTitle("Novo serviço");

        // Validando permissão da galeria de fotos, utilizando método da classe ValidaPermissoes
        ValidaPermissoes.validarPermissoes(permissoes, this,1);

        // Inicialização de componentes necessários da interface
        inicializarComponentes();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Carrega itens nos spinners
        carregarSpinner();
    }

    // Sobreescreve o método onClick para tratamento da imagem selecionada
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_AddFoto1:
                carregarImagem(1);
                break;

            case R.id.imageView_AddFoto2:
                carregarImagem(2);
                break;

            case R.id.imageView_AddFoto3:
                carregarImagem(3);
                break;
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
        if (resultCode == Activity.RESULT_OK){

            // Recupera endereço da imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            // Configura a imagem no ImageView
            if (requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
            }
            else if (requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }
            else if (requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
            }

            // Salva o caminho de cada imagem na lista
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    // Inicializa componentes necessários da interface ao criar nova instância de cadastro de serviços
    private void inicializarComponentes() {
        campoLocalidade = findViewById(R.id.spinner_localidade);
        campoCategoria = findViewById(R.id.spinner_categoria);

        imagem1 = findViewById(R.id.imageView_AddFoto1);
        imagem2 = findViewById(R.id.imageView_AddFoto2);
        imagem3 = findViewById(R.id.imageView_AddFoto3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        campoNomeServico = findViewById(R.id.editText_nomeServico);
        campoDescricaoServico = findViewById(R.id.editText_descricaoServico);
        campoValorServico = findViewById(R.id.editText_valorServico);
        // Configura localidade do campo de valor para pt-br, para que a moeda seja o Real
        campoValorServico.setLocale(new Locale("pt", "BR"));
        campoValorServico.setText(null);
    }

    // Preenche os Spinners (combobox) com informações
    private void carregarSpinner(){
        String[] cidades = new String[]{
                "Campinas",
                "Louveira",
                "Valinhos",
                "Vinhedo"};
        ArrayAdapter<String> adapterLocalidade = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, cidades);
        adapterLocalidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoLocalidade.setAdapter(adapterLocalidade);

        String[] categorias = new String[]{
                "Elétrica",
                "Mecânica",
                "Residencial",
                "Construção civil",
                "Cuidador"};
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
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
        builder.setMessage("Para carregar fotos, será necessário aceitar a permissão de acesso");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Finaliza a CadastroServicoActivity, impedindo o usuário de cadastrar serviço
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método que é chamado ao clicar no botão "Cadastrar Serviço"
    // Faz validação dos campos antes de salvar um novo serviço
    public void validarDadosServico(View view){

        // Configura os valores dos campos a serem validados
        servico = configurarServico();

        // Validações
        if (listaFotosRecuperadas.size() != 0){
            if (!servico.getLocalidade().isEmpty()){
                if (!servico.getCategoria().isEmpty()){
                    if (!servico.getNomeServico().isEmpty()){
                        if (!String.valueOf(campoValorServico.getRawValue()).isEmpty()
                                && !String.valueOf(campoValorServico.getRawValue()).equals("0")){
                            if (!servico.getDescricao().isEmpty()){
                                // Se todas as validações forem completadas corretamente, o serviço é salvo
                                salvarServico();
                            }
                            else{
                                exibirMensagemErro("Preencha o campo Descrição !");
                            }
                        }
                        else{
                            exibirMensagemErro("Preencha o campo Valor !");
                        }
                    }
                    else{
                        exibirMensagemErro("Preencha o campo Título !");
                    }
                }
                else{
                    exibirMensagemErro("Preencha o campo Categoria !");
                }
            }
            else{
                exibirMensagemErro("Preencha o campo Localidade !");
            }
        }
        else{
            exibirMensagemErro("Selecione ao menos uma foto !");
        }
    }

    // Método simples para deixar o código mais limpo
    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    /* Configura um objeto serviço para ser utilizado nas validações e/ou afins,
    recuperando todos os dados da interface*/
    private Servico configurarServico(){
        Servico servico = new Servico();
        servico.setLocalidade(campoLocalidade.getSelectedItem().toString());
        servico.setCategoria(campoCategoria.getSelectedItem().toString());
        servico.setNomeServico(campoNomeServico.getText().toString());
        servico.setValor(campoValorServico.getText().toString());
        servico.setDescricao(campoDescricaoServico.getText().toString());

        return servico;
    }

    // Após o devido tratamento, salva os dados do serviço a ser cadastrado
    private void salvarServico(){
        // Dialog de progresso do salvamento. Executa até receber o método dismiss(), ao salvar no banco
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando serviço")
                .setCancelable(false)
                .build();
        dialog.show();

        // Salva as imagens no Storage do Firebase
        // Percorre a lista de endereços das imagens
        for (int i = 0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            // Indica a quantidade a ser salva e suas urls
            salvarFotoStorage(urlImagem, listaFotosRecuperadas.size(), i);
        }
    }

    // Salva as imagens no Storage do Firebase
    private void salvarFotoStorage(String urlString, int totalFotos, int contador){
        // Cria nó no Storage
        StorageReference imagemServico = storage
                .child("Imagens")
                .child("Servicos")
                .child(servico.getIdServico())
                .child("imagemServico"+contador);

        // Faz o upload do arquivo de imagem
        UploadTask uploadTask = imagemServico.putFile(Uri.parse(urlString));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return imagemServico.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUrl = task.getResult();
                    listaUrlFotos.add(downloadUrl.toString());
                    // Condição para o upload do número de fotos adicionadas
                    if (totalFotos == listaUrlFotos.size()){
                        servico.setFotos(listaUrlFotos);
                        // Salva informações do serviço no banco de dados
                        servico.salvar();
                        // Interrompe o dialog de progresso
                        dialog.dismiss();
                        // Finaliza a activity
                        finish();
                    }
                }
            }
        });
    }

}
