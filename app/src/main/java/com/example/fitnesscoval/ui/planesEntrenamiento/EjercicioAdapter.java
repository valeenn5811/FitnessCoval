package com.example.fitnesscoval.ui.planesEntrenamiento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.fitnesscoval.R;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder> {
    private Context context;
    private List<Ejercicio> ejercicios;

    public EjercicioAdapter(Context context, List<Ejercicio> ejercicios) {
        this.context = context;
        this.ejercicios = ejercicios;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ejercicio, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);
        holder.bind(ejercicio);
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombre;
        private TextView txtDescripcion;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreEjercicio);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionEjercicio);
        }

        public void bind(Ejercicio ejercicio) {
            txtNombre.setText(ejercicio.getNombre());
            txtDescripcion.setText(ejercicio.getDescripcion());
        }
    }
}

