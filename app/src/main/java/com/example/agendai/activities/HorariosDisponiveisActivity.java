package com.example.agendai.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agendai.R;
import com.example.agendai.adapters.HorariosLivresAdapter;
import com.example.agendai.dao.EventoDAO;
import com.example.agendai.models.Evento;
import com.google.android.material.appbar.MaterialToolbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorariosDisponiveisActivity extends AppCompatActivity {

    private RecyclerView recyclerHorariosDisponiveis;
    private HorariosLivresAdapter adapter;
    private EventoDAO eventoDAO;
    private String selectedArea;
    private String selectedDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private String[] horariosOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios_disponiveis);
        setTitle("Horários Disponíveis");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerHorariosDisponiveis = findViewById(R.id.recyclerHorariosDisponiveis);
        recyclerHorariosDisponiveis.setLayoutManager(new LinearLayoutManager(this));

        eventoDAO = new EventoDAO(this);
        eventoDAO.carregaLista(this);
        horariosOptions = getResources().getStringArray(R.array.horario_options);

        selectedArea = getIntent().getStringExtra("area");
        selectedDate = getIntent().getStringExtra("data");

        if(selectedArea == null || selectedDate == null){
            Toast.makeText(this, "Dados não informados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        filtrarHorariosDisponiveis();
    }

    private void filtrarHorariosDisponiveis() {
        List<Evento> eventosAgendados = eventoDAO.getEventosPorData(selectedDate);
        List<String> horariosOcupados = new ArrayList<>();

        for (Evento evento : eventosAgendados) {
            if (evento.getLocal() != null && evento.getLocal().equalsIgnoreCase(selectedArea)) {
                try {
                    Date startTime = timeFormat.parse(evento.getHoraInicio());
                    Date endTime = timeFormat.parse(evento.getHoraTermino());
                    if (startTime != null && endTime != null) {
                        for (String horario : horariosOptions) {
                            Date optionTime = timeFormat.parse(horario);
                            if (optionTime != null) {
                                if (!optionTime.before(startTime) && optionTime.before(endTime)) {
                                    if (!horariosOcupados.contains(horario)) {
                                        horariosOcupados.add(horario);
                                    }
                                }
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> horariosDisponiveis = new ArrayList<>();
        for (String horario : horariosOptions) {
            if (!horariosOcupados.contains(horario)) {
                horariosDisponiveis.add(horario);
            }
        }

        if (horariosDisponiveis.isEmpty()) {
            Toast.makeText(this, "Nenhum horário disponível para " + selectedArea, Toast.LENGTH_SHORT).show();
        }

        adapter = new HorariosLivresAdapter(horariosDisponiveis);
        recyclerHorariosDisponiveis.setAdapter(adapter);
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
