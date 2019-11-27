package com.tcc.serviceapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaData;
    private static FirebaseAuth      referenciaAutenticacao;
    private static StorageReference  referenciaStorage;

    // Retorna o ID do usuário conectado
    public static String getIdUsuario(){
        FirebaseAuth autenticacao = getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }

    // Retorna referência do banco de dados do Firebase (Realtime Database)
    public static DatabaseReference getFirebase(){
        if (referenciaData == null){
            referenciaData = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaData;
    }

    // Retorna referência do serviço de autenticação do Firebase
    public static FirebaseAuth getFirebaseAutenticacao(){
        if (referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return referenciaAutenticacao;
    }

    // Retorna referência do banco de dados do Firebase
    public static StorageReference getFirebaseStorage(){
        if (referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }
}
