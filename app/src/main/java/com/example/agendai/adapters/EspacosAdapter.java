package com.example.agendai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agendai.R;
import com.example.agendai.models.Espaco;
import java.util.List;

public class EspacosAdapter extends RecyclerView.Adapter<EspacosAdapter.EspacoViewHolder> {

    private List<Espaco> espacos;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Espaco espaco, int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Espaco espaco, int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public EspacosAdapter(List<Espaco> espacos) {
        this.espacos = espacos;
    }

    @NonNull
    @Override
    public EspacoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_espaco, parent, false);
        return new EspacoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EspacoViewHolder holder, int position) {
        Espaco espaco = espacos.get(position);
        holder.textViewNomeEspaco.setText(espaco.getNome());
        holder.buttonEditarEspaco.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(espaco, position);
            }
        });
        holder.buttonExcluirEspaco.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(espaco, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return espacos.size();
    }

    public static class EspacoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomeEspaco;
        ImageButton buttonEditarEspaco;
        ImageButton buttonExcluirEspaco;

        public EspacoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomeEspaco = itemView.findViewById(R.id.textViewNomeEspaco);
            buttonEditarEspaco = itemView.findViewById(R.id.buttonEditarEspaco);
            buttonExcluirEspaco = itemView.findViewById(R.id.buttonExcluirEspaco);
        }
    }
}
