package com.example.fitnesscoval.ui.registroComidas;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Activity para buscar alimentos y agregarlos como alimentos consumidos.
 * Permite al usuario buscar alimentos por nombre, ver detalles y agregar la cantidad consumida.
 */
public class BuscarAlimentoActivity extends AppCompatActivity {
    private EditText edtBuscarAlimento;
    private Button btnAgregar;
    private RecyclerView recyclerViewResultado;
    private AlimentoAdapterV2 adapter;
    private List<Alimento> alimentos;
    private Alimento alimentoSeleccionadoTemp; // Variable temporal para almacenar el alimento seleccionado
    private AlimentoConsumido alimentoConsumido;

    /**
     * Método llamado cuando se crea la actividad.
     * Configura las vistas y los listeners.
     *
     * @param savedInstanceState Información sobre el estado de la actividad.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_alimento);

        edtBuscarAlimento = findViewById(R.id.edtBuscarAlimento);
        btnAgregar = findViewById(R.id.btnBuscar);
        recyclerViewResultado = findViewById(R.id.recyclerViewResultado);

        // Configurar RecyclerView
        recyclerViewResultado.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlimentoAdapterV2(new ArrayList<>(), new AlimentoAdapterV2.OnItemClickListener() {
            @Override
            public void onItemClick(Alimento alimento) {
                // Cuando se hace clic en un elemento del RecyclerView, almacenar el alimento seleccionado
                alimentoSeleccionadoTemp = alimento;
                // Mostrar los detalles del alimento seleccionado
                mostrarDetallesAlimento(alimento);
                // Habilitar el botón de agregar
                btnAgregar.setEnabled(true);
            }
        });

        recyclerViewResultado.setAdapter(adapter);

        alimentos = new ArrayList<>();

        // Inicialmente deshabilitar el botón de agregar
        btnAgregar.setEnabled(false);

        // Agregar listener al botón de búsqueda
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarAlimentoConsumido();
            }
        });

        // Agregar listener al EditText para realizar la búsqueda mientras el usuario escribe
        edtBuscarAlimento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                buscarAlimento();
            }
        });
    }

    // Variable miembro para almacenar el AlertDialog
    private AlertDialog alertDialog;

    /**
     * Muestra los detalles del alimento seleccionado en un diálogo.
     *
     * @param alimento Alimento seleccionado.
     */
    private void mostrarDetallesAlimento(Alimento alimento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Alimento");

        // Construir el mensaje con los detalles del alimento
        StringBuilder message = new StringBuilder();
        message.append("Nombre: ").append(alimento.getNombre()).append("\n");
        message.append("Calorías_100: ").append(alimento.getCalorias_cien()).append("\n");
        message.append("Proteínas: ").append(alimento.getProteinas()).append("\n");
        message.append("Carbohidratos: ").append(alimento.getCarbohidratos()).append("\n");
        message.append("Grasas: ").append(alimento.getGrasas()).append("\n");

        // Configurar el mensaje en el diálogo
        builder.setMessage(message.toString());

        // Botón para cerrar el diálogo
        builder.setPositiveButton("Cerrar", null);

        // Guardar la referencia al AlertDialog creado
        alertDialog = builder.create();

        // Mostrar el diálogo
        alertDialog.show();
    }

