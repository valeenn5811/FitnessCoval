package com.example.fitnesscoval.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.MainActivity;
import com.example.fitnesscoval.R;
import com.example.fitnesscoval.ui.bbdd.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LoginActivity permite a los usuarios iniciar sesión en la aplicación.
 * Si el inicio de sesión es exitoso, el usuario es redirigido a la actividad principal de la aplicación.
 * Si el usuario no tiene una cuenta, puede hacer clic en el botón "Crear cuenta" para registrarse.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonCreateAccount;

    /**
     * Este método se llama cuando la actividad se está creando.
     * Aquí se configuran las vistas y se asignan los listeners a los botones.
     * @param savedInstanceState información sobre el estado previo de la actividad, si está disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(this);

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateActivity.class));
            }
        });
    }

    /**
     * Método invocado cuando se hace clic en el botón de inicio de sesión.
     * Se valida la entrada del usuario y se procesa el inicio de sesión.
     * @param v La vista que ha sido clickeada.
     */
    @Override
    public void onClick(View v) {
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Por favor, ingrese nombre de usuario y contraseña.")
                    .setNegativeButton("OK", null)
                    .create().show();
        } else {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(LoginActivity.this, "Iniciando Sesión", Toast.LENGTH_SHORT).show();

                            int Id = jsonResponse.getInt("id");
                            String Username = jsonResponse.getString("username");

                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Id", Id);
                            editor.putString("username", Username);
                            editor.apply();
                            Log.d("LoginActivity", "Usuario con ID y Nombre " + Id + Username + " ha iniciado sesión");

                            obtenerPlanActivo(Id); // Obtener y registrar el plan activo

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = jsonResponse.getString("message");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(errorMessage)
                                    .setNegativeButton("Reintentar", null)
                                    .create().show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error de análisis JSON", "Error: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }
    }

    /**
     * Método para obtener y registrar el plan activo del usuario.
     * @param userId El ID del usuario para el cual se obtendrá el plan activo.
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
                                Log.d("LoginActivity", "Usuario con ID " + userId + " tiene el plan activo con ID: " + planActivoId);
                            } else {
                                Log.d("LoginActivity", "Usuario con ID " + userId + " no tiene un plan activo.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LoginActivity", "Error al obtener el plan activo: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LoginActivity", "Error al obtener el plan activo: " + error.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }
}