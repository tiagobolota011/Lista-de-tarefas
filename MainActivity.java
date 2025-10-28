package com.example.listadetarefas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends AppCompatActivity {

    private Switch themeSwitch;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preferências para guardar o tema
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("DarkMode", false);

        // Aplicar tema salvo
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // Inicializar componentes
        themeSwitch = findViewById(R.id.themeSwitch);

        // Ajustar estado inicial do switch
        themeSwitch.setChecked(darkMode);

        // Listener do switch
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mostrarMensagem("Modo escuro ativado");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mostrarMensagem("Modo claro ativado");
            }
        });
    }

    private void mostrarMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
