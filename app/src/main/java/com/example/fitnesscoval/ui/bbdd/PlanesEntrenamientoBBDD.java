package com.example.fitnesscoval.ui.bbdd;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.ui.planesEntrenamiento.PlanEntrenamiento;

import org.json.JSONArray;

/**
 * Clase para realizar operaciones relacionadas con la obtención de planes de entrenamiento desde el servidor.
 */
public class PlanesEntrenamientoBBDD {

    private static final String URL_PLANES = "http://192.168.1.16/PlanesEntrenamiento.php";

    /**
     * Interfaz para manejar el resultado de la solicitud de obtención de planes de entrenamiento.
     */
    public interface PlanCallback {
        void onSuccess(PlanEntrenamiento plan);
        void onFailure(String errorMessage);
    }

    /**
     * Obtiene los planes de entrenamiento del servidor.
     *
     * @param context  El contexto de la aplicación.
     * @param listener Listener para manejar la respuesta del servidor.
     */
    public static void obtenerPlanes(Context context, Response.Listener<JSONArray> listener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_PLANES, null,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar error
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
