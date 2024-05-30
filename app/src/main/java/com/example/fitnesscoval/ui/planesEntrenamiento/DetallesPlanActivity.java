package com.example.fitnesscoval.ui.planesEntrenamiento;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnesscoval.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Esta clase representa la actividad que muestra los detalles de un plan de entrenamiento
 * y permite al usuario activar un nuevo plan si lo desea.
 */
public class DetallesPlanActivity extends AppCompatActivity {

    private int planId;
    private int userId;
    private Integer planActivoId; // Variable para almacenar el ID del plan activo

    /**
     * Método llamado cuando la actividad se crea por primera vez.
     * Se encarga de inicializar la interfaz de usuario y configurar los listeners.
     * También obtiene detalles del plan y verifica el plan activo del usuario.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_plan);

        // Desactivar la validación del certificado SSL
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Obtener el ID del plan del intent
        planId = getIntent().getIntExtra("planId", -1);

        // Obtener el ID del usuario
        userId = getUserId();

        // Obtener detalles del plan
        obtenerDetallesPlan(planId, this);

        // Obtener el plan activo del usuario
        obtenerPlanActivo(userId); // Llamamos al método para obtener el plan activo

        // Configurar el botón de activar plan
        Button activarPlanButton = findViewById(R.id.activar_plan_button);
        activarPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si el plan activo coincide con el plan que se está intentando activar
                if (planActivoId != null && planId == planActivoId) {
                    // El plan que intentas activar ya está activo
                    Toast.makeText(DetallesPlanActivity.this, "Este plan ya está activo", Toast.LENGTH_SHORT).show();
                } else {
                    // Si no hay ningún plan activo o el plan que se está intentando activar es diferente al activo
                    // Activar el nuevo plan
                    if (planActivoId != null) {
                        // Si hay un plan activo, mostrar el diálogo de desactivación solo si el plan a activar es diferente
                        if (planId != planActivoId) {
                            activarNuevoPlanDespuesDeDesactivarExistente(planId, userId);
                        } else {
                            // El plan que intentas activar es el mismo que ya está activo
                            Toast.makeText(DetallesPlanActivity.this, "Este plan ya está activo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No hay ningún plan activo, simplemente activar el nuevo plan
                        activarPlan(planId, userId);
                    }
                }
            }
        });
    }

    /**
     * Método para obtener los detalles del plan de entrenamiento desde el servidor.
     * Esto incluye el nombre, la descripción y los ejercicios asociados con el plan.
     *
     * @param planId  ID del plan del cual obtener los detalles.
     * @param context Contexto de la actividad actual.
     */
    private void obtenerDetallesPlan(int planId, final Context context) {
        String url = "http://192.168.1.16/DetallesPlan.php?planId=" + planId;

        JsonObjectRequest planRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener y mostrar el nombre y la descripción del plan
                            String nombre = response.getString("nombre");
                            String descripcion = response.getString("descripcion");

                            TextView nombreTextView = findViewById(R.id.nombre_plan);
                            nombreTextView.setText(nombre);

                            TextView descripcionTextView = findViewById(R.id.descripcion_plan);
                            descripcionTextView.setText(descripcion);

                            // Obtener la lista de ejercicios relacionados
                            JSONArray ejerciciosArray = response.getJSONArray("ejercicios");

                            // Obtener el contenedor de ejercicios
                            LinearLayout ejerciciosContainer = findViewById(R.id.ejercicios_container);
                            ejerciciosContainer.removeAllViews(); // Limpiar vistas anteriores

                            for (int i = 0; i < ejerciciosArray.length(); i++) {
                                JSONObject ejercicio = ejerciciosArray.getJSONObject(i);
                                String nombreEjercicio = ejercicio.getString("nombre");
                                String descripcionEjercicio = ejercicio.getString("descripcion");
                                String imagenEjercicio = ejercicio.optString("imagen", "");

                                // Crear un LinearLayout para el ejercicio (nombre, descripción e imagen)
                                LinearLayout ejercicioLayout = new LinearLayout(context);
                                ejercicioLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                ejercicioLayout.setOrientation(LinearLayout.VERTICAL);
                                ejerciciosContainer.addView(ejercicioLayout);

                                // Mostrar el nombre del ejercicio
                                TextView txtNombreEjercicio = new TextView(context);
                                txtNombreEjercicio.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                txtNombreEjercicio.setText(nombreEjercicio);
                                txtNombreEjercicio.setTypeface(null, android.graphics.Typeface.BOLD); // Negrita
                                ejercicioLayout.addView(txtNombreEjercicio);

                                // Mostrar la descripción del ejercicio
                                TextView txtDescripcionEjercicio = new TextView(context);
                                txtDescripcionEjercicio.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                txtDescripcionEjercicio.setText(descripcionEjercicio);
                                ejercicioLayout.addView(txtDescripcionEjercicio);

                                if (imagenEjercicio != null && !imagenEjercicio.isEmpty() && !imagenEjercicio.equals("")) {
                                    // Crear y configurar el ImageView para la imagen del ejercicio
                                    ImageView imageView = new ImageView(context);
                                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                            600, 600));
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Centrar la imagen
                                    ejercicioLayout.addView(imageView);

                                    // Llamar a cargarImagen() para cargar la imagen en el ImageView
                                    cargarImagen(imagenEjercicio, imageView);
                                } else {
                                    // Si la URL de la imagen es null o "null", simplemente no agregamos ninguna imagen
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                    }
                });

        // Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }

    /**
     * Método para cargar una imagen desde una URL y mostrarla en un ImageView.
     *
     * @param url       URL de la imagen a cargar.
     * @param imageView ImageView donde se mostrará la imagen cargada.
     */
    private void cargarImagen(String url, final ImageView imageView) {
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Mostrar la imagen en el ImageView
                        imageView.setImageBitmap(bitmap);
                    }
                }, 600, 600, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                        Log.e("DetallesPlanActivity", "Error al cargar la imagen: " + error.getMessage());
                    }
                });

        // Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    /**
     * Método para activar un plan para el usuario especificado.
     *
     * @param planId El ID del plan que se va a activar.
     * @param userId El ID del usuario para quien se activará el plan.
     */
    private void activarPlan(int planId, int userId) {
        String url = "http://192.168.1.16/ActivarPlan.php?planId=" + planId + "&id=" + userId;

        JsonObjectRequest planRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor (puede ser un mensaje de éxito o error)
                        try {
                            String message = response.getString("message");
                            Toast.makeText(DetallesPlanActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Actualizar SharedPreferences para indicar que el usuario tiene un plan activo
                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                            editor.putBoolean("PlanActivo", true);
                            editor.putInt("PlanActivoId", planId);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Mostrar un Toast indicando que ocurrió un error
                            Toast.makeText(DetallesPlanActivity.this, "Error al activar el plan", Toast.LENGTH_SHORT).show();
                            // Registrar un mensaje de error en el log
                            Log.e("DetallesPlanActivity", "Error al activar el plan: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                        // Mostrar un Toast indicando que ocurrió un error
                        Toast.makeText(DetallesPlanActivity.this, "Error al activar el plan", Toast.LENGTH_SHORT).show();
                        // Registrar un mensaje de error en el log
                        Log.e("DetallesPlanActivity", "Error al activar el plan: " + error.getMessage());
                    }
                });

        // Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }

    /**
     * Método para mostrar un diálogo de confirmación para desactivar el plan existente antes de activar un nuevo plan.
     *
     * @param listener El listener para manejar la acción del usuario.
     */
    private void mostrarDialogoDesactivarPlan(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ya tienes un plan activo");
        builder.setMessage("¿Quieres desactivar el plan existente y activar este nuevo?");
        builder.setPositiveButton("Sí", listener);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada si el usuario cancela
            }
        });
        builder.show();
    }

    /**
     * Método para activar un nuevo plan después de desactivar el plan existente.
     *
     * @param nuevoPlanId El ID del nuevo plan que se va a activar.
     * @param userId      El ID del usuario para quien se activará el nuevo plan.
     */
    private void activarNuevoPlanDespuesDeDesactivarExistente(final int nuevoPlanId, final int userId) {
        mostrarDialogoDesactivarPlan(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                desactivarPlan(userId, new PlanDesactivadoListener() {
                    @Override
                    public void onPlanDesactivado() {
                        activarPlan(nuevoPlanId, userId);
                    }
                });
            }
        });
    }

    /**
     * Método para desactivar el plan para el usuario especificado.
     *
     * @param userId   El ID del usuario para quien se desactivará el plan.
     * @param listener El listener para manejar la acción después de desactivar el plan.
     */
    private void desactivarPlan(int userId, final PlanDesactivadoListener listener) {
        String url = "http://192.168.1.16/DesactivarPlan.php?id=" + userId;

        JsonObjectRequest planRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor (puede ser un mensaje de éxito o error)
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                listener.onPlanDesactivado(); // Llamar al listener cuando el plan se desactiva con éxito
                            } else {
                                // Mostrar un Toast indicando que ocurrió un error
                                Toast.makeText(DetallesPlanActivity.this, "Error al desactivar el plan", Toast.LENGTH_SHORT).show();
                                // Registrar un mensaje de error en el log
                                Log.e("DetallesPlanActivity", "Error al desactivar el plan: " + response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Mostrar un Toast indicando que ocurrió un error
                            Toast.makeText(DetallesPlanActivity.this, "Error al desactivar el plan", Toast.LENGTH_SHORT).show();
                            // Registrar un mensaje de error en el log
                            Log.e("DetallesPlanActivity", "Error al desactivar el plan: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                        // Mostrar un Toast indicando que ocurrió un error
                        Toast.makeText(DetallesPlanActivity.this, "Error al desactivar el plan", Toast.LENGTH_SHORT).show();
                        // Registrar un mensaje de error en el log
                        Log.e("DetallesPlanActivity", "Error al desactivar el plan: " + error.getMessage());
                    }
                });

        // Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }

    /**
     * Interfaz para escuchar cuando se desactiva un plan con éxito.
     */
    private interface PlanDesactivadoListener {
        void onPlanDesactivado();
    }
    /**
     * Método para obtener el ID del usuario actual.
     *
     * @return El ID del usuario actual.
     */
    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("Id", -1); // -1 es un valor predeterminado si no se encuentra el ID de usuario
        Log.d("DetallesPlanActivity", "ID de usuario: " + userId); // Agregar registro de depuración
        return userId;
    }

    /**
     * Método para obtener información sobre el plan activo del usuario especificado.
     *
     * @param userId El ID del usuario para quien se obtendrá la información del plan activo.
     */
    private void obtenerPlanActivo(int userId) {
        String url = "http://192.168.1.16/ObtenerPlanActivo.php?id=" + userId;

        JsonObjectRequest planRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.isNull("plan_activo")) {
                                planActivoId = response.getInt("plan_activo");
                                Log.d("DetallesPlanActivity", "Usuario con ID " + userId + " tiene el plan activo con ID: " + planActivoId);
                            } else {
                                planActivoId = null; // No hay ningún plan activo para este usuario
                                Log.d("DetallesPlanActivity", "Usuario con ID " + userId + " no tiene un plan activo.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("DetallesPlanActivity", "Error al obtener el plan activo: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DetallesPlanActivity", "Error al obtener el plan activo: " + error.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(planRequest);
    }
}
