package com.example.fitnesscoval.ui.registroComidas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesscoval.R;

import java.util.List;

public class AlimentoAdapter extends RecyclerView.Adapter<AlimentoAdapter.AlimentoViewHolder> {
    private List<String> alimentos;

    public void setAlimentos(List<String> alimentos) {
        this.alimentos = alimentos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alimento, parent, false);
        return new AlimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentoViewHolder holder, int position) {
        String alimento = alimentos.get(position);
        holder.bind(alimento);
    }

    @Override
    public int getItemCount() {
        return alimentos != null ? alimentos.size() : 0;
    }

    static class AlimentoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;

        public AlimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.nombreTextView); // Este id debe coincidir con el TextView en item_alimento.xml
        }

        public void bind(String alimento) {
            textViewNombre.setText(alimento);
        }
    }

    public void clearAlimentos() {
        alimentos.clear();
        notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
    }
}
