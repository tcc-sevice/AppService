package com.tcc.serviceapp.model;

import com.google.firebase.database.DatabaseReference;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

public class Endereco {

    private String  id;
    private String  cidade;
    private String  bairro;
    private String  rua;
    private Integer numero;
    private String  cep;
    private String  complemento;

    public void Salvar(Endereco endereco){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference enderecoRef = firebaseRef.child("Endereco").child("Id");
        enderecoRef.setValue(endereco);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
