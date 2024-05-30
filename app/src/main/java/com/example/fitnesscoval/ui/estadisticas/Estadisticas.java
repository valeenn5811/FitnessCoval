package com.example.fitnesscoval.ui.estadisticas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragmento para mostrar estadísticas de usuario como consumo de calorías, distribución de macronutrientes
 * y actividad física realizada.
 */
public class Estadisticas extends Fragment {

    private TextView textViewEstadisticas;
    private TextView textViewConsumoCalorias;
    private TextView textViewDistribucionMacronutrientes;
    private TextView textViewActividadFisica;
    private TextView textViewTusDatos;
    private int userId;
    private String nombreUsuario;

    /**
     * Infla la interfaz de usuario del fragmento y configura los componentes de la interfaz.
     * También carga y muestra estadísticas de usuario como el consumo de calorías, la distribución de macronutrientes
     * y la actividad física realizada.
     *
     * @param inflater           El LayoutInflater utilizado para inflar la interfaz de usuario.
     * @param container          El ViewGroup al que se debe adjuntar el fragmento.
     * @param savedInstanceState La instancia guardada del fragmento, si la hay.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        textViewEstadisticas = root.findViewById(R.id.text_estadisticas_titulo);
        textViewConsumoCalorias = root.findViewById(R.id.text_consumo_calorias);
        textViewDistribucionMacronutrientes = root.findViewById(R.id.text_distribucion_macronutrientes);
        textViewActividadFisica = root.findViewById(R.id.text_actividad_fisica);
        textViewTusDatos = root.findViewById(R.id.text_tus_datos);

        userId = getUserId(requireContext());

        // Obtener y mostrar datos del usuario
        obtenerInformacionUsuario();

        // Obtener la fecha actual en el formato necesario (por ejemplo, "YYYY-MM-DD")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        // Llama a los métodos para obtener los datos
        obtenerConsumoCaloriasPorDia(fechaActual);
        obtenerDistribucionMacronutrientes();
        obtenerActividadFisicaRealizada();

        return root;
    }

    /**
     * Obtiene y muestra la información del usuario, incluyendo nombre, apellidos, fecha de nacimiento,
     * altura y peso.
     * Actualiza la interfaz de usuario con los datos obtenidos.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Actualizar los datos del usuario cuando el fragmento se reanuda
        obtenerInformacionUsuario();
    }

    /**
     * Obtiene el consumo de calorías del usuario para el día actual y lo muestra en la interfaz de usuario.
     *
     * @param fecha La fecha para la cual se debe obtener el consumo de calorías.
     */
    private void obtenerConsumoCaloriasPorDia(String fecha) {
        String url = "http://192.168.1.16/consumo_calorias.php?userId=" + userId + "&fecha=" + fecha;

        // Hacer solicitud HTTP usando Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta JSON
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() > 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String fecha = jsonObject.getString("fecha_consumo");
                                    double totalCalorias = jsonObject.getDouble("total_calorias");
                                    stringBuilder.append("Fecha: ").append(fecha)
                                            .append(", Calorías: ").append(totalCalorias).append("\n");
                                }
                                textViewConsumoCalorias.setText(stringBuilder.toString());
                            } else {
                                textViewConsumoCalorias.setText("No se encontraron datos para el día actual");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewConsumoCalorias.setText("¡Es hora de comer!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de la solicitud
                textViewConsumoCalorias.setText("Error al obtener datos");
            }
        });

        // Agregar la solicitud a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    /**
     * Obtiene la distribución de macronutrientes del usuario y la muestra en la interfaz de usuario.
     */
    private void obtenerDistribucionMacronutrientes() {
        String url = "http://192.168.1.16/distribucion_macronutrientes.php?userId=" + userId;

        // Hacer solicitud HTTP usando Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta JSON
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() > 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                JSONObject jsonObject = jsonArray.getJSONObject(0); // Tomar el primer y único elemento (para el día actual)
                                double totalProteinas = jsonObject.getDouble("total_proteinas");
                                double totalCarbohidratos = jsonObject.getDouble("total_carbohidratos");
                                double totalGrasas = jsonObject.getDouble("total_grasas");
                                stringBuilder.append("Proteínas: ").append(totalProteinas)
                                        .append(", Carbohidratos: ").append(totalCarbohidratos)
                                        .append(", Grasas: ").append(totalGrasas).append("\n");
                                textViewDistribucionMacronutrientes.setText(stringBuilder.toString());
                            } else {
                                textViewDistribucionMacronutrientes.setText("No se encontraron datos para el día actual");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewDistribucionMacronutrientes.setText("Ve a diario y añade tu primera comida del día");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de la solicitud
                textViewDistribucionMacronutrientes.setText("Error al obtener datos");
            }
        });

        // Agregar la solicitud a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    /**
     * Obtiene la actividad física realizada por el usuario y la muestra en la interfaz de usuario.
     */
    private void obtenerActividadFisicaRealizada() {
        // URL del script PHP para obtener el nombre del plan activo del usuario
        String url = "http://192.168.1.16/actividad_fisica.php?userId=" + userId;

        // Hacer solicitud HTTP usando Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta JSON
                        try {
                            if (response.has("nombre_plan_activo")) {
                                String nombrePlanActivo = response.getString("nombre_plan_activo");
                                // Mostrar el nombre del plan activo en el TextView correspondiente
                                textViewActividadFisica.setText(nombrePlanActivo);
                            } else {
                                textViewActividadFisica.setText("No se encontró un plan activo para el usuario.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewActividadFisica.setText("Actívate un plan y empieza a moverte");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de la solicitud
                textViewActividadFisica.setText("Error al obtener el nombre del plan activo del usuario");
            }
        });

        // Agregar la solicitud a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    /**
     * Obtiene y muestra la información del usuario, incluyendo nombre, apellidos, fecha de nacimiento,
     * altura y peso.
     */
    private void obtenerInformacionUsuario() {
        nombreUsuario = getNombreUsuario(requireContext());
        textViewTusDatos.setText("Tus Datos: Usuario --> " + nombreUsuario);
        int userId = getUserId(requireContext());
        if (userId != -1) {
            new Thread(() -> {
                try {
                    // Crear la conexión al servidor para obtener la información del usuario
                    URL url = new URL("http://192.168.1.16/obtener_info_usuario.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Enviar el ID del usuario al servidor
                    OutputStream os = conn.getOutputStream();
                    os.write(("id=" + userId).getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // Leer la respuesta del servidor
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    Log.d("RespuestaServidor", response.toString());

                    // Procesar la respuesta del servidor
                    // Aquí debes implementar la lógica para parsear la respuesta y obtener los datos del usuario
                    String[] userData = response.toString().split(",");

                    if (userData.length >= 6) { // Verifica si hay al menos 6 elementos en el array
                        // Establecer los valores en los campos de la interfaz de usuario
                        // Establecer los valores en los campos de la interfaz de usuario
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) requireView().findViewById(R.id.tvNombre)).setText(userData[0]);
                                ((TextView) requireView().findViewById(R.id.tvApellidos)).setText(userData[1]);
                                ((TextView) requireView().findViewById(R.id.tvFechaNacimiento)).setText(userData[2]);
                                ((TextView) requireView().findViewById(R.id.tvAltura)).setText(userData[3]);
                                ((TextView) requireView().findViewById(R.id.tvPeso)).setText(userData[4]);
                            }
                        });
                    } else {
                        // Manejar el caso donde la respuesta del servidor no contiene todos los datos esperados
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Completa tus datos en la pestaña de ajustes!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Error al obtener información del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        } else {
            Toast.makeText(requireContext(), "ID de usuario no válido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Obtiene el índice de un género en el array de recursos de la aplicación.
     *
     * @param context El contexto de la aplicación.
     * @param genero  El género para el cual se debe obtener el índice.
     * @return El índice del género en el array de recursos, o 0 si no se encuentra.
     */
    private int obtenerIndiceGenero(Context context, String genero) {
        String[] generos = context.getResources().getStringArray(R.array.genero_array);
        for (int i = 0; i < generos.length; i++) {
            if (generos[i].equals(genero)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Obtiene el ID de usuario almacenado en las preferencias compartidas de la aplicación.
     *
     * @param context El contexto de la aplicación.
     * @return El ID de usuario almacenado en las preferencias compartidas, o -1 si no se encuentra.
     */
    private int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("Id", -1);
    }

    /**
     * Obtiene el nombre de usuario almacenado en las preferencias compartidas de la aplicación.
     *
     * @param context El contexto de la aplicación.
     * @return El nombre de usuario almacenado en las preferencias compartidas.
     */
    private String getNombreUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "");
    }
}
