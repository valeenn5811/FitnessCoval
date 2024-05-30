package com.example.fitnesscoval.ui.planesDietas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnesscoval.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dietas es un fragmento que muestra diferentes dietas y un botón para ver consejos relacionados.
 */
public class Dietas extends Fragment {

    /**
     * Constructor vacío requerido para instancias de fragmento.
     */
    public Dietas() {
        // Constructor vacío requerido
    }

    /**
     * Este método infla el diseño XML para el fragmento y configura la vista del fragmento.
     * @param inflater El LayoutInflater utilizado para inflar el diseño.
     * @param container El ViewGroup en el que se inflará el diseño.
     * @param savedInstanceState La instancia anteriormente guardada del fragmento, si está disponible.
     * @return La vista del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño XML para este fragmento
        View view = inflater.inflate(R.layout.fragment_dietas, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDietas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el botón y el TextView
        Button buttonConsejos = view.findViewById(R.id.buttonConsejos);
        buttonConsejos.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ConsejosActivity.class);
            startActivity(intent);
        });

        // Datos de ejemplo para las dietas
        ArrayList<HashMap<String, String[]>> dietas = new ArrayList<>();

        HashMap<String, String[]> dieta1 = new HashMap<>();
        dieta1.put("Desayuno", new String[]{
                "Avena con frutas (200g) - 300 cal",
                "Tostadas con aguacate (2 rebanadas) - 250 cal",
                "Smoothie verde (1 vaso) - 200 cal"});
        dieta1.put("Comida", new String[]{
                "Ensalada de quinoa (250g) - 400 cal",
                "Pollo a la parrilla con verduras (200g) - 350 cal",
                "Pasta integral con pesto (150g) - 300 cal"});
        dieta1.put("Merienda", new String[]{
                "Yogur con granola (1 taza) - 200 cal",
                "Frutas frescas (1 porción) - 100 cal",
                "Hummus con vegetales (100g) - 150 cal"});
        dieta1.put("Cena", new String[]{
                "Sopa de verduras (1 taza) - 150 cal",
                "Pescado al horno con espárragos (200g) - 250 cal",
                "Tacos de lechuga con carne (2 tacos) - 150 cal"});

        HashMap<String, String[]> dieta2 = new HashMap<>();
        dieta2.put("Desayuno", new String[]{
                "Batido de proteínas (1 vaso) - 250 cal",
                "Huevos revueltos con espinacas (2 huevos) - 200 cal",
                "Pan integral con tomate (2 rebanadas) - 150 cal"});
        dieta2.put("Comida", new String[]{
                "Arroz integral con pollo (200g) - 350 cal",
                "Salmón a la plancha (150g) - 300 cal",
                "Lentejas con verduras (1 taza) - 250 cal"});
        dieta2.put("Merienda", new String[]{
                "Barra de proteínas (1 unidad) - 200 cal",
                "Manzana con mantequilla de maní (1 unidad) - 150 cal",
                "Batido de frutas (1 vaso) - 150 cal"});
        dieta2.put("Cena", new String[]{
                "Ensalada César (1 porción) - 200 cal",
                "Pollo al curry (200g) - 300 cal",
                "Crema de calabaza (1 taza) - 200 cal"});

        HashMap<String, String[]> dieta3 = new HashMap<>();
        dieta3.put("Desayuno", new String[]{
                "Tortilla de claras (4 claras) - 150 cal",
                "Pan de centeno con queso cottage (2 rebanadas) - 200 cal",
                "Batido de espinacas y plátano (1 vaso) - 200 cal"});
        dieta3.put("Comida", new String[]{
                "Pavo con arroz (200g) - 350 cal",
                "Ensalada de garbanzos (1 taza) - 300 cal",
                "Atún con verduras asadas (200g) - 300 cal"});
        dieta3.put("Merienda", new String[]{
                "Frutos secos (50g) - 250 cal",
                "Zumo de naranja natural (1 vaso) - 100 cal",
                "Tostadas de arroz con hummus (2 unidades) - 150 cal"});
        dieta3.put("Cena", new String[]{
                "Pechuga de pollo con ensalada (200g) - 300 cal",
                "Sopa miso (1 taza) - 100 cal",
                "Vegetales al wok (1 taza) - 150 cal"});

        HashMap<String, String[]> dieta4 = new HashMap<>();
        dieta4.put("Desayuno", new String[]{
                "Porridge con nueces (1 taza) - 300 cal",
                "Sandwich de pavo y aguacate (1 unidad) - 350 cal",
                "Yogur con miel y nueces (1 taza) - 200 cal"});
        dieta4.put("Comida", new String[]{
                "Hamburguesa de lentejas (1 unidad) - 300 cal",
                "Quiche de verduras (1 porción) - 300 cal",
                "Pasta con salsa de tomate (200g) - 300 cal"});
        dieta4.put("Merienda", new String[]{
                "Batido de proteínas (1 vaso) - 200 cal",
                "Barra de cereales (1 unidad) - 150 cal",
                "Té verde con galletas integrales (1 taza + 2 galletas) - 100 cal"});
        dieta4.put("Cena", new String[]{
                "Pizza de coliflor (2 rebanadas) - 300 cal",
                "Bacalao con patatas (200g) - 300 cal",
                "Crema de champiñones (1 taza) - 200 cal"});

        dietas.add(dieta1);
        dietas.add(dieta2);
        dietas.add(dieta3);
        dietas.add(dieta4);

        DietasAdapter adapter = new DietasAdapter(dietas);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
