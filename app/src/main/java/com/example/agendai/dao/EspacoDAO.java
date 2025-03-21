package com.example.agendai.dao;

import android.content.Context;
import com.example.agendai.models.Espaco;
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

public class EspacoDAO {

    private static List<Espaco> listaEspacos = new ArrayList<>();
    private final String ARQUIVO_BD = "espacos.json";
    private Gson gson = new Gson();

    public void carregaLista(Context context) {
        try {
            FileReader reader = new FileReader(context.getFilesDir() + "/" + ARQUIVO_BD);
            Type type = new TypeToken<Espaco[]>() {}.getType();
            Espaco[] array = gson.fromJson(reader, type);
            if(array != null){
                listaEspacos = new ArrayList<>(Arrays.asList(array));
            } else {
                listaEspacos = new ArrayList<>();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            listaEspacos = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvaLista(Context context) {
        try {
            FileWriter writer = new FileWriter(context.getFilesDir() + "/" + ARQUIVO_BD);
            String json = gson.toJson(listaEspacos);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Espaco> getEspacos() {
        return new ArrayList<>(listaEspacos);
    }

    public void adiciona(Espaco espaco) {
        listaEspacos.add(espaco);
    }

    public void altera(int position, Espaco espaco) {
        listaEspacos.set(position, espaco);
    }

    public void remove(int position) {
        listaEspacos.remove(position);
    }

    // Método para obter um espaço pelo nome
    public Espaco getEspacoByNome(String nome) {
        for (Espaco espaco : listaEspacos) {
            if (espaco.getNome().equalsIgnoreCase(nome)) {
                return espaco;
            }
        }
        return null;
    }
}
