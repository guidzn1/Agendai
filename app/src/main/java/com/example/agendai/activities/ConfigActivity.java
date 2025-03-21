package com.example.agendai.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agendai.R;
import com.example.agendai.adapters.EspacosAdapter;
import com.example.agendai.dao.EspacoDAO;
import com.example.agendai.models.Espaco;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ConfigActivity extends AppCompatActivity {

    private EditText editTextNomeEspaco, editTextValorHora;
    private Button buttonSalvarEspaco;
    private RecyclerView recyclerEspacos;
    private EspacoDAO espacoDAO;
    private EspacosAdapter espacosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setTitle("Configuração de Espaços");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextNomeEspaco = findViewById(R.id.editTextNomeEspaco);
        editTextValorHora = findViewById(R.id.editTextValorHora);
        buttonSalvarEspaco = findViewById(R.id.buttonSalvarEspaco);
        recyclerEspacos = findViewById(R.id.recyclerEspacos);

        espacoDAO = new EspacoDAO();
        espacoDAO.carregaLista(this);
        recyclerEspacos.setLayoutManager(new LinearLayoutManager(this));
        atualizarLista();

        buttonSalvarEspaco.setOnClickListener(v -> {
            String nome = editTextNomeEspaco.getText().toString().trim();
            String valorStr = editTextValorHora.getText().toString().trim();
            if(nome.isEmpty() || valorStr.isEmpty()){
                Toast.makeText(ConfigActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            double valor;
            try {
                valor = Double.parseDouble(valorStr);
            } catch(NumberFormatException e){
                Toast.makeText(ConfigActivity.this, "Valor inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            Espaco espaco = new Espaco(nome, valor);
            espacoDAO.adiciona(espaco);
            espacoDAO.salvaLista(ConfigActivity.this);
            Toast.makeText(ConfigActivity.this, "Espaço adicionado", Toast.LENGTH_SHORT).show();
            atualizarLista();
            editTextNomeEspaco.setText("");
            editTextValorHora.setText("");
        });
    }

    private void atualizarLista(){
        List<Espaco> espacos = espacoDAO.getEspacos();
        espacosAdapter = new EspacosAdapter(espacos);
        espacosAdapter.setOnEditClickListener((espaco, position) -> {
            showEditDialog(espaco, position);
        });
        espacosAdapter.setOnDeleteClickListener((espaco, position) -> {
            showDeleteDialog(espaco, position);
        });
        recyclerEspacos.setAdapter(espacosAdapter);
    }

    private void showEditDialog(Espaco espaco, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Espaço");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputNome = new EditText(this);
        inputNome.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNome.setHint("Nome do Espaço");
        inputNome.setText(espaco.getNome());
        layout.addView(inputNome);

        final EditText inputValor = new EditText(this);
        inputValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputValor.setHint("Valor por Hora");
        inputValor.setText(String.valueOf(espaco.getValorPorHora()));
        layout.addView(inputValor);

        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = inputNome.getText().toString().trim();
            String novoValorStr = inputValor.getText().toString().trim();
            if(novoNome.isEmpty() || novoValorStr.isEmpty()){
                Toast.makeText(ConfigActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            double novoValor;
            try {
                novoValor = Double.parseDouble(novoValorStr);
            } catch(NumberFormatException e){
                Toast.makeText(ConfigActivity.this, "Valor inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            espaco.setNome(novoNome);
            espaco.setValorPorHora(novoValor);
            espacoDAO.altera(position, espaco);
            espacoDAO.salvaLista(ConfigActivity.this);
            atualizarLista();
            Toast.makeText(ConfigActivity.this, "Espaço atualizado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteDialog(Espaco espaco, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Espaço");
        builder.setMessage("Deseja realmente excluir o espaço \"" + espaco.getNome() + "\"?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            espacoDAO.remove(position);
            espacoDAO.salvaLista(ConfigActivity.this);
            atualizarLista();
            Toast.makeText(ConfigActivity.this, "Espaço excluído", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
