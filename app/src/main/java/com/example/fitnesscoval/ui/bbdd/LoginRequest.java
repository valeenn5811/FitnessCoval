package com.example.fitnesscoval.ui.bbdd;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Una solicitud para iniciar sesión en el servidor.
 * Extiende StringRequest para realizar una solicitud HTTP POST y enviar los datos de inicio de sesión al servidor.
 */
public class LoginRequest extends StringRequest {

    public static final String REGISTER_REQUEST_URL="http://192.168.1.16/LoginFit.php";
    private Map<String,String> params;

    /**
     * Crea una nueva instancia de LoginRequest.
     *
     * @param username Nombre de usuario proporcionado por el usuario.
     * @param password Contraseña proporcionada por el usuario.
     * @param listener Listener para manejar la respuesta del servidor.
     */
    public LoginRequest(String username, String password, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Nullable
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
