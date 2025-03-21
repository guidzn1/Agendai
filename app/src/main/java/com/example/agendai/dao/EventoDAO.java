package com.example.agendai.dao;

import android.content.Context;
import com.example.agendai.models.Evento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventoDAO {

    private static List<Evento> listaEventos = new ArrayList<>();
    private final String ARQUIVO_BD = "eventos.json";
    private Gson gson = new Gson();

    public EventoDAO(Context context) {
        // Opcional: carregar a lista aqui, se desejar.
    }

    public void carregaLista(Context context) {
        try {
            FileReader reader = new FileReader(context.getFilesDir() + "/" + ARQUIVO_BD);
            Type type = new TypeToken<Evento[]>() {}.getType();
            Evento[] eventosArray = gson.fromJson(reader, type);
            if (eventosArray != null) {
                listaEventos = new ArrayList<>(Arrays.asList(eventosArray));
            } else {
                listaEventos = new ArrayList<>();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            listaEventos = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvaLista(Context context) {
        try {
            FileWriter writer = new FileWriter(context.getFilesDir() + "/" + ARQUIVO_BD);
            String json = gson.toJson(listaEventos);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Evento> todos() {
        return new ArrayList<>(listaEventos);
    }

    public Evento getEvento(int position) {
        return listaEventos.get(position);
    }

    // Método para filtrar eventos por data (formato "dd/MM/yyyy")
    public List<Evento> getEventosPorData(String data) {
        List<Evento> filtrados = new ArrayList<>();
        for (Evento e : listaEventos) {
            if (e.getData() != null && e.getData().equals(data)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    // Método para verificar conflito, ignorando o evento passado (quando em edição)
    public boolean existeConflitoHorario(String local, String data, String horaInicio, String horaTermino, Evento ignorar) {
        for (Evento e : listaEventos) {
            if (ignorar != null && e.getId() == ignorar.getId()) {
                continue;
            }
            if (e.getData().equals(data) && e.getLocal().equalsIgnoreCase(local)) {
                int inicioNovo = toMinutes(horaInicio);
                int terminoNovo = toMinutes(horaTermino);
                int inicioExistente = toMinutes(e.getHoraInicio());
                int terminoExistente = toMinutes(e.getHoraTermino());
                if (inicioNovo < terminoExistente && terminoNovo > inicioExistente) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método original de verificação de conflito (para novas inserções)
    public boolean existeConflitoHorario(String local, String data, String horaInicio, String horaTermino) {
        for (Evento e : listaEventos) {
            if (e.getData().equals(data) && e.getLocal().equalsIgnoreCase(local)) {
                int inicioNovo = toMinutes(horaInicio);
                int terminoNovo = toMinutes(horaTermino);
                int inicioExistente = toMinutes(e.getHoraInicio());
                int terminoExistente = toMinutes(e.getHoraTermino());
                if (inicioNovo < terminoExistente && terminoNovo > inicioExistente) {
                    return true;
                }
            }
        }
        return false;
    }

    private int toMinutes(String hora) {
        String[] partes = hora.split(":");
        int h = Integer.parseInt(partes[0]);
        int m = Integer.parseInt(partes[1]);
        return h * 60 + m;
    }

    public void adiciona(Evento evento) {
        listaEventos.add(evento);
    }

    public void altera(int position, Evento evento) {
        listaEventos.set(position, evento);
    }

    public void remove(int position) {
        listaEventos.remove(position);
    }
}
