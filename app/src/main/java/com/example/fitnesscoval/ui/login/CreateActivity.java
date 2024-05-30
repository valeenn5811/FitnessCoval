package com.example.fitnesscoval.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.MainActivity;
import com.example.fitnesscoval.R;
import com.example.fitnesscoval.ui.bbdd.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Actividad para crear una nueva cuenta de usuario.
 * Permite al usuario ingresar un nombre de usuario, contraseña y correo electrónico,
 * y registra la cuenta en la base de datos.
 */
public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonCreateAccount.setOnClickListener(this);
    }

    /**
     * Método onClick para manejar el evento de clic en el botón de creación de cuenta.
     * Valida los campos de entrada del usuario y registra la nueva cuenta en la base de datos.
     *
     * @param v La vista que se ha hecho clic (en este caso, el botón de creación de cuenta).
     */
    @Override
    public void onClick(View v) {
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
            builder.setMessage("Por favor, complete todos los campos.")
                    .setNegativeButton("OK", null)
                    .create().show();
        } else if (!isValidEmail(email)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
            builder.setMessage("Por favor, ingrese un correo electrónico válido.")
                    .setNegativeButton("OK", null)
                    .create().show();
        } else {
            Response.Listener<String> respoListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject jsonResponse = new JSONObject(s);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            int Id = jsonResponse.getInt("id");
                            String Username = jsonResponse.getString("username");

                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Id", Id);
                            editor.putString("username", Username);
                            editor.apply();

                            obtenerPlanActivo(Id); // Obtener y registrar el plan activo

                            Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonResponse.getString("message");
                            if (message.contains("nombre de usuario")) {
                                message = "El nombre de usuario ya está en uso.";
                            } else if (message.contains("correo electrónico")) {
                                message = "El correo electrónico ya está en uso.";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                            builder.setMessage(message)
                                    .setNegativeButton("Reintentar", null)
                                    .create().show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            RegisterRequest registerRequest = new RegisterRequest(username, password, email, respoListener);
            RequestQueue queue = Volley.newRequestQueue(CreateActivity.this);
            queue.add(registerRequest);
        }
    }

    /**
     * Método para obtener y registrar el plan activo del usuario recién registrado.
     *
     * @param userId El ID del usuario recién registrado.
     */
    private void obtenerPlanActivo(int userId) {
        String url = "http://192.168.1.16/ObtenerPlanActivo.php?id=" + userId;

        JsonObjectRequest planRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.isNull("plan_activo")) {
                                int planActivoId = response.getInt("plan_activo");
                                Log.d("CreateActivity", "Usuario con ID " + userId + " tiene el plan activo con ID: " + planActivoId);
                            } else {
                                Log.d("CreateActivity", "Usuario con ID " + userId + " no tiene un plan activo.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("CreateActivity", "Error al obtener el plan activo: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CreateActivity", "Error al obtener el plan activo: " + error.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }

    /**
     * Método para validar el formato del correo electrónico.
     *
     * @param email El correo electrónico a validar.
     * @return true si el correo electrónico tiene un formato válido, false de lo contrario.
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}