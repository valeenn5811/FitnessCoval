package com.example.fitnesscoval.ui.registroComidas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragmento para mostrar y gestionar los alimentos consumidos durante el día.
 */
public class Diario extends Fragment {
    private Calendar calendar; // Declaración de la variable calendar
    private EditText edtFecha; // Declaración de edtFecha como variable de instancia
    final Calendar newCalendar = Calendar.getInstance();
    private RecyclerView recyclerViewDesayuno, recyclerViewComida, recyclerViewMerienda, recyclerViewCena, recyclerViewOtros;
    private AlimentoAdapter adapterDesayuno, adapterComida, adapterMerienda, adapterCena, adapterOtros;
    private int userId;
    private int totalCalorias = 0;

    /**
     * Método llamado cuando se crea la vista del fragmento.
     *
     * @param inflater           El LayoutInflater utilizado para inflar la vista.
     * @param container          El ViewGroup al que se adjuntará la vista.
     * @param savedInstanceState Información sobre el estado previamente guardado del fragmento.
     * @return La vista inflada del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_diario, container, false);

        edtFecha = rootView.findViewById(R.id.edtFecha);
        calendar = Calendar.getInstance();

        // Establecer el texto del EditText con la fecha actual
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        edtFecha.setText(currentDate);

        // Obtener el ID del usuario actual
        userId = getUserId(requireContext());

        // Inicializar RecyclerViews
        recyclerViewDesayuno = rootView.findViewById(R.id.recyclerViewDesayuno);
        recyclerViewComida = rootView.findViewById(R.id.recyclerViewComida);
        recyclerViewMerienda = rootView.findViewById(R.id.recyclerViewMerienda);
        recyclerViewCena = rootView.findViewById(R.id.recyclerViewCena);
        recyclerViewOtros = rootView.findViewById(R.id.recyclerViewOtros);

        // Establecer diseño de RecyclerViews
        recyclerViewDesayuno.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewComida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMerienda.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCena.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewOtros.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar adaptadores
        adapterDesayuno = new AlimentoAdapter();
        adapterComida = new AlimentoAdapter();
        adapterMerienda = new AlimentoAdapter();
        adapterCena = new AlimentoAdapter();
        adapterOtros = new AlimentoAdapter();

        // Establecer adaptadores en RecyclerViews
        recyclerViewDesayuno.setAdapter(adapterDesayuno);
        recyclerViewComida.setAdapter(adapterComida);
        recyclerViewMerienda.setAdapter(adapterMerienda);
        recyclerViewCena.setAdapter(adapterCena);
        recyclerViewOtros.setAdapter(adapterOtros);

        // Cargar alimentos consumidos para la fecha actual
        cargarAlimentosConsumidos("Desayuno", adapterDesayuno, currentDate);
        cargarAlimentosConsumidos("Comida", adapterComida, currentDate);
        cargarAlimentosConsumidos("Merienda", adapterMerienda, currentDate);
        cargarAlimentosConsumidos("Cena", adapterCena, currentDate);
        cargarAlimentosConsumidos("Otros", adapterOtros, currentDate);

        return rootView;
    }

    /**
     * Método llamado cuando la vista del fragmento se ha creado.
     * Configura los listeners de las vistas.
     *
     * @param view               La vista del fragmento.
     * @param savedInstanceState Información sobre el estado previamente guardado del fragmento.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText edtFecha = view.findViewById(R.id.edtFecha);

        // Agregar OnClickListener al EditText de fecha
        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar DatePickerDialog
                showDatePickerDialog();
            }
        });

        Button btnAgregarAlimento = view.findViewById(R.id.btnAgregarAlimento);
        btnAgregarAlimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BuscarAlimentoActivity.class);
                startActivityForResult(intent, 1); // El segundo parámetro es un código de solicitud que identifica esta solicitud
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Verifica que este es el código de solicitud que has utilizado
            if (resultCode == Activity.RESULT_OK) { // Verifica si el resultado es RESULT_OK
                // Actualiza la lista de alimentos consumidos aquí
                // Por ejemplo, puedes volver a cargar los alimentos para la fecha actual
                String currentDate = obtenerFechaActualEnFormatoDeseado(); // Obtener la fecha actual
                adapterDesayuno.clearAlimentos();
                adapterComida.clearAlimentos();
                adapterMerienda.clearAlimentos();
                adapterCena.clearAlimentos();
                adapterOtros.clearAlimentos();
                totalCalorias = 0;
                cargarAlimentosConsumidos("Desayuno", adapterDesayuno, currentDate);
                cargarAlimentosConsumidos("Comida", adapterComida, currentDate);
                cargarAlimentosConsumidos("Merienda", adapterMerienda, currentDate);
                cargarAlimentosConsumidos("Cena", adapterCena, currentDate);
                cargarAlimentosConsumidos("Otros", adapterOtros, currentDate);
            }
        }
    }

    private String obtenerFechaActualEnFormatoDeseado() {
        // Obtener la fecha actual en el formato deseado (dd/MM/yyyy)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Muestra el DatePickerDialog para seleccionar una fecha.
     */
    private void showDatePickerDialog() {
        // Crear y mostrar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Actualizar el calendario con la fecha seleccionada
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Actualizar el texto del EditText con la nueva fecha
                        updateFechaEditText();

                        // Obtener la nueva fecha seleccionada
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());

