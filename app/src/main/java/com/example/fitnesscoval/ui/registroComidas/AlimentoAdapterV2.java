package com.example.fitnesscoval.ui.registroComidas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesscoval.R;

import java.util.List;

public class AlimentoAdapterV2 extends RecyclerView.Adapter<AlimentoAdapterV2.AlimentoViewHolder> {
    private List<Alimento> alimentos;
    private OnItemClickListener listener;
    private int selectedItem = -1; // Variable para almacenar el índice del elemento seleccionado

    public AlimentoAdapterV2(List<Alimento> alimentos, OnItemClickListener listener) {
        this.alimentos = alimentos;
        this.listener = listener;
    }

    public void setAlimentos(List<Alimento> alimentos) {
        this.alimentos = alimentos;
        notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
    }

    @NonNull
    @Override
    public AlimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alimento, parent, false);
        return new AlimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentoViewHolder holder, int position) {
        if (alimentos != null && position < alimentos.size()) {
            Alimento alimento = alimentos.get(position);
            holder.bind(alimento, position);
        }
    }

    @Override
    public int getItemCount() {
        return alimentos != null ? alimentos.size() : 0;
    }

    public class AlimentoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombreAlimento;

        public AlimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreAlimento = itemView.findViewById(R.id.nombreTextView);

            // Agregar el listener de clics al ViewHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(alimentos.get(position));
                            selectedItem = position; // Actualizar el índice del elemento seleccionado
                            notifyDataSetChanged(); // Notificar al adaptador de que los datos han cambiado
                        }
                    }
                }
            });
        }

        public void bind(Alimento alimento, int position) {
            txtNombreAlimento.setText(alimento.getNombre());
            // Establecer el fondo según si este elemento está seleccionado o no
            itemView.setBackgroundResource(position == selectedItem ? R.color.selected_color : android.R.color.transparent);
        }
    }

    // Interfaz para manejar los clics en los elementos del RecyclerView
    public interface OnItemClickListener {
        void onItemClick(Alimento alimento);
    }
}
