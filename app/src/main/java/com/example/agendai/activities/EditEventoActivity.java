package com.example.agendai.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class EditEventoActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextContato, editTextData;
    private Spinner spinnerHoraInicio, spinnerHoraTermino, spinnerEspaco;
    private Button buttonSalvar, buttonLimpar;
    private RadioGroup radioGroupPagamento;
    private RadioButton radio50, radio100;
    private EventoDAO eventoDAO;
    private EspacoDAO espacoDAO;
    private ArrayAdapter<Espaco> espacoAdapter;
    private ArrayAdapter<String> horaAdapter;
    private String[] horarioOptions;
    private int position;
    private Evento eventoAntigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_evento);
        setTitle("Editar Evento");

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
        radio50 = findViewById(R.id.radio50);
        radio100 = findViewById(R.id.radio100);

        eventoDAO = new EventoDAO(this);
        eventoDAO.carregaLista(this);
        espacoDAO = new EspacoDAO();
        espacoDAO.carregaLista(this);

        List<Espaco> espacos = espacoDAO.getEspacos();
        espacoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, espacos);
        espacoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspaco.setAdapter(espacoAdapter);

        horarioOptions = getResources().getStringArray(R.array.horario_options);
        horaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horarioOptions);
        horaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHoraInicio.setAdapter(horaAdapter);
        spinnerHoraTermino.setAdapter(horaAdapter);

        // Recupera a posição do evento a ser editado, passada via Intent
        position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            eventoAntigo = eventoDAO.getEvento(position);
            if (eventoAntigo != null) {
                editTextTitulo.setText(eventoAntigo.getTitulo());
                editTextContato.setText(eventoAntigo.getContato());
                editTextData.setText(eventoAntigo.getData());

                int indexInicio = getIndexForValue(horarioOptions, eventoAntigo.getHoraInicio());
                int indexTermino = getIndexForValue(horarioOptions, eventoAntigo.getHoraTermino());
                spinnerHoraInicio.setSelection(indexInicio);
                spinnerHoraTermino.setSelection(indexTermino);

                int indexEspaco = getEspacoIndex(espacos, eventoAntigo.getLocal());
                spinnerEspaco.setSelection(indexEspaco);

                // Configura o RadioGroup conforme o valor de pagamento salvo
                if (eventoAntigo.getPagamento().equalsIgnoreCase("50%")) {
                    radioGroupPagamento.check(R.id.radio50);
                } else {
                    radioGroupPagamento.check(R.id.radio100);
                }
            } else {
                Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTextData.setOnClickListener(v -> showDatePicker(editTextData));

        buttonSalvar.setOnClickListener(v -> salvarEvento());
        buttonLimpar.setOnClickListener(v -> limparCampos());
    }

    private int getIndexForValue(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }

    private int getEspacoIndex(List<Espaco> espacos, String local) {
        for (int i = 0; i < espacos.size(); i++) {
            if (espacos.get(i).getNome().equalsIgnoreCase(local)) {
                return i;
            }
        }
        return 0;
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
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
        // Verifica conflito ignorando o evento atual (eventoAntigo)
        boolean conflito = eventoDAO.existeConflitoHorario(local, data, horaInicio, horaTermino, eventoAntigo);
        if (conflito) {
            Toast.makeText(this, "Já existe um evento nesse horário/local!", Toast.LENGTH_LONG).show();
            return;
        }

        // Cria o evento atualizado e preserva o mesmo ID do evento original
        Evento eventoAtualizado = new Evento(titulo, contato, data, horaInicio, horaTermino, local, pagamento, valorAluguel);
        eventoAtualizado.setId(eventoAntigo.getId());
        eventoDAO.altera(position, eventoAtualizado);
        eventoDAO.salvaLista(this);
        Toast.makeText(this, "Evento atualizado!", Toast.LENGTH_SHORT).show();
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
