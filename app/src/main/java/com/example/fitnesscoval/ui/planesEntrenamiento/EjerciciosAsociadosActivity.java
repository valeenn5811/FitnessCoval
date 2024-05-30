package com.example.fitnesscoval.ui.planesEntrenamiento;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.example.fitnesscoval.R;
import com.example.fitnesscoval.ui.bbdd.PlanesEntrenamientoBBDD;

import java.util.List;

public class EjerciciosAsociadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios_asociados);
    }

    private void obtenerYMostrarEjercicios(int planId) {

    }

}
