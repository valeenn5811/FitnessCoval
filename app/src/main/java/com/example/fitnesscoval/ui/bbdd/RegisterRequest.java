package com.example.fitnesscoval.ui.bbdd;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Una solicitud para registrar un nuevo usuario en el servidor.
 * Extiende StringRequest para realizar una solicitud HTTP POST y enviar los datos de registro al servidor.
 */
public class RegisterRequest extends StringRequest {

    public static final String REGISTER_REQUEST_URL="http://192.168.1.16/RegisterFit.php";
    private Map<String,String> params;

    /**
     * Crea una nueva instancia de RegisterRequest.
     *
     * @param username Nombre de usuario proporcionado por el usuario.
     * @param password Contraseña proporcionada por el usuario.
     * @param email    Correo electrónico proporcionado por el usuario.
     * @param listener Listener para manejar la respuesta del servidor.
     */
    public RegisterRequest(String username, String password, String email, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        // Validar el formato del correo electrónico antes de agregarlo a los parámetros
        if (isValidEmail(email)) {
            params.put("email", email);
        } else {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
    }

    @Nullable
    @Override
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Verifica si el formato del correo electrónico es válido.
     *
     * @param email El correo electrónico a validar.
     * @return true si el formato es válido, false de lo contrario.
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
