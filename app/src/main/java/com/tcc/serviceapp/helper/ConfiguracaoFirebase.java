package com.tcc.serviceapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaData;
    private static FirebaseAuth      referenciaAutenticacao;
    private static StorageReference referenciaStorage;

    // Retorna referencia do banco de dados do Firebase
    public static DatabaseReference getReferenciaData(){
        if (referenciaData == null){
            referenciaData = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaData;
    }

    // Retorna intancia do serviço de autenticação do Firebase
    public static FirebaseAuth getReferenciaAutenticacao() {
        if (referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return referenciaAutenticacao;
    }

    // Retorna intancia do banco de dados do Firebase
    public static StorageReference getReferenciaStorage(){
        if (referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }
}
