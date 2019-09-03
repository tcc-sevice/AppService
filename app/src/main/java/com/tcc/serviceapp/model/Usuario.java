package com.tcc.serviceapp.model;

import java.util.Date;

public class Usuario {

    private String nome;
    private String sobrenome;
    private Float cpf;
    private Date dataNascimento;
    private String sexo;
    private String email;
    private String telefone;
    private String senha;
    private String confirmasSenha;
    private String caminhoFotoPerfil;

    public Usuario() {
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

    public Float getCpf() {
        return cpf;
    }

    public void setCpf(Float cpf) {
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmasSenha() {
        return confirmasSenha;
    }

    public void setConfirmasSenha(String confirmasSenha) {
        this.confirmasSenha = confirmasSenha;
    }

    public String getCaminhoFotoPerfil() {
        return caminhoFotoPerfil;
    }

    public void setCaminhoFotoPerfil(String caminhoFotoPerfil) {
        this.caminhoFotoPerfil = caminhoFotoPerfil;
    }
}
