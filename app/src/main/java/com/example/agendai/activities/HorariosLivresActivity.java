package com.example.agendai.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.agendai.R;
import com.example.agendai.dao.EspacoDAO;
import com.example.agendai.models.Espaco;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorariosLivresActivity extends AppCompatActivity {

    private EditText editTextDate;
    private Button buttonFiltrarHorarios;
    private LinearLayout layoutEspacos;
    private EspacoDAO espacoDAO;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios_livres);
        setTitle("Horários Livres");

        // Configura a Toolbar com botão de voltar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        editTextDate = findViewById(R.id.editTextDate);
        buttonFiltrarHorarios = findViewById(R.id.buttonFiltrarHorarios);
        layoutEspacos = findViewById(R.id.layoutEspacos);

        // Carrega a data atual por padrão
        selectedDate = new Date();
        setDateToEditText(selectedDate);

        // Ao clicar no campo de data, abre o DatePickerDialog
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Configura o DAO de espaços
        espacoDAO = new EspacoDAO();
        espacoDAO.carregaLista(this);

        // Se quiser já criar os botões ao abrir a tela:
        criarBotoesEspacos();

        // Se preferir criar/atualizar só quando o usuário clicar em "Filtrar Horários"
        buttonFiltrarHorarios.setOnClickListener(v -> {
            // Atualiza a selectedDate com o que está no EditText, se quiser
            // (Aqui estou usando a data do EditText caso precise converter, mas você pode usar 'selectedDate' já setada)
            criarBotoesEspacos();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newCal = Calendar.getInstance();
            newCal.set(year, month, dayOfMonth);
            selectedDate = newCal.getTime();
            setDateToEditText(selectedDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setDateToEditText(Date date) {
        // Formata a data como dd/MM/yyyy
        String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                date.getDate(), (date.getMonth() + 1), (date.getYear() + 1900));
        editTextDate.setText(dataFormatada);
    }

    private void criarBotoesEspacos() {
        // Limpa qualquer botão antigo
        layoutEspacos.removeAllViews();

        // Carrega a lista de espaços
        List<Espaco> listaEspacos = espacoDAO.getEspacos();
        if (listaEspacos.isEmpty()) {
            Toast.makeText(this, "Nenhum espaço cadastrado nas configurações.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pega a data do EditText (caso queira converter)
        String dataStr = editTextDate.getText().toString().trim();
        if (dataStr.isEmpty()) {
            Toast.makeText(this, "Selecione uma data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria dinamicamente um botão para cada espaço
        for (Espaco espaco : listaEspacos) {
            Button btn = new Button(this);
            btn.setText(espaco.getNome());
            // Configura o clique para abrir HorariosDisponiveisActivity
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(HorariosLivresActivity.this, HorariosDisponiveisActivity.class);
                intent.putExtra("area", espaco.getNome()); // "local" do espaço
                intent.putExtra("data", dataStr);          // data selecionada
                startActivity(intent);
            });
            layoutEspacos.addView(btn);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
