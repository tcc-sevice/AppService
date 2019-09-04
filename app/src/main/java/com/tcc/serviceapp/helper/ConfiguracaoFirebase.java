package com.tcc.serviceapp.helper;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaData;
    private static FirebaseAuth      referenciaAutenticacao;
    private static StorageReference  storageReference;

    public static DatabaseReference getReferenciaData(){
        if (referenciaData == null){
            referenciaData = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaData;
    }

    public static FirebaseAuth getReferenciaAutenticacao() {
        if (referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }

        return referenciaAutenticacao;
    }
        public static StorageReference getStorageReference(){
            if (storageReference == null){
                storageReference = FirebaseStorage.getInstance().getReference();
            }

            return storageReference;
    }
}
