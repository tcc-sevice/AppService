package com.tcc.serviceapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.List;

public class Servico implements Serializable {

    //Atributos
    private String idServico;
    private String localidade;
    private String categoria;
    private String nomeServico;
    private String valor;
    private String descricao;
    private List<String> fotos;
    private String nomeUsuario;
    private String urlFotoUsuario;
    private String telUsuario;

    // Construtor
    public Servico() {
        // Gera um ID para o serviço cadastrado que será a identificação no Firebase
        DatabaseReference servicoRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_cadastrados");
        setIdServico(servicoRef.push().getKey());
    }

    // Salva os dados do serviço no banco de dados do Firebase
    public void salvarServico(){

        // Recebe o ID do usuário conectado
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        // Referencia do banco de dados
        DatabaseReference servicoRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_cadastrados");

        // Atribui os valores
        servicoRef.child(idUsuario)
                .child(getIdServico())
                .setValue(this);

        salvarServicoPublico();
    }
    // Salva os dados do serviços em um outro nó para utilizar na função de filtragem
    public void salvarServicoPublico(){

        // Referencia do banco de dados
        DatabaseReference servicoRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_publicos");

        // Atribui os valores
        servicoRef.child(getLocalidade())
                  .child(getCategoria())
                  .child(getIdServico())
                  .setValue(this);
    }

    // Exclui um serviços selecionado pelo usuário
    public void removerServico(){
        // Recebe o ID do usuário conectado
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        // Referencia do banco de dados
        DatabaseReference servicoRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_cadastrados")
                .child(idUsuario)
                .child(getIdServico());

        // Exclui os dados no nó referenciado
        servicoRef.removeValue();

        removerServicoPublico();
        removerFotosStorage();
    }
    // Exclui um serviços selecionado pelo usuário
    public void removerServicoPublico(){
        // Referencia do banco de dados
        DatabaseReference servicoRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_publicos")
                .child(getLocalidade())
                .child(getCategoria())
                .child(getIdServico());

        // Exclui os dados no nó referenciado
        servicoRef.removeValue();
    }
    // Exclui fotos do serviço no Storage
    public void removerFotosStorage(){
        List<String> fotos = getFotos();
        for (int i = 0; i < fotos.size(); i++){
            StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage()
                    .child("Imagens")
                    .child("Servicos")
                    .child(getIdServico())
                    .child("imagemServico"+i);
            storageReference.delete();
        }
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

    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getUrlFotoUsuario() {
        return urlFotoUsuario;
    }
    public void setUrlFotoUsuario(String urlFotoUsuario) {
        this.urlFotoUsuario = urlFotoUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }
    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }
}
