package com.example.fitnesscoval.ui.calculadora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fitnesscoval.R;

/**
 * Fragmento para la calculadora de calorías.
 * Permite a los usuarios calcular las calorías diarias recomendadas según su peso, altura, edad, género,
 * nivel de actividad y objetivo (mantener, perder o ganar peso).
 */
public class CalculadoraCalorias extends Fragment {

    private EditText editTextWeight, editTextHeight, editTextAge;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;
    private Spinner spinnerActivityLevel, spinnerGoal, spinnerDeficit, spinnerSurplus;
    private Button buttonCalculate;
    private TextView textViewResult;

    /**
     * Infla la interfaz de usuario del fragmento y configura los componentes de la interfaz.
     * También establece listeners para los componentes interactivos.
     *
     * @param inflater           El LayoutInflater utilizado para inflar la interfaz de usuario.
     * @param container          El ViewGroup al que se debe adjuntar el fragmento.
     * @param savedInstanceState La instancia guardada del fragmento, si la hay.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.calculadora_calorias, container, false);

        editTextWeight = root.findViewById(R.id.editTextWeight);
        editTextHeight = root.findViewById(R.id.editTextHeight);
        editTextAge = root.findViewById(R.id.editTextAge);
        radioGroupGender = root.findViewById(R.id.radioGroupGender);
        radioButtonMale = root.findViewById(R.id.radioButtonMale);
        radioButtonFemale = root.findViewById(R.id.radioButtonFemale);
        spinnerActivityLevel = root.findViewById(R.id.spinnerActivityLevel);
        spinnerGoal = root.findViewById(R.id.spinnerGoal);
        spinnerDeficit = root.findViewById(R.id.spinnerDeficit);
        spinnerSurplus = root.findViewById(R.id.spinnerSurplus);
        buttonCalculate = root.findViewById(R.id.buttonCalculate);
        textViewResult = root.findViewById(R.id.textViewResult);

        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_levels, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityLevelAdapter);

        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.goals, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);

        ArrayAdapter<CharSequence> deficitAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.deficits, android.R.layout.simple_spinner_item);
        deficitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeficit.setAdapter(deficitAdapter);

        ArrayAdapter<CharSequence> surplusAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.surpluses, android.R.layout.simple_spinner_item);
        surplusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSurplus.setAdapter(surplusAdapter);

        // Listener para mostrar u ocultar el spinner de déficit o superávit según el objetivo seleccionado
        spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                if (selectedGoal.equals("Bajar grasa corporal")) {
                    spinnerDeficit.setVisibility(View.VISIBLE);
                    spinnerSurplus.setVisibility(View.GONE);
                } else if (selectedGoal.equals("Aumentar masa muscular")) {
                    spinnerDeficit.setVisibility(View.GONE);
                    spinnerSurplus.setVisibility(View.VISIBLE);
                } else {
                    spinnerDeficit.setVisibility(View.GONE);
                    spinnerSurplus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No es necesario implementar nada aquí
            }
        });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalories();
            }
        });

        return root;
    }

    /**
     * Calcula las calorías diarias recomendadas según los datos ingresados por el usuario.
     * Utiliza la fórmula de Harris-Benedict para calcular el metabolismo basal (BMR) y lo ajusta
     * según el nivel de actividad física y el objetivo de acondicionamiento físico.
     */
    private void calculateCalories() {
        // Obtener los valores ingresados por el usuario
        String resultText;
        double weight = Double.parseDouble(editTextWeight.getText().toString());
        double height = Double.parseDouble(editTextHeight.getText().toString());
        int age = Integer.parseInt(editTextAge.getText().toString());
        int genderId = radioGroupGender.getCheckedRadioButtonId();
        boolean isMale = genderId == R.id.radioButtonMale;
        int activityLevel = spinnerActivityLevel.getSelectedItemPosition();

        // Calcular las calorías según la fórmula de Harris-Benedict
        double bmr;
        if (isMale) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) +5; // Para hombres
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) -161; // Para mujeres
        }

        // Ajustar las calorías según el nivel de actividad física
        double calories;
        switch (activityLevel) {
            case 0: // Sedentario
                calories = bmr * 1.2;
                break;
            case 1: // Moderado
                calories = bmr * 1.5;
                break;
            case 2: // Activo
                calories = bmr * 1.7;
                break;
            case 3: // Muy activo
                calories = bmr * 1.9;
                break;
            default:
                calories = 0;
        }

        // Obtener el objetivo seleccionado por el usuario
        int goalIndex = spinnerGoal.getSelectedItemPosition();
        String[] goals = getResources().getStringArray(R.array.goals);
        String selectedGoal = goals[goalIndex];

        // Ajustar las calorías según el objetivo seleccionado
        switch (selectedGoal) {
            case "Mantener peso corporal":
                // No es necesario ajuste, las calorías serán las calculadas previamente
                break;
            case "Bajar grasa corporal":
                resultText = "Calorías diarias recomendadas para bajar grasa corporal:\n";
                int deficitIndex = spinnerDeficit.getSelectedItemPosition();
                switch (deficitIndex) {
                    case 0: // Déficit ligero
                        calories *= 0.9;
                        resultText += String.format("Déficit ligero: %.2f", calories);
                        break;
                    case 1: // Déficit moderado
                        calories *= 0.8;
                        resultText += String.format("Déficit moderado: %.2f", calories);
                        break;
                    case 2: // Déficit agresivo
                        calories *= 0.7;
                        resultText += String.format("Déficit agresivo: %.2f", calories);
                        break;
                    default:
                        break;
                }
                break;
            case "Aumentar masa muscular":
                resultText = "Calorías diarias recomendadas para aumentar masa muscular:\n";
                int surplusIndex = spinnerSurplus.getSelectedItemPosition();
                switch (surplusIndex) {
                    case 0: // Superávit ligero
                        calories *= 1.1;
                        resultText += String.format("Superávit ligero: %.2f", calories);
                        break;
                    case 1: // Superávit moderado
                        calories *= 1.2;
                        resultText += String.format("Superávit moderado: %.2f", calories);
                        break;
                    case 2: // Superávit agresivo
                        calories *= 1.3;
                        resultText += String.format("Superávit agresivo: %.2f", calories);
                        break;
                    default:
                        break;
                }
                break;
            default:
                textViewResult.setText("Seleccione un objetivo válido");
                return;
        }

        // Mostrar el resultado
        textViewResult.setText(String.format("Calorías diarias recomendadas: %.2f", calories));
    }
}
