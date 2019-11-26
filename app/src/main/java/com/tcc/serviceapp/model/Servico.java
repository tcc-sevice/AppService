package com.tcc.serviceapp.model;

import com.google.firebase.database.DatabaseReference;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

import java.util.List;

public class Servico {

    //Atributos
    private String idServico;
    private String localidade;
    private String categoria;
    private String nomeServico;
    private String valor;
    private String descricao;
    private List<String> fotos;

    // Construtor
    public Servico() {
        // Gera um ID para o serviço cadastrado que será a identificação no Firebase
        DatabaseReference servicoRef = ConfiguracaoFirebase.getReferenciaData()
                .child("meus_servicos");
        setIdServico(servicoRef.push().getKey());
    }

    public String getIdServico() {
        return idServico;
    }
    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }

    public String getLocalidade() {
        return localidade;
    }
    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNomeServico() {
        return nomeServico;
    }
    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }
    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
