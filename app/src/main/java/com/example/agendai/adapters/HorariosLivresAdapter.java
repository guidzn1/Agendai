package com.example.agendai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agendai.R;
import java.util.List;

public class HorariosLivresAdapter extends RecyclerView.Adapter<HorariosLivresAdapter.HorarioViewHolder> {

    private List<String> horarios;

    public HorariosLivresAdapter(List<String> horarios) {
        this.horarios = horarios;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario_livre, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        String horario = horarios.get(position);
        holder.textViewHorarioLivre.setText(horario);
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    public static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHorarioLivre;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHorarioLivre = itemView.findViewById(R.id.textViewHorarioLivre);
        }
    }
}
