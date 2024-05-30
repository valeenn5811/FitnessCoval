package com.example.fitnesscoval.ui.planesDietas;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnesscoval.R;

/**
 * ConsejosActivity muestra consejos relacionados con planes de dieta y ejercicio.
 */
public class ConsejosActivity extends AppCompatActivity {

    /**
     * Este método se llama cuando la actividad se está creando.
     * Aquí se establece el diseño de la actividad.
     * @param savedInstanceState información sobre el estado previo de la actividad, si está disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);
    }
}
