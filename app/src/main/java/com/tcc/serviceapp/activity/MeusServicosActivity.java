package com.tcc.serviceapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

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
import com.tcc.serviceapp.helper.RecyclerItemClickListener;
import com.tcc.serviceapp.model.Servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewServicos;
    private List<Servico> servicos = new ArrayList<>();
    private AdapterServicos adapterServicos;
    private DatabaseReference servicoUsuarioRef;
    private AlertDialog dialog;

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

        // Recupera os serviços cadastrados pelo usuário conectado
        recuperarServicos();

        // Adiciona evento de clique no recyclerView
        recyclerViewServicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerViewServicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                            }

                            // Chamado caso o usuario mantenha o item pressionado
                            @Override
                            public void onLongItemClick(View view, int position) {
                                // Dialog de confirmação de exclusão
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MeusServicosActivity.this);
                                builder.setTitle("Exclusão de serviço");
                                builder.setMessage("Tem certeza que deseja excluir o serviço selecionado ?");
                                builder.setCancelable(true);
                                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Objeto recebe o serviço da lista
                                        Servico servicoSelecionado = servicos.get(position);
                                        // Faz a exclusão
                                        servicoSelecionado.removerServico();
                                        adapterServicos.notifyDataSetChanged();
                                    }
                                });

                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        }
                )
        );
    }

    // Recupera informações de serviços do usuário conectado
    private void recuperarServicos(){

        // Dialog de progresso do carregamento. Executa até receber o método dismiss()
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando seus serviços")
                .setCancelable(false)
                .build();
        dialog.show();

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

                // Interrompe o dialog de progresso
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
