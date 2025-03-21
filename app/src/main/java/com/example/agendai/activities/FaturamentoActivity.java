package com.example.agendai.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agendai.R;
import com.example.agendai.dao.EventoDAO;
import com.example.agendai.models.Evento;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FaturamentoActivity extends AppCompatActivity {

    private EventoDAO eventoDAO;
    private TextView textViewFaturamento, textViewAreaMaisUsada;
    private EditText editTextFilterDate;
    private Spinner spinnerFilter;
    private Button buttonAplicarFiltro;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faturamento);
        setTitle("Faturamento");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerFilter = findViewById(R.id.spinnerFilter);
        editTextFilterDate = findViewById(R.id.editTextFilterDate);
        buttonAplicarFiltro = findViewById(R.id.buttonAplicarFiltro);
        textViewFaturamento = findViewById(R.id.textViewFaturamento);
        textViewAreaMaisUsada = findViewById(R.id.textViewAreaMaisUsada);

        // Define a data atual como padrão
        selectedDate = new Date();
        editTextFilterDate.setText(dateFormat.format(selectedDate));

        // Configura o DatePicker para o EditText de data
        editTextFilterDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            new DatePickerDialog(FaturamentoActivity.this, (view, year, month, dayOfMonth) -> {
                Calendar newCal = Calendar.getInstance();
                newCal.set(year, month, dayOfMonth);
                selectedDate = newCal.getTime();
                editTextFilterDate.setText(dateFormat.format(selectedDate));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        eventoDAO = new EventoDAO(this);
        eventoDAO.carregaLista(this);

        buttonAplicarFiltro.setOnClickListener(v -> gerarRelatorio());
    }

    private void gerarRelatorio() {
        String filtro = spinnerFilter.getSelectedItem().toString();
        String dataSelecionada = editTextFilterDate.getText().toString().trim();
        if (dataSelecionada.isEmpty()) {
            Toast.makeText(this, "Selecione uma data", Toast.LENGTH_SHORT).show();
            return;
        }

        eventoDAO.carregaLista(this);
        List<Evento> todosEventos = eventoDAO.todos();
        List<Evento> eventosFiltrados = new ArrayList<>();

        for (Evento evento : todosEventos) {
            if (evento.getData() == null) continue;
            if (filtro.equalsIgnoreCase("Dia")) {
                if (evento.getData().equals(dataSelecionada)) {
                    eventosFiltrados.add(evento);
                }
            } else if (filtro.equalsIgnoreCase("Mês")) {
                String mesAnoEvento = evento.getData().substring(3);
                String mesAnoSelecionado = dataSelecionada.substring(3);
                if (mesAnoEvento.equals(mesAnoSelecionado)) {
                    eventosFiltrados.add(evento);
                }
            }
        }

        double totalFaturamento = 0.0;
        HashMap<String, Double> areaUsage = new HashMap<>();

        for (Evento evento : eventosFiltrados) {
            if (evento.getHoraInicio() == null || evento.getHoraTermino() == null ||
                    evento.getHoraInicio().trim().isEmpty() || evento.getHoraTermino().trim().isEmpty()) {
                continue;
            }
            try {
                Date startTime = timeFormat.parse(evento.getHoraInicio());
                Date endTime = timeFormat.parse(evento.getHoraTermino());
                if (startTime != null && endTime != null) {
                    long diffMillis = endTime.getTime() - startTime.getTime();
                    double diffHours = diffMillis / 3600000.0;
                    // Multiplica por 0.5 se o pagamento for 50%, caso contrário 1.0
                    double rateMultiplier = evento.getPagamento().equalsIgnoreCase("50%") ? 0.5 : 1.0;
                    totalFaturamento += diffHours * evento.getValorAluguel() * rateMultiplier;

                    String local = evento.getLocal();
                    if (local != null && !local.trim().isEmpty()) {
                        double currentUsage = areaUsage.containsKey(local) ? areaUsage.get(local) : 0.0;
                        areaUsage.put(local, currentUsage + diffHours);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao calcular horário para um evento.", Toast.LENGTH_SHORT).show();
            }
        }

        textViewFaturamento.setText(String.format(Locale.getDefault(), "Faturamento: R$ %.2f", totalFaturamento));

        String areaMaisUsada = null;
        double maxUsage = 0.0;
        for (Map.Entry<String, Double> entry : areaUsage.entrySet()) {
            if (entry.getValue() > maxUsage) {
                maxUsage = entry.getValue();
                areaMaisUsada = entry.getKey();
            }
        }
        if (areaMaisUsada != null) {
            textViewAreaMaisUsada.setText(String.format(Locale.getDefault(), "Área mais usada: %s (%.1fh)", areaMaisUsada, maxUsage));
        } else {
            textViewAreaMaisUsada.setText("Área mais usada: -");
        }
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
