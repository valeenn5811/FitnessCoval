package com.example.fitnesscoval.preferencias;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta actividad permite al usuario cambiar su nombre de usuario, correo electrónico y contraseña.
 * Realiza las validaciones necesarias antes de enviar los cambios a un servidor mediante una solicitud HTTP POST.
 */
public class AccountSettingsActivity extends AppCompatActivity {

    private EditText etNuevoUsuario, etNuevaContrasena, etNuevoCorreo;
    private Button btnGuardarCambios;
    private String nombreUsuarioActual;
    private String correoUsuarioActual;

    /**
     * Método que se llama cuando se crea la actividad.
     * Inicializa las vistas y carga los valores actuales del nombre de usuario y correo electrónico.
     *
     * @param savedInstanceState Estado de la instancia guardada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        etNuevoUsuario = findViewById(R.id.etNuevoUsuario);
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        etNuevoCorreo = findViewById(R.id.etNuevoCorreo);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        nombreUsuarioActual = getNombreUsuario(this);
        correoUsuarioActual = getCorreoUsuario(this);

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });
    }

    /**
     * Método que guarda los cambios realizados por el usuario.
     * Valida los campos y envía una solicitud HTTP POST al servidor.
     */
    private void guardarCambios() {
        final String nuevoUsuario = etNuevoUsuario.getText().toString().trim();
        final String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
        final String nuevoCorreo = etNuevoCorreo.getText().toString().trim();

        if (nuevoUsuario.isEmpty() && nuevaContrasena.isEmpty() && nuevoCorreo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete al menos un campo para cambiar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevoCorreo.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
            Toast.makeText(this, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaContrasena.isEmpty() && nuevaContrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.16/account_settings.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            if (!nuevoUsuario.isEmpty()) {
                                saveNombreUsuario(nuevoUsuario);
                            }
                            Toast.makeText(AccountSettingsActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        } else if (response.trim().equals("same_email")) {
                            showConfirmationDialog("El correo electrónico ya está asociado a una cuenta. Ingresa otro correo.");
                        } else if (response.trim().equals("same_username")) {
                            showConfirmationDialog2("El nombre de usuario ya está en uso. Por favor, elige otro.");
                        } else {
                            Toast.makeText(AccountSettingsActivity.this, "Error: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AccountSettingsActivity.this, "Error al guardar cambios: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombreUsuarioActual", nombreUsuarioActual);
                params.put("correoUsuarioActual", correoUsuarioActual);

                if (!nuevoUsuario.isEmpty()) {
                    params.put("nuevoUsuario", nuevoUsuario);
                }

                if (!nuevaContrasena.isEmpty()) {
                    params.put("nuevaContrasena", nuevaContrasena);
                }

                if (!nuevoCorreo.isEmpty()) {
                    params.put("nuevoCorreo", nuevoCorreo);
                }

                return params;
            }
        };

        queue.add(stringRequest);
    }

    /**
     * Muestra un cuadro de diálogo de confirmación cuando el correo electrónico ya está asociado a otra cuenta.
     *
     * @param mensaje Mensaje a mostrar en el cuadro de diálogo.
     */
    private void showConfirmationDialog(final String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hacer nada y cerrar el diálogo.
                    }
                });
        builder.create().show();
    }

    /**
     * Muestra un cuadro de diálogo de confirmación cuando el nombre de usuario ya está en uso.
     *
     * @param mensaje Mensaje a mostrar en el cuadro de diálogo.
     */
    private void showConfirmationDialog2(final String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hacer nada y cerrar el diálogo.
                    }
                });
        builder.create().show();
    }

    /**
     * Guarda el nuevo nombre de usuario en las preferencias compartidas.
     *
     * @param nuevoUsuario El nuevo nombre de usuario.
     */
    private void saveNombreUsuario(String nuevoUsuario) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", nuevoUsuario);
        editor.apply();
    }

    /**
     * Obtiene el nombre de usuario actual de las preferencias compartidas.
     *
     * @param context El contexto de la actividad.
     * @return El nombre de usuario actual.
     */
    private String getNombreUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "");
    }

    /**
     * Obtiene el correo electrónico actual de las preferencias compartidas.
     *
     * @param context El contexto de la actividad.
     * @return El correo electrónico actual.
     */
    private String getCorreoUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getString("mail", "");
    }
}
