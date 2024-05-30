package com.example.fitnesscoval.ui.planesDietas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnesscoval.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * El adaptador para la lista de dietas en el RecyclerView.
 */
public class DietasAdapter extends RecyclerView.Adapter<DietasAdapter.DietasViewHolder> {

    private ArrayList<HashMap<String, String[]>> dietas;

    /**
     * Constructor del adaptador de dietas.
     * @param dietas La lista de dietas a mostrar.
     */
    public DietasAdapter(ArrayList<HashMap<String, String[]>> dietas) {
        this.dietas = dietas;
    }

    /**
     * Método llamado cuando se necesita crear un nuevo ViewHolder.
     * @param parent El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo DietasViewHolder que contiene la vista de un elemento de dieta.
     */
    @NonNull
    @Override
    public DietasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dieta, parent, false);
        return new DietasViewHolder(view);
    }

    /**
     * Método llamado cuando se necesita actualizar un ViewHolder existente con nuevos datos.
     * @param holder El DietasViewHolder a actualizar.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull DietasViewHolder holder, int position) {
        HashMap<String, String[]> dieta = dietas.get(position);
        holder.dietaTitle.setText("Dieta " + (position + 1));
        holder.desayuno.setText("Desayuno: \n" + String.join("\n", dieta.get("Desayuno")));
        holder.comida.setText("Comida: \n" + String.join("\n", dieta.get("Comida")));
        holder.merienda.setText("Merienda: \n" + String.join("\n", dieta.get("Merienda")));
        holder.cena.setText("Cena: \n" + String.join("\n", dieta.get("Cena")));
    }

    /**
     * Método llamado para obtener el número total de elementos en el conjunto de datos.
     * @return El número total de dietas.
     */
    @Override
    public int getItemCount() {
        return dietas.size();
    }

    /**
     * Clase interna que representa el ViewHolder para cada elemento de la lista de dietas.
     */
    static class DietasViewHolder extends RecyclerView.ViewHolder {

        TextView dietaTitle;
        TextView desayuno;
        TextView comida;
        TextView merienda;
        TextView cena;

        /**
         * Constructor del DietasViewHolder.
         * @param itemView La vista del elemento de la dieta.
         */
        public DietasViewHolder(@NonNull View itemView) {
            super(itemView);
            dietaTitle = itemView.findViewById(R.id.dietaTitle);
            desayuno = itemView.findViewById(R.id.desayuno);
            comida = itemView.findViewById(R.id.comida);
            merienda = itemView.findViewById(R.id.merienda);
            cena = itemView.findViewById(R.id.cena);
        }
    }
}