                        // Limpiar las listas y cargar alimentos para la nueva fecha seleccionada
                        adapterDesayuno.clearAlimentos();
                        adapterComida.clearAlimentos();
                        adapterMerienda.clearAlimentos();
                        adapterCena.clearAlimentos();
                        adapterOtros.clearAlimentos();
                        totalCalorias = 0;
                        cargarAlimentosConsumidos("Desayuno", adapterDesayuno, selectedDate);
                        cargarAlimentosConsumidos("Comida", adapterComida, selectedDate);
                        cargarAlimentosConsumidos("Merienda", adapterMerienda, selectedDate);
                        cargarAlimentosConsumidos("Cena", adapterCena, selectedDate);
                        cargarAlimentosConsumidos("Otros", adapterOtros, selectedDate);
                    }
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }

    /**
     * Actualiza el texto del EditText de fecha con la fecha seleccionada.
     */
    private void updateFechaEditText() {
        // Formatear la fecha en el formato deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Establecer el texto en el EditText de fecha
        edtFecha.setText(currentDate);
    }

    /**
     * Carga los alimentos consumidos para un tipo de comida y fecha específicos.
     *
     * @param comida   Tipo de comida (Desayuno, Comida, Merienda, Cena, Otros).
     * @param adapter  Adaptador del RecyclerView correspondiente.
     * @param fecha    Fecha en formato deseado (dd/MM/yyyy).
     */
    private void cargarAlimentosConsumidos(final String comida, final AlimentoAdapter adapter, final String fecha) {
        String tipoComida = comida.substring(0, 1).toUpperCase() + comida.substring(1).toLowerCase();
        String url = "http://192.168.1.16/obtener_alimentos_por_fecha.php?user_id=" + userId + "&comida=" + tipoComida + "&fecha=" + fecha;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("DiarioFragment", "Respuesta del servidor para " + comida + ": " + response.toString());
                        List<String> nombresAlimentos = parseJsonToNombresAlimentos(response);
                        adapter.setAlimentos(nombresAlimentos);

                        // Sumar las calorías consumidas de esta comida al totalCalorias
                        totalCalorias += calcularTotalCalorias(response);
                        TextView txtTotalCalorias = requireView().findViewById(R.id.txtTotalCalorias);
                        txtTotalCalorias.setText("Total de calorías consumidas: " + totalCalorias);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DiarioFragment", "Error al cargar alimentos para " + comida + ": " + error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jsonArrayRequest);
    }

    /**
     * Convierte la respuesta JSON en una lista de nombres de alimentos.
     *
     * @param jsonArray Respuesta JSON.
     * @return Lista de nombres de alimentos.
     */
    private List<String> parseJsonToNombresAlimentos(JSONArray jsonArray) {
        List<String> detallesAlimentos = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objetoAlimento = jsonArray.getJSONObject(i);
                String nombre = objetoAlimento.getString("nombre");
                String cantidad = objetoAlimento.getString("cantidad_gramos");
                String calorias = objetoAlimento.getString("calorias_consumidas");

                detallesAlimentos.add(nombre + "                   "
                        + cantidad + "                   " + calorias);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detallesAlimentos;
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

    /**
     * Calcula el total de calorías a partir de la respuesta JSON.
     *
     * @param jsonArray Respuesta JSON.
     * @return Total de calorías consumidas.
     */
    private int calcularTotalCalorias(JSONArray jsonArray) {
        int totalCalorias = 0;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objetoAlimento = jsonArray.getJSONObject(i);
                int calorias = objetoAlimento.getInt("calorias_consumidas");
                totalCalorias += calorias;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return totalCalorias;
    }
}
