package com.example.listadetarefas;

import android.content.Context;
import android.content.SharedPreferences; // Import necessário
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    private EditText editNome;
    private EditText editIdade;
    private MaterialButton btnGuardarPerfil;

    // Constantes para salvar o perfil (usando o mesmo arquivo de SharedPreferences)
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_USER_NAME = "UserName";
    private static final String KEY_USER_AGE = "UserAge";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar os componentes
        editNome = view.findViewById(R.id.editNome);
        editIdade = view.findViewById(R.id.editIdade);
        btnGuardarPerfil = view.findViewById(R.id.btnGuardarPerfil);

        // Adicionar listener ao botão Guardar
        btnGuardarPerfil.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String idade = editIdade.getText().toString().trim();

            if (nome.isEmpty() || idade.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                // 1. Acessar o SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // 2. Guardar os valores
                editor.putString(KEY_USER_NAME, nome);
                editor.putString(KEY_USER_AGE, idade);
                editor.apply(); // Salva as alterações

                Toast.makeText(getContext(), "Perfil salvo!", Toast.LENGTH_LONG).show();

                // 3. Voltar para a tela de Ajustes
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }
}