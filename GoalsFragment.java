package com.example.listadetarefas;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.button.MaterialButton;

import android.graphics.drawable.GradientDrawable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Color; // Import para Color.parseColor
import java.util.HashMap;
import java.util.Map;
import android.graphics.Typeface;

public class GoalsFragment extends Fragment {

    private EditText editNewGoal;
    private MaterialButton btnSaveGoal;
    private TextView tvGoalsPlaceholder;
    private LinearLayout linearLayoutGoalsContainer;

    // Contêineres de sub-tarefas (para a lista de itens)
    private LinearLayout linearLayoutSubtaskCasa, linearLayoutSubtaskTrabalho, linearLayoutSubtaskSaude;

    // Mapeamento de cor e contêineres
    private final String CATEGORY_COLOR_MAPPER(String category) {
        switch (category) {
            case "Casa":
            case "Trabalho":
            case "Estudos":
                return "#9C27B0";
            case "Saúde":
                return "#4CAF50";
            default:
                return "#FFC107";
        }
    }

    private Map<String, LinearLayout> subtaskContainers = new HashMap<>();
    private Map<String, TextView> categoryDetailViews = new HashMap<>();
    private Map<String, Boolean> isExpanded = new HashMap<>();

    // --- Estrutura de Dados para Metas (NÃO É BASE DE DADOS) ---
    private static class GoalItem {
        String titulo;
        String categoria;
        boolean concluida;

        GoalItem(String titulo, String categoria, boolean concluida) {
            this.titulo = titulo;
            this.categoria = categoria;
            this.concluida = concluida;
        }
    }

    private List<GoalItem> allGoalItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Inicializar componentes
        editNewGoal = view.findViewById(R.id.editNewGoal);
        btnSaveGoal = view.findViewById(R.id.btnSaveGoal);
        tvGoalsPlaceholder = view.findViewById(R.id.tvGoalsPlaceholder);
        linearLayoutGoalsContainer = view.findViewById(R.id.linearLayoutGoalsContainer);

        // ⬅️ CORREÇÃO DE ID: Os IDs no XML são 'linearLayoutSubtask...'
        linearLayoutSubtaskCasa = view.findViewById(R.id.linearLayoutSubtaskCasa);
        linearLayoutSubtaskTrabalho = view.findViewById(R.id.linearLayoutSubtaskTrabalho);
        linearLayoutSubtaskSaude = view.findViewById(R.id.linearLayoutSubtaskSaude);

        // Mapear contêineres de sub-tarefas
        subtaskContainers.put("Casa", linearLayoutSubtaskCasa);
        subtaskContainers.put("Trabalho", linearLayoutSubtaskTrabalho);
        subtaskContainers.put("Saúde", linearLayoutSubtaskSaude);

        // Inicializar estado de expansão
        isExpanded.put("Casa", false);
        isExpanded.put("Trabalho", false);
        isExpanded.put("Saúde", false);

        setupStaticHeaders(view);

        // Listener para o botão GUARDAR
        btnSaveGoal.setOnClickListener(v -> {
            String goalTitle = editNewGoal.getText().toString().trim();

            if (!goalTitle.isEmpty()) {
                showCategoryAndStatusDialog(goalTitle);
            } else {
                Toast.makeText(getContext(), "Digite um título para a meta.", Toast.LENGTH_SHORT).show();
            }
        });

