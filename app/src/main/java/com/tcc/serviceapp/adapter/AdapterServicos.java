// Classe que define o layout/visualização dos itens "serviços" para listagem

package com.tcc.serviceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.model.Servico;

import java.util.List;

public class AdapterServicos extends RecyclerView.Adapter<AdapterServicos.MyViewHolder> {

    private List<Servico> servicos;
    private Context context;

    // Construtor
    public AdapterServicos(List<Servico> servicos, Context context) {
        this.servicos = servicos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_servico, parent, false);
        return new MyViewHolder(item);
    }

    // Edita os elementos da interface com as informações do serviço
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Servico servico = servicos.get(position);
        holder.tituloServico.setText(servico.getNomeServico());
        holder.valorServico.setText(servico.getValor());

        // Pega a primeira imagem da lista
        List<String> urlFotos = servico.getFotos();
        String urlCapa = urlFotos.get(0);

        // Módulo que carrega a imagem a partir da url
        Picasso.get().load(urlCapa).into(holder.fotoServico);
    }

    @Override
    public int getItemCount() {
        return servicos.size();
    }

    // Elementos contidos no recyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tituloServico;
        TextView valorServico;
        ImageView fotoServico;

        public MyViewHolder(View itemView){
            super(itemView);

            tituloServico = itemView.findViewById(R.id.textView_nomeServico);
            valorServico = itemView.findViewById(R.id.textView_valorServico);
            fotoServico = itemView.findViewById(R.id.imageView_servico);
        }
    }

}
