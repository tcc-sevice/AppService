package com.tcc.serviceapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tcc.serviceapp.R;
import com.tcc.serviceapp.adapter.AdapterServicos;
import com.tcc.serviceapp.helper.ConfiguracaoFirebase;
import com.tcc.serviceapp.model.Servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewServicos;
    private List<Servico> servicos = new ArrayList<>();
    private AdapterServicos adapterServicos;

    private DatabaseReference servicoUsuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_servicos);

        // Configurações iniciais
        servicoUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_cadastrados")
                .child(ConfiguracaoFirebase.getIdUsuario());
        recyclerViewServicos = findViewById(R.id.recyclerViewServicos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO metodo inicializarComponentes(), se necessario mais atributos

        // Método de manipulação do botão de "voltar" no canto da tela
        // fab == Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Direciona para outra tela
                startActivity(new Intent(getApplicationContext(), CadastroServicoActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurações do RecyclerView
        recyclerViewServicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewServicos.setHasFixedSize(true);
        adapterServicos = new AdapterServicos(servicos, this);
        recyclerViewServicos.setAdapter(adapterServicos);

        recuperarServicos();
    }

    // Recupera informações de serviços do usuário conectado
    private void recuperarServicos(){
        servicoUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpa a lista
                servicos.clear();
                // Adiciona os serviços salvos no Firebase à lista
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    servicos.add(ds.getValue(Servico.class));
                }

                Collections.reverse(servicos);
                adapterServicos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