        refreshGoalList();
        return view;
    }

    // Configura os cabeçalhos (Headers) e adiciona o clique para expandir/recolher
    private void setupStaticHeaders(View view) {
        String[] categories = {"Casa", "Trabalho", "Saúde"};
        int[] headerIds = {R.id.headerCasa, R.id.headerTrabalho, R.id.headerSaude};

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            View headerView = view.findViewById(headerIds[i]);
            String color = CATEGORY_COLOR_MAPPER(category);

            configureHeader(headerView, category, category, color);

            headerView.setOnClickListener(v -> {
                Boolean expanded = isExpanded.get(category);
                if (expanded == null) expanded = false;
                toggleSubtasks(category, !expanded);
            });
        }
    }

    // Método para expandir/recolher a lista de sub-tarefas
    private void toggleSubtasks(String category, boolean expand) {
        LinearLayout container = subtaskContainers.get(category);
        if (container != null) {
            container.setVisibility(expand ? View.VISIBLE : View.GONE);
            isExpanded.put(category, expand);
        }
    }


    private void configureHeader(View headerView, String category, String title, String colorHex) {
        TextView tvTitle = headerView.findViewById(R.id.tvCategoryTitle);
        TextView tvDetails = headerView.findViewById(R.id.tvCategoryDetails);
        TextView tvDot = headerView.findViewById(R.id.tvCategoryDot);

        tvTitle.setText(title);
        tvDetails.setText("0 tarefas • próxima: Nenhuma");
        categoryDetailViews.put(category, tvDetails);

        GradientDrawable drawableDot = new GradientDrawable();
        drawableDot.setShape(GradientDrawable.OVAL);
        drawableDot.setColor(Color.parseColor(colorHex));
        tvDot.setBackground(drawableDot);
    }

    // --- Lógica do Menu de Status ---

    // 1. Abre o menu pop-up de Alternância de Status
    private void showStatusToggleMenu(GoalItem item) {
        final String currentStatus = item.concluida ? "Concluída" : "Pendente";

        final String[] options = {
                item.concluida ? "Marcar como Pendente" : "Marcar como Concluída"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Opções para: " + item.titulo + " (" + currentStatus + ")");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Alternar status
                boolean newStatus = !item.concluida;
                updateGoalItemStatus(item, newStatus);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // 2. Atualiza o status do item na lista de dados e redesenha
    private void updateGoalItemStatus(GoalItem itemToUpdate, boolean newStatus) {
        for (GoalItem item : allGoalItems) {
            if (item == itemToUpdate) { // Compara a referência do objeto
                item.concluida = newStatus;
                break;
            }
        }

        refreshGoalList();

        String statusText = newStatus ? "Concluída" : "Pendente";
        Toast.makeText(getContext(), itemToUpdate.titulo + " marcado como: " + statusText, Toast.LENGTH_SHORT).show();
    }

    // Método para exibir o pop-up de seleção de categoria (para a sub-tarefa)
    private void showCategoryAndStatusDialog(String goalTitle) {
        final String[] categories = {"Casa", "Trabalho", "Saúde"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Meta: '" + goalTitle + "'");

        builder.setItems(categories, (dialog, which) -> {
            String selectedCategory = categories[which];
            showStatusDialog(goalTitle, selectedCategory);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showStatusDialog(String goalTitle, String category) {
        final String[] statuses = {"Concluída", "Pendente"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Definir Status Inicial");

        builder.setItems(statuses, (dialog, which) -> {
            boolean isConcluida = (which == 0); // 0 = Concluída, 1 = Pendente
            saveNewGoalAndRefresh(goalTitle, category, isConcluida);
            Toast.makeText(getContext(), "Sub-tarefa adicionada como " + statuses[which] + " em: " + category, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // --- Lógica de Salvar e Atualizar ---
    private void saveNewGoalAndRefresh(String title, String category, boolean isConcluida) {
        GoalItem newItem = new GoalItem(title, category, isConcluida); // Salva com o status
        allGoalItems.add(newItem);
        editNewGoal.setText(""); // Limpa o campo

        refreshGoalList();

        // Força a expansão da categoria onde o item foi adicionado
        toggleSubtasks(category, true);
        updateCategoryDetails(category);
        updatePlaceholderVisibility();
    }

    // Atualiza o contador de tarefas no cabeçalho
    private void updateCategoryDetails(String category) {
        TextView tvDetails = categoryDetailViews.get(category);
        if (tvDetails != null) {
            long totalCount = allGoalItems.stream().filter(item -> item.categoria.equals(category)).count();
            long completedCount = allGoalItems.stream().filter(item -> item.categoria.equals(category) && item.concluida).count();

            tvDetails.setText(totalCount + " tarefas • Concluídas: " + completedCount);
        }
    }


    // Desenha a lista principal e todas as sub-tarefas
    private void refreshGoalList() {
        // Limpa todos os contêineres de sub-tarefas
        linearLayoutSubtaskCasa.removeAllViews();
        linearLayoutSubtaskTrabalho.removeAllViews();
        linearLayoutSubtaskSaude.removeAllViews();

        // Adiciona o campo de entrada 'Adicionar sub-tarefa...' a cada container
        addStaticInputFields();

        // Adiciona as sub-tarefas dinamicamente
        for (GoalItem item : allGoalItems) {
            LinearLayout container = subtaskContainers.get(item.categoria);
            if (container != null) {
                View itemCard = createSubtaskView(item);
                container.addView(itemCard);
            }
        }

        // Garante que o estado de expansão seja respeitado (expandido/recolhido)
        toggleSubtasks("Casa", isExpanded.get("Casa"));
        toggleSubtasks("Trabalho", isExpanded.get("Trabalho"));
        toggleSubtasks("Saúde", isExpanded.get("Saúde"));

        updatePlaceholderVisibility();
        updateAllCategoryDetails();
    }

    // Adiciona o campo "Adicionar sub-tarefa..." e o botão a cada categoria
    private void addStaticInputFields() {
        String[] categories = {"Casa", "Trabalho", "Saúde"};
        for (String category : categories) {
            LinearLayout container = subtaskContainers.get(category);
            if (container != null) {

                LinearLayout inputContainer = new LinearLayout(getContext());
                inputContainer.setOrientation(LinearLayout.HORIZONTAL);
                inputContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                inputContainer.setPadding(0, 8, 0, 8);

                EditText etSubtask = new EditText(getContext());
                etSubtask.setHint("Adicionar sub-tarefa...");
                etSubtask.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                etSubtask.setPadding(12, 12, 12, 12);
                etSubtask.setBackgroundColor(Color.TRANSPARENT);
                etSubtask.setTextColor(getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary}).getColor(0, 0));

                MaterialButton btnAdd = new MaterialButton(getContext());
                btnAdd.setText("ADICIONAR");
                btnAdd.setPadding(12, 0, 12, 0);
                btnAdd.setCornerRadius(8);
                btnAdd.setMinimumHeight(0);

                // Implementação do clique do botão Adicionar sub-tarefa
                btnAdd.setOnClickListener(v -> {
                    String subtaskTitle = etSubtask.getText().toString().trim();
                    if (!subtaskTitle.isEmpty()) {
                        showCategoryAndStatusDialog(subtaskTitle);
                        etSubtask.setText("");
                    }
                });

                inputContainer.addView(etSubtask);
                inputContainer.addView(btnAdd);
                container.addView(inputContainer);
            }
        }
    }


    private void updateAllCategoryDetails() {
        for (String category : subtaskContainers.keySet()) {
            updateCategoryDetails(category);
        }
    }

    private void updatePlaceholderVisibility() {
        if (allGoalItems.isEmpty()) {
            tvGoalsPlaceholder.setVisibility(View.VISIBLE);
            linearLayoutGoalsContainer.setVisibility(View.GONE);
        } else {
            tvGoalsPlaceholder.setVisibility(View.GONE);
            linearLayoutGoalsContainer.setVisibility(View.VISIBLE);
        }
    }

    // Cria a View da Sub-tarefa com os ícones de status
    private View createSubtaskView(final GoalItem item) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 8, 0, 8);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 4);
        itemLayout.setLayoutParams(layoutParams);

        // --- 1. Ícone de Status (X ou Certo) ---
        TextView tvStatusIcon = new TextView(getContext());
        tvStatusIcon.setText(item.concluida ? "✓" : "X");
        tvStatusIcon.setTextSize(18);

        int statusColor = item.concluida ? Color.parseColor("#4CAF50") : Color.parseColor("#E53935");
        tvStatusIcon.setTextColor(statusColor);

        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setShape(GradientDrawable.OVAL);
        statusBg.setColor(Color.TRANSPARENT);
        statusBg.setStroke(2, statusColor);
        tvStatusIcon.setBackground(statusBg);

        tvStatusIcon.setPadding(8, 4, 8, 4);
        itemLayout.addView(tvStatusIcon);

        // --- 2. Texto da Sub-tarefa e Detalhes ---
        LinearLayout textContainer = new LinearLayout(getContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        textContainer.setPadding(12, 0, 12, 0);

        // Título
        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(item.titulo);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary}).getColor(0, 0));
        textContainer.addView(tvTitle);

        // Vence: [Data]
        TextView tvDueDate = new TextView(getContext());
        tvDueDate.setText("Vence: Hoje"); // Placeholder
        tvDueDate.setTextSize(12);
        tvDueDate.setTextColor(getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorSecondary}).getColor(0, 0));
        textContainer.addView(tvDueDate);

        itemLayout.addView(textContainer);

        // --- 3. Status Escrito (Concluída / Pendente) ---
        TextView tvStatusText = new TextView(getContext());
        tvStatusText.setText(item.concluida ? "Concluída" : "Pendente");
        tvStatusText.setTextSize(14);
        tvStatusText.setTextColor(item.concluida ? Color.parseColor("#4CAF50") : Color.parseColor("#E53935"));
        itemLayout.addView(tvStatusText);

        // --- 4. Ícone de Menu (⋮) com Listener de Ação ---
        TextView tvMenu = new TextView(getContext());
        tvMenu.setText("⋮");
        tvMenu.setTextSize(20);
        tvMenu.setPadding(8, 0, 0, 0);
        tvMenu.setTextColor(getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorSecondary}).getColor(0, 0));

        tvMenu.setOnClickListener(v -> showStatusToggleMenu(item));

        itemLayout.addView(tvMenu);


        return itemLayout;
    }
}