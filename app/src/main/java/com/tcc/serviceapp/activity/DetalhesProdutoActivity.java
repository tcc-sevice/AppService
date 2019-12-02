package com.tcc.serviceapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Servico;

public class DetalhesProdutoActivity extends AppCompatActivity {

    // Atributos
    private CarouselView carouselView;
    private TextView titulo;
    private TextView valor;
    private TextView localidade;
    private TextView descricao;
    private Servico servicoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        // Configuração da action bar TODO excluir linhas abaixo caso não funcione na tela para a tela de meus serviços
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes do serviço");

        // Inicializa os atributos com os componentes da interface necessários
        inicializarComponentes();

        // Recupera o serviço para a exibição
        servicoSelecionado = (Servico) getIntent().getSerializableExtra("servicoSelecionado");
        if (servicoSelecionado != null){
            // Preenche os campos da interface com as informações
            titulo.setText(servicoSelecionado.getNomeServico());
            valor.setText(servicoSelecionado.getValor());
            localidade.setText(servicoSelecionado.getLocalidade());
            descricao.setText(servicoSelecionado.getDescricao());

            // Configuração da exibição das imagens no carousel
            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = servicoSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };
            // Define a quantidade de paginas no carousel
            carouselView.setPageCount(servicoSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    // Inicializa os atributos com os componentes da interface necessários
    private void inicializarComponentes(){
        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textView_detalheTitulo);
        valor = findViewById(R.id.textView_detalheValor);
        localidade = findViewById(R.id.textView_detalheLocalidade);
        descricao = findViewById(R.id.textView_detalheDescricao);
    }

    public void visualizarTelefone(View view){
//        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ));
    }
}