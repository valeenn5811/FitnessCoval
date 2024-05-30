package com.example.fitnesscoval.ui.planesEntrenamiento;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.fitnesscoval.R;
import com.example.fitnesscoval.ui.bbdd.PlanesEntrenamientoBBDD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragmento que muestra una lista de planes de entrenamiento disponibles y maneja su visualización detallada.
 * Los datos de los planes se obtienen de un servidor remoto y se muestran en un RecyclerView.
 */
public class PlanesEntrenamiento extends Fragment implements PlanesAdapter.OnPlanClickListener {

    private RecyclerView recyclerViewPlanes;
    private PlanesAdapter planesAdapter;
    private List<PlanEntrenamiento> listaPlanes;

    /**
     * Método llamado para crear y devolver la jerarquía de vistas asociada con el fragmento.
     *
     * @param inflater           El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container          Si no es nulo, este es el grupo padre al que se debe adjuntar el fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido a partir de un estado guardado anterior.
     * @return La vista root del fragmento.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.planes_entrenamiento, container, false);

        recyclerViewPlanes = root.findViewById(R.id.recyclerViewPlanes);
        recyclerViewPlanes.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPlanes = new ArrayList<>();

        obtenerPlanes();

        return root;
    }

    /**
     * Método llamado cuando se hace clic en un elemento de la lista de planes de entrenamiento.
     *
     * @param position La posición del elemento en la lista que se ha hecho clic.
     */
    @Override
    public void onPlanClick(int position) {
        if (listaPlanes != null && position >= 0 && position < listaPlanes.size()) {
            PlanEntrenamiento plan = listaPlanes.get(position);
            Log.d("PlanesEntrenamiento", "Plan seleccionado - ID: " + plan.getId());
            Intent intent = new Intent(getContext(), DetallesPlanActivity.class);
            intent.putExtra("planId", plan.getId());
            startActivity(intent);
        }
    }

    /**
     * Método para obtener los datos de los planes de entrenamiento del servidor.
     */
    private void obtenerPlanes() {
        PlanesEntrenamientoBBDD.obtenerPlanes(getContext(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    listaPlanes.clear();
                    Set<String> nombresDePlan = new HashSet<>(); // Utilizamos un conjunto para almacenar los nombres de plan únicos

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject planJson = response.getJSONObject(i);
                        int id = planJson.getInt("id");
                        String nombre = planJson.getString("nombre");
                        String descripcion = planJson.getString("descripcion");
                        int frecuencia = planJson.getInt("frecuencia");
                        String objetivo = planJson.getString("objetivo");

                        // Verificamos si el nombre del plan ya está en el conjunto
                        if (!nombresDePlan.contains(nombre)) {
                            PlanEntrenamiento plan = new PlanEntrenamiento(id, nombre, descripcion, frecuencia, objetivo);
                            listaPlanes.add(plan);
                            nombresDePlan.add(nombre); // Agregamos el nombre del plan al conjunto
                        }
                    }
                    planesAdapter = new PlanesAdapter(getContext(), listaPlanes);
                    planesAdapter.setOnPlanClickListener(PlanesEntrenamiento.this);
                    recyclerViewPlanes.setAdapter(planesAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
