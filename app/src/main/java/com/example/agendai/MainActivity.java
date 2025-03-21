package com.example.agendai;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agendai.activities.AddEventoActivity;
import com.example.agendai.activities.ConfigActivity;
import com.example.agendai.activities.EditEventoActivity;
import com.example.agendai.activities.FaturamentoActivity;
import com.example.agendai.activities.HorariosLivresActivity;
import com.example.agendai.adapters.EventoAdapter;
import com.example.agendai.dao.EventoDAO;
import com.example.agendai.models.Evento;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private CalendarView calendarView;
    private RecyclerView recyclerEventos;
    private SpeedDialView speedDial;
    private LinearLayout layoutNoEvents;
    private EventoDAO eventoDAO;
    private EventoAdapter eventoAdapter;
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Agendai");

        // Inicializa DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);

        // Configura a Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura o ActionBarDrawerToggle (Navigation Drawer)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializa demais views
        calendarView = findViewById(R.id.calendarView);
        recyclerEventos = findViewById(R.id.recyclerEventos);
        speedDial = findViewById(R.id.speedDial);
        layoutNoEvents = findViewById(R.id.layoutNoEvents);

        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));
        eventoDAO = new EventoDAO(this);
        selectedDate = dateFormat.format(new java.util.Date());

        // Listener do CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            carregarEventos();
        });

        // Configura o ícone principal do Speed Dial para "ic_speeddial"
        speedDial.setMainFabClosedDrawable(ContextCompat.getDrawable(this, R.drawable.ic_speeddial));
        // Configura o Speed Dial com duas ações: Adicionar Evento e Buscar por Nome
        speedDial.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_event, R.drawable.ic_add_event)
                .setLabel("Adicionar Evento")
                .setFabBackgroundColor(getResources().getColor(R.color.blue_500))
                .create());
        speedDial.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_action2, R.drawable.ic_busca)
                .setLabel("Buscar por Nome")
                .setFabBackgroundColor(getResources().getColor(R.color.orange_500))
                .create());
        speedDial.setUseReverseAnimationOnClose(true);
        speedDial.setOnActionSelectedListener(actionItem -> {
            if (actionItem.getId() == R.id.fab_add_event) {
                Intent intent = new Intent(MainActivity.this, AddEventoActivity.class);
                intent.putExtra("data", selectedDate);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return false;
            } else if (actionItem.getId() == R.id.fab_action2) {
                showSearchDialog();
                return false;
            }
            return false;
        });

        carregarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEventos();
    }

    private void carregarEventos() {
        eventoDAO.carregaLista(this);
        List<Evento> eventos = eventoDAO.getEventosPorData(selectedDate);
        if (eventos.isEmpty()) {
            layoutNoEvents.setVisibility(LinearLayout.VISIBLE);
            recyclerEventos.setVisibility(RecyclerView.GONE);
        } else {
            layoutNoEvents.setVisibility(LinearLayout.GONE);
            recyclerEventos.setVisibility(RecyclerView.VISIBLE);
            eventoAdapter = new EventoAdapter(eventos, this);
            eventoAdapter.setOnItemClickListener(evento -> {
                int pos = eventoDAO.todos().indexOf(evento);
                if (pos != -1) {
                    Intent intent = new Intent(MainActivity.this, EditEventoActivity.class);
                    intent.putExtra("position", pos);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(MainActivity.this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
                }
            });
            eventoAdapter.setOnItemLongClickListener(evento -> {
                showLongClickOptions(evento);
            });
            recyclerEventos.setAdapter(eventoAdapter);
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar Evento");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String query = input.getText().toString().trim();
            if (!query.isEmpty()) {
                buscarEventos(query);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void buscarEventos(String query) {
        List<Evento> allEvents = eventoDAO.todos();
        List<Evento> filtered = new ArrayList<>();
        for (Evento evento : allEvents) {
            if (evento.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(evento);
            }
        }
        if (filtered.isEmpty()) {
            Toast.makeText(MainActivity.this, "Nenhum evento encontrado para \"" + query + "\"", Toast.LENGTH_SHORT).show();
        } else {
            eventoAdapter = new EventoAdapter(filtered, this);
            eventoAdapter.setOnItemClickListener(evento -> {
                int pos = eventoDAO.todos().indexOf(evento);
                if (pos != -1) {
                    Intent intent = new Intent(MainActivity.this, EditEventoActivity.class);
                    intent.putExtra("position", pos);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(MainActivity.this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
                }
            });
            eventoAdapter.setOnItemLongClickListener(evento -> {
                showLongClickOptions(evento);
            });
            recyclerEventos.setAdapter(eventoAdapter);
        }
    }

    private void showLongClickOptions(Evento evento) {
        String[] options = {"Excluir", "Marcar como Concluído"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções");
        builder.setItems(options, (dialog, which) -> {
            int pos = eventoDAO.todos().indexOf(evento);
            if (which == 0) { // Excluir
                if (pos != -1) {
                    eventoDAO.remove(pos);
                    eventoDAO.salvaLista(this);
                    carregarEventos();
                    Toast.makeText(MainActivity.this, "Evento excluído", Toast.LENGTH_SHORT).show();
                }
            } else if (which == 1) { // Marcar como Concluído
                if (pos != -1) {
                    evento.setConcluido(true);
                    eventoDAO.altera(pos, evento);
                    eventoDAO.salvaLista(this);
                    carregarEventos();
                    Toast.makeText(MainActivity.this, "Evento marcado como concluído", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_configuracoes) {
            Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_horarios_livres) {
            Intent intent = new Intent(MainActivity.this, HorariosLivresActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_faturamento) {
            Intent intent = new Intent(MainActivity.this, FaturamentoActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