    // Método onDestroy() de la actividad
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Verificar si el diálogo está mostrándose y cerrarlo si es necesario
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    /**
     * Agrega el alimento seleccionado como alimento consumido.
     */
    private void agregarAlimentoConsumido() {
        if (alimentoSeleccionadoTemp != null) {
            // Obtener la fecha actual en el formato deseado (YYYY-MM-DD)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaActual = dateFormat.format(Calendar.getInstance().getTime());

            // Crear un diálogo para que el usuario ingrese la cantidad en gramos y el tipo de comida
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Agregar alimento consumido");

            // Layout personalizado para el diálogo
            View view = getLayoutInflater().inflate(R.layout.dialog_agregar_alimento_consumido, null);
            EditText edtCantidad = view.findViewById(R.id.edtCantidad);
            Spinner spinnerTipoComida = view.findViewById(R.id.spinnerTipoComida);

            // Mostrar los detalles del alimento seleccionado en el diálogo
            mostrarDetallesAlimento(alimentoSeleccionadoTemp);

            builder.setView(view);

            // Botón para agregar el alimento consumido
            builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        // Obtener cantidad y tipo de comida
                        String cantidadText = edtCantidad.getText().toString().trim();
                        if (!cantidadText.isEmpty()) {
                            double cantidadGramos = Double.parseDouble(cantidadText);
                            int idTipoComida = spinnerTipoComida.getSelectedItemPosition() + 1; // Suponiendo que los índices en el spinner corresponden a los IDs de tipo de comida

                            // Crear un objeto JSON para enviar al servidor
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id_usuario", getUserId(BuscarAlimentoActivity.this)); // Obtener el ID del usuario actual
                            jsonObject.put("id_tipo_comida", idTipoComida);
                            jsonObject.put("id_alimento", alimentoSeleccionadoTemp.getId());
                            jsonObject.put("cantidad_gramos", cantidadGramos);
                            jsonObject.put("fecha_consumo", fechaActual);

                            // URL del script PHP en tu servidor que maneja la inserción del alimento consumido
                            String url = "http://192.168.1.16/insertar_alimento_consumido.php";

                            // Crear una solicitud POST utilizando Volley
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Manejar la respuesta del servidor
                                            // Aquí puedes mostrar un mensaje de éxito al usuario
                                            Log.d("ERROR AGREGAR ALIMENTO", "Respuesta del servidor: " + response.toString());
                                            Toast.makeText(BuscarAlimentoActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            // Configurar el resultado como RESULT_OK y finalizar la actividad para volver al fragmento Diario
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                            // Reiniciar la selección
                                            alimentoSeleccionadoTemp = null;
                                            // Deshabilitar el botón de agregar nuevamente
                                            btnAgregar.setEnabled(false);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Manejar errores de la solicitud
                                            // Aquí puedes mostrar un mensaje de error al usuario o realizar otra acción adecuada
                                            Log.e("ERROR AGREGAR ALIMENTO", "Error al agregar alimento consumido: " + error.toString());
                                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                                try {
                                                    String response = new String(error.networkResponse.data, "UTF-8");
                                                    Log.e("ERROR AGREGAR ALIMENTO", "Error response: " + response);
                                                    Toast.makeText(BuscarAlimentoActivity.this, response, Toast.LENGTH_SHORT).show();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(BuscarAlimentoActivity.this, "Error al agregar alimento consumido", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            // Agregar la solicitud a la cola de solicitudes de Volley
                            RequestQueue queue = Volley.newRequestQueue(BuscarAlimentoActivity.this);
                            queue.add(jsonObjectRequest);
                        } else {
                            // Mostrar un mensaje si el usuario no ingresó la cantidad
                            Toast.makeText(BuscarAlimentoActivity.this, "Por favor, ingresa la cantidad en gramos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Botón para cancelar
            builder.setNegativeButton("Cancelar", null);

            // Mostrar el diálogo
            builder.create().show();
        }
    }

    /**
     * Convierte la respuesta JSON en una lista de objetos Alimento.
     *
     * @param jsonArray Respuesta JSON.
     * @return Lista de objetos Alimento.
     */
    private List<Alimento> parseJsonToAlimentos(JSONArray jsonArray) {
        List<Alimento> alimentosList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objetoAlimento = jsonArray.getJSONObject(i);
                int id = objetoAlimento.getInt("id");
                String nombre = objetoAlimento.getString("nombre");
                int calorias = objetoAlimento.getInt("calorias_cien");
                double proteinas = objetoAlimento.getDouble("proteinas");
                double carbohidratos = objetoAlimento.getDouble("carbohidratos");
                double grasas = objetoAlimento.getDouble("grasas");

                Alimento alimento = new Alimento(id, nombre, calorias, proteinas, carbohidratos, grasas);
                alimentosList.add(alimento);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alimentosList;
    }

    /**
     * Busca alimentos en el servidor según la consulta ingresada.
     */
    private void buscarAlimento() {
        String query = edtBuscarAlimento.getText().toString().trim();
        if (!query.isEmpty()) {
            // URL del script PHP en tu servidor que busca alimentos por nombre
            String url = "http://192.168.1.16/buscar_alimento.php?nombre=" + query;

            // Crear una solicitud GET utilizando Volley
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Manejar la respuesta exitosa
                            Log.d("BuscarAlimentoActivity", "Respuesta del servidor: " + response.toString());
                            List<Alimento> alimentosEncontrados = parseJsonToAlimentos(response);
                            adapter.setAlimentos(alimentosEncontrados); // Actualizar el RecyclerView con los alimentos encontrados
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Manejar errores de la solicitud
                            // Aquí puedes mostrar un mensaje de error al usuario o realizar otra acción adecuada
                            Log.e("BuscarAlimentoActivity", "Error al buscar alimentos: " + error.toString());
                            Toast.makeText(BuscarAlimentoActivity.this, "Error al buscar alimentos", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Agregar la solicitud a la cola de solicitudes de Volley
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonArrayRequest);
        }
    }

    /**
     * Obtiene el ID del usuario actual.
     *
     * @param context Contexto de la aplicación.
     * @return ID del usuario.
     */
    private int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("Id", -1);
    }

}
