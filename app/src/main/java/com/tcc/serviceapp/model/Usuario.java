package com.tcc.serviceapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.Date;

public class Usuario implements Serializable {

    // Atributos
    private String id;
    private String nome;
    private String sobrenome;
    private String cpf;
    private Date   dataNascimento;
    private String sexo;
    private String email;
    private String telefone;
    private String senha;
    private String confirmaSenha;
    private String idFoto;

    public Usuario() {
    }

    // Salva dados de usuario no banco de dados
    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        usuariosRef.setValue(this);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public String getConfirmaSenha() {
        return confirmaSenha;
    }
    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public String getIdFoto() {
        return idFoto;
    }
    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

}
