package com.tcc.serviceapp.model;

import android.net.Uri;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;

import java.util.Date;

public class Usuario {

    private String id;
    private String nome;
    private String sobrenome;
    private String cpf;
    private Date   dataNascimento;
    private String sexo;
    private String email;
    private String telefone;
    private String senha;
    private String confirmasSenha;
    private String idFoto;
    private Uri    caminhoFotoPerfil = null;

    public Usuario() {
    }
    public void Salvar(Usuario usuario){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaData();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        usuariosRef.setValue(usuario);
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
    public String getConfirmasSenha() {
        return confirmasSenha;
    }

    public void setConfirmasSenha(String confirmasSenha) {
        this.confirmasSenha = confirmasSenha;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public Uri getCaminhoFotoPerfil() {
        return caminhoFotoPerfil;
    }

    public void setCaminhoFotoPerfil(Uri caminhoFotoPerfil) {
        this.caminhoFotoPerfil = caminhoFotoPerfil;
    }
}
