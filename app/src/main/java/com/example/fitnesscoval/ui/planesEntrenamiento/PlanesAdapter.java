package com.example.fitnesscoval.ui.planesEntrenamiento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesscoval.R;

import java.util.List;

public class PlanesAdapter extends RecyclerView.Adapter<PlanesAdapter.PlanViewHolder> {

    private Context context;
    private List<PlanEntrenamiento> listaPlanes;
    private OnPlanClickListener mListener;
    private int selectedItem = -1;

    // Constructor
    public PlanesAdapter(Context context, List<PlanEntrenamiento> listaPlanes) {
        this.context = context;
        this.listaPlanes = listaPlanes;
    }

    // Interface para manejar los clics en los planes
    public interface OnPlanClickListener {
        void onPlanClick(int position);
    }

    // Método para establecer el listener
    public void setOnPlanClickListener(OnPlanClickListener listener) {
        mListener = listener;
    }

    // Método para establecer el ítem seleccionado
    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan_entrenamiento, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanEntrenamiento plan = listaPlanes.get(position);
        holder.textViewNombre.setText(plan.getNombre());
        holder.textViewDescripcion.setText(plan.getDescripcion());

        // Aplicar la animación de escala al elemento seleccionado
        if (position == selectedItem) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
            holder.itemView.startAnimation(animation);
        } else {
            // Limpiar la animación si el elemento no está seleccionado
            holder.itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return listaPlanes.size();
    }

    // ViewHolder
    public class PlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewNombre, textViewDescripcion;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombrePlan);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcionPlan);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Notificar al listener sobre el clic y actualizar el ítem seleccionado
                    mListener.onPlanClick(position);
                    setSelectedItem(position);
                }
            }
        }
    }
}
