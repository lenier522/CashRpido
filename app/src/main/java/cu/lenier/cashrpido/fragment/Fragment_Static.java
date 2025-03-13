package cu.lenier.cashrpido.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cu.lenier.cashrpido.R;

public class Fragment_Static extends Fragment {

    private PieChart pieChart;
    private Button dateButton;
    private Calendar calendar;
    private String currentDate;
    private TextView noDataTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_static, container, false);

        // Cambiar el título del Toolbar
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.estad_sticas));
        }

        pieChart = view.findViewById(R.id.pie_chart);
        dateButton = view.findViewById(R.id.date_button);
        noDataTextView = view.findViewById(R.id.no_data_text_view);
        calendar = Calendar.getInstance();

        // Cargar datos del día actual por defecto
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate = dateFormat.format(calendar.getTime());

        if (dateButton != null) {
            dateButton.setText(currentDate);
            dateButton.setOnClickListener(v -> showMaterialDatePicker());
        } else {
            Log.e("Fragment_Static", "dateButton is null");
        }

        // Cargar y mostrar el gráfico inicial con los datos del día actual
        cargarDatosYMostrarGrafico(currentDate);

        return view;
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(R.string.seleccione_una_fecha);
        MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            // Ajustar Fecha
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String selectDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());
            cargarDatosYMostrarGrafico(selectDate);
            dateButton.setText(selectDate);
        });

        materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void cargarDatosYMostrarGrafico(String date) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(date)) {
                String category = key.substring(date.length() + 1);
                Object value = entry.getValue();
                if (value instanceof String) {
                    try {
                        float amount = Float.parseFloat((String) value);
                        entries.add(new PieEntry(amount, category));
                    } catch (NumberFormatException e) {
                        // No hacemos nada si no podemos convertir el valor a float
                    }
                }
            }
        }

        if (entries.isEmpty()) {
            // Mostrar el texto de error cuando no hay datos
            noDataTextView.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            noDataTextView.setText(R.string.no_hay_datos_disponibles_para_la_fecha_seleccionada);
        } else {
            // Mostrar el gráfico cuando hay datos
            noDataTextView.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);

            PieDataSet dataSet = new PieDataSet(entries, getString(R.string.gastos_por_categor_a));
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pieData = new PieData(dataSet);
            pieData.setValueTextSize(12f);
            pieData.setValueTextColor(Color.WHITE);

            pieChart.setData(pieData);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(40f);
            pieChart.setTransparentCircleRadius(45f);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setEntryLabelTextSize(12f);

            Description description = new Description();
            description.setText(getString(R.string.gastos_por_categor_a) + " - " + date);
            pieChart.setDescription(description);

            // Agregar animaciones
            pieChart.animateY(1400, Easing.EaseInOutQuad);
            pieChart.animateX(1400, Easing.EaseInOutQuad);

            pieChart.invalidate(); // Refresh the chart
        }
    }
}
