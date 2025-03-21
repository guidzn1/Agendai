package com.example.agendai.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agendai.R;
import com.example.agendai.models.Evento;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventos;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Evento evento);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(Evento evento);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public EventoAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.textViewTitulo.setText(evento.getTitulo());
        holder.textViewEspaco.setText(evento.getLocal());
        holder.textViewContato.setText("Contato: " + evento.getContato());
        holder.textViewData.setText("Data: " + evento.getData());
        holder.textViewHorario.setText("Início: " + evento.getHoraInicio() + " - Término: " + evento.getHoraTermino());

        // Botão para cobrar via WhatsApp
        holder.buttonCobrar.setOnClickListener(v -> {
            String phoneNumber = evento.getContato();
            // Garante que o número comece com "55"
            if (!phoneNumber.startsWith("55")) {
                phoneNumber = "55" + phoneNumber;
            }
            String message = "Olá, estamos entrando em contato para cobrar o valor restante do seu agendamento.";
            try {
                String url = "https://wa.me/" + phoneNumber + "?text=" + URLEncoder.encode(message, "UTF-8");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "WhatsApp não está instalado", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Erro ao abrir o WhatsApp", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão para editar
        holder.buttonEditar.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(evento);
            }
        });

        // Botão para excluir
        holder.buttonExcluir.setOnClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(evento);
            }
        });

        // Clique no item para ação padrão
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(evento);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        TextView textViewEspaco;
        TextView textViewContato;
        TextView textViewData;
        TextView textViewHorario;
        ImageButton buttonCobrar;
        ImageButton buttonEditar;
        ImageButton buttonExcluir;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewEspaco = itemView.findViewById(R.id.textViewEspaco);
            textViewContato = itemView.findViewById(R.id.textViewContato);
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewHorario = itemView.findViewById(R.id.textViewHorario);
            buttonCobrar = itemView.findViewById(R.id.buttonCobrar);
            buttonEditar = itemView.findViewById(R.id.buttonEditarEvento);
            buttonExcluir = itemView.findViewById(R.id.buttonExcluirEvento);
        }
    }
}
