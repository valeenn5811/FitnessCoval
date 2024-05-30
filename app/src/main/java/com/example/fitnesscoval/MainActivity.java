package com.example.fitnesscoval;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.example.fitnesscoval.preferencias.PreferencesActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesscoval.databinding.ActivityMainBinding;

/**
 * Actividad principal que gestiona la navegación entre los fragmentos del menú y las acciones del AppBar.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    /**
     * Método llamado cuando se crea la actividad.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_estadísticas, R.id.nav_calculadora, R.id.nav_planes_entrenamiento, R.id.nav_diario, R.id.nav_dietas)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * Método llamado para crear el menú de opciones en la barra de herramientas.
     *
     * @param menu El menú en el que se inflarán los elementos de la barra de herramientas.
     * @return Verdadero para mostrar el menú, falso en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Método llamado cuando se selecciona un elemento en la barra de herramientas.
     *
     * @param item El elemento de la barra de herramientas seleccionado.
     * @return Verdadero si el evento fue manejado, falso en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        } else if (id == R.id.menuAbout) {
            Toast.makeText(this, "Desarrollado por Coval", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método llamado para permitir que la navegación hacia arriba en la barra de herramientas funcione correctamente.
     *
     * @return Verdadero si la navegación fue manejada correctamente, falso en caso contrario.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
