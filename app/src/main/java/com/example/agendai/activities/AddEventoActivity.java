package com.example.agendai.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agendai.R;
import com.example.agendai.dao.EspacoDAO;
import com.example.agendai.dao.EventoDAO;
import com.example.agendai.models.Espaco;
import com.example.agendai.models.Evento;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventoActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextContato, editTextData;
    private Spinner spinnerHoraInicio, spinnerHoraTermino, spinnerEspaco;
    private Button buttonSalvar, buttonLimpar;
    private RadioGroup radioGroupPagamento;
    private EventoDAO eventoDAO;
    private EspacoDAO espacoDAO;
    private ArrayAdapter<Espaco> espacoAdapter;
    private ArrayAdapter<String> horaAdapter;
    private String[] horarioOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);
        setTitle("Adicionar Evento");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Vinculação dos componentes
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextContato = findViewById(R.id.editTextContato);
        editTextData = findViewById(R.id.editTextData);
        spinnerHoraInicio = findViewById(R.id.spinnerHoraInicio);
        spinnerHoraTermino = findViewById(R.id.spinnerHoraTermino);
        spinnerEspaco = findViewById(R.id.spinnerEspaco);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        buttonLimpar = findViewById(R.id.buttonLimpar);
        radioGroupPagamento = findViewById(R.id.radioGroupPagamento);

        eventoDAO = new EventoDAO(this);
        espacoDAO = new EspacoDAO();
        espacoDAO.carregaLista(this);
        List<Espaco> espacos = espacoDAO.getEspacos();
        espacoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, espacos);
        espacoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspaco.setAdapter(espacoAdapter);

        // Carrega os horários predefinidos
        horarioOptions = getResources().getStringArray(R.array.horario_options);
        horaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horarioOptions);
        horaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHoraInicio.setAdapter(horaAdapter);
        spinnerHoraTermino.setAdapter(horaAdapter);

        String dataRecebida = getIntent().getStringExtra("data");
        if (dataRecebida != null) {
            editTextData.setText(dataRecebida);
        }

        editTextData.setOnClickListener(v -> showDatePicker(editTextData));

        buttonSalvar.setOnClickListener(v -> salvarEvento());
        buttonLimpar.setOnClickListener(v -> limparCampos());
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                    dayOfMonth, month + 1, year);
            editText.setText(dataFormatada);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void salvarEvento() {
        String titulo = editTextTitulo.getText().toString().trim();
        String contato = editTextContato.getText().toString().trim();
        String data = editTextData.getText().toString().trim();
        String horaInicio = spinnerHoraInicio.getSelectedItem().toString();
        String horaTermino = spinnerHoraTermino.getSelectedItem().toString();

        if (titulo.isEmpty() || contato.isEmpty() || data.isEmpty() ||
                horaInicio.isEmpty() || horaTermino.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Espaco espacoSelecionado = (Espaco) spinnerEspaco.getSelectedItem();
        if (espacoSelecionado == null) {
            Toast.makeText(this, "Selecione um espaço", Toast.LENGTH_SHORT).show();
            return;
        }
        String local = espacoSelecionado.getNome();
        double valorAluguel = espacoSelecionado.getValorPorHora();

        // Obtém o valor do pagamento a partir do RadioGroup
        String pagamento;
        int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
        if (selectedId == R.id.radio50) {
            pagamento = "50%";
        } else {
            pagamento = "100%";
        }

        eventoDAO.carregaLista(this);
        boolean conflito = eventoDAO.existeConflitoHorario(local, data, horaInicio, horaTermino);
        if (conflito) {
            Toast.makeText(this, "Já existe um evento nesse horário/local!", Toast.LENGTH_LONG).show();
            return;
        }

        Evento evento = new Evento(titulo, contato, data, horaInicio, horaTermino, local, pagamento, valorAluguel);
        eventoDAO.adiciona(evento);
        eventoDAO.salvaLista(this);
        Toast.makeText(this, "Evento salvo!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void limparCampos() {
        editTextTitulo.setText("");
        editTextContato.setText("");
        editTextData.setText("");
        spinnerHoraInicio.setSelection(0);
        spinnerHoraTermino.setSelection(0);
        spinnerEspaco.setSelection(0);
        radioGroupPagamento.clearCheck();
    }
}
