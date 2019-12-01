package com.tcc.serviceapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    //Atributos
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerView;
    private Button button_localidade, button_categoria; //TODO excluir linha?
    private AdapterServicos adapterServicos;
    private List<Servico> listaServicos = new ArrayList<>();
    private DatabaseReference servicosPublicosRef;
    private AlertDialog dialog;
    private String filtroLocalidade = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurações dialog de progresso
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Aguarde")
                .setCancelable(false)
                .build();

        // Configuração inicial do objeto de autenticação do Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        servicosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_publicos");

        // Configurações do RecyclerView
        recyclerView = findViewById(R.id.recyclerView_servicosPublicos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterServicos = new AdapterServicos(listaServicos, this);
        recyclerView.setAdapter(adapterServicos);

        // Recupera os dados do nó "servicos_publicos" no banco de dados
        recuperarServicosPublicos();
    }

    // Recupera os dados do nó "servicos_publicos" no banco de dados
    public void recuperarServicosPublicos(){
        // Dialog de progresso do carregamento. Executa até receber o método dismiss()
        dialog.show();

        listaServicos.clear();
        servicosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Percorre cada nó do banco de dados, iterando cada "filho"
                for (DataSnapshot localidades: dataSnapshot.getChildren()){
                    for (DataSnapshot categorias: localidades.getChildren()){
                        for (DataSnapshot servicos: categorias.getChildren()){
                            // Atribui o serviço recuperado a um objeto Serviço
                            Servico servico = servicos.getValue(Servico.class);
                            // Adiciona serviços a uma lista
                            listaServicos.add(servico);

                        }
                    }
                }

                Collections.reverse(listaServicos);
                adapterServicos.notifyDataSetChanged();

                // Interrompe o dialog de progresso
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Chamado pelo botão da interface de filtragem por localidade
    public void filtrarPorLocalidade(View view){
        // Configurações do dialog para seleção de local
        AlertDialog.Builder dialogLocalidade = new AlertDialog.Builder(this);
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        // Configuração spinner de locais
        Spinner spinnerLocalidade = viewSpinner.findViewById(R.id.spinner_filtro);
        String[] cidades = getResources().getStringArray(R.array.localidade);
        ArrayAdapter<String> adapterLocalidade = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, cidades);
        adapterLocalidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalidade.setAdapter(adapterLocalidade);

        dialogLocalidade.setTitle("Selecione a opção desejada:");
        dialogLocalidade.setView(viewSpinner);
        // Ao confirmar a dialog
        dialogLocalidade.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Recupera o item do spinner ao clicar em OK
                filtroLocalidade = spinnerLocalidade.getSelectedItem().toString();
                recuperarServicosPorLocalidade();
            }
        });
        // Ao cancelar a dialog
        dialogLocalidade.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog dialog = dialogLocalidade.create();
        dialog.show();
    }
    // Mostra apenas os serviços que forem filtrados por localidade
    public void recuperarServicosPorLocalidade(){
        // Dialog de progresso do carregamento. Executa até receber o método dismiss()
        dialog.show();

        servicosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("servicos_publicos")
                .child(filtroLocalidade);

        servicosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaServicos.clear();
                for (DataSnapshot categorias: dataSnapshot.getChildren()){
                    for (DataSnapshot servicos: categorias.getChildren()){
                        // Atribui o serviço recuperado a um objeto Serviço
                        Servico servico = servicos.getValue(Servico.class);
                        // Adiciona serviços a uma lista
                        listaServicos.add(servico);
                    }
                }

                Collections.reverse(listaServicos);
                adapterServicos.notifyDataSetChanged();

                // Interrompe o dialog de progresso
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Carrega os menus do actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // Manipula estados dos menus do actionBar
    // (chamado antes do menu ser exibido, toda vez que a activity é carregada)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Verifica se o usuário está logado e a partir disso, mostra o menu correspondente
        if (autenticacao.getCurrentUser() == null){
            menu.setGroupVisible(R.id.group_deslogado, true);
        }
        else {
            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    // Manipula as funções dos menus do actionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_login:
                // Direciona para outra tela
                startActivity(new Intent(getApplicationContext(), LoginActiviy.class));
                break;

            case R.id.menu_servicos:
                // Direciona para outra tela
                startActivity(new Intent(getApplicationContext(), MeusServicosActivity.class));
                break;

            case R.id.menu_sair:
                // Método de desligamento de usuário do Firebase
                autenticacao.signOut();
                // Mensagem momentânea no rodapé
                Toast.makeText(MainActivity.this,
                        "Deslogado !",
                        Toast.LENGTH_SHORT).show();
                // Invalida o método onCreateOptionsMenu para atualizar a exibição dos menus
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
