package com.example.fitnesscoval.preferencias;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesscoval.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Esta actividad permite al usuario establecer sus preferencias, como nombre, apellidos, fecha de
 * nacimiento, altura, peso y género. Los datos ingresados por el usuario se guardan localmente en
 * SharedPreferences y se envían al servidor mediante una solicitud HTTP POST.
 */
public class PreferencesActivity extends AppCompatActivity {

    private EditText etFechaNacimiento;
    public Spinner spinnerGenero;

    /**
     * Método llamado cuando se crea la actividad.
     * Inicializa las vistas y establece los listeners de los botones.
     *
     * @param savedInstanceState Datos proporcionados si la actividad se está reanudando desde un estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spinnerGenero = findViewById(R.id.spinnerGenero);

        findViewById(R.id.btnGuardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPreferencias();
            }
        });

        findViewById(R.id.btnAccountSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAccountSettingsActivity();
            }
        });
        obtenerInformacionUsuario();
    }

    /**
     * Muestra un diálogo de selección de fecha cuando se hace clic en el EditText de la fecha de nacimiento.
     *
     * @param v La vista que se ha hecho clic.
     */
    public void showDatePickerDialog(View v) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        etFechaNacimiento.setText(sdf.format(calendar.getTime()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Guarda las preferencias ingresadas por el usuario.
     * Valida los campos y envía una solicitud HTTP POST al servidor para guardar los datos.
     */
    private void guardarPreferencias() {
        String nombre = ((EditText) findViewById(R.id.etNombre)).getText().toString();
        String apellidos = ((EditText) findViewById(R.id.etApellidos)).getText().toString();
        String fechaNacimiento = etFechaNacimiento.getText().toString();
        String altura = ((EditText) findViewById(R.id.etAltura)).getText().toString();
        String peso = ((EditText) findViewById(R.id.etPeso)).getText().toString();
        String genero = spinnerGenero.getSelectedItem().toString();

        if (nombre.isEmpty() || apellidos.isEmpty() || fechaNacimiento.isEmpty() || altura.isEmpty() || peso.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(fechaNacimiento);
            fechaNacimiento = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String fechaNacimientoFinal = fechaNacimiento;
        new Thread(() -> enviarAlServidor(nombre, apellidos, fechaNacimientoFinal, altura, peso, genero)).start();
    }

    /**
     * Envía los datos al servidor mediante una solicitud HTTP POST.
     * Los datos se envían como parámetros en la solicitud.
     *
     * @param nombre           Nombre del usuario.
     * @param apellidos        Apellidos del usuario.
     * @param fechaNacimiento  Fecha de nacimiento del usuario.
     * @param altura           Altura del usuario.
     * @param peso             Peso del usuario.
     * @param genero           Género del usuario.
     */
    private void enviarAlServidor(String nombre, String apellidos, String fechaNacimiento, String altura, String peso, String genero) {
        try {
            int userId = getUserId(this);
            URL url = new URL("http://192.168.1.16/info_usuarios.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(userId));
            params.put("nombre", nombre);
            params.put("apellidos", apellidos);
            params.put("fechaNacimiento", fechaNacimiento);
            params.put("altura", altura);
            params.put("peso", peso);
            params.put("genero", genero);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(param.getKey());
                postData.append('=');
                postData.append(param.getValue());
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            runOnUiThread(() -> {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al guardar preferencias", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Obtiene el ID de usuario almacenado en SharedPreferences.
     *
     * @param context El contexto de la aplicación.
     * @return El ID de usuario, o -1 si no se encuentra.
     */
    private int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("Id", -1);
    }

    /**
     * Abre la actividad AccountSettingsActivity.
     */
    private void abrirAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Obtiene la información del usuario del servidor y la muestra en los campos correspondientes.
     * Hace una solicitud HTTP POST al servidor para obtener los datos del usuario y luego los muestra
     * en los campos de la interfaz de usuario.
     */
    private void obtenerInformacionUsuario() {
        int userId = getUserId(this);
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
                        runOnUiThread(() -> {
                            ((EditText) findViewById(R.id.etNombre)).setText(userData[0]);
                            ((EditText) findViewById(R.id.etApellidos)).setText(userData[1]);
                            etFechaNacimiento.setText(userData[2]);
                            ((EditText) findViewById(R.id.etAltura)).setText(userData[3]);
                            ((EditText) findViewById(R.id.etPeso)).setText(userData[4]);
                            spinnerGenero.setSelection(obtenerIndiceGenero(this, userData[5]));
                        });

                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error al obtener información del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "ID de usuario no válido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Obtiene el índice del género en el array de géneros.
     *
     * @param context El contexto de la aplicación.
     * @param genero  El género a buscar.
     * @return El índice del género en el array de géneros, o 0 si no se encuentra.
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
}
