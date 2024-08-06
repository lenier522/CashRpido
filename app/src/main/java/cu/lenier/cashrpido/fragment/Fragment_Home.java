package cu.lenier.cashrpido.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cu.lenier.cashrpido.ActivityAgregar;
import cu.lenier.cashrpido.ActivityQuitar;
import cu.lenier.cashrpido.R;
import cu.lenier.cashrpido.sql.ResultadoContract;
import cu.lenier.cashrpido.sql.ResultadoDbHelper;

public class Fragment_Home extends Fragment {

    private TextView cash, tarjeta;
    private ResultadoDbHelper dbHelper;
    private DecimalFormat formatoDecimal;
    private FloatingActionButton fab;

    private TextView idRopa, idTransporte, idMascotas, idCasa,
            idFood, idBaby, idPhone, idFitness, idEduc, idElect, idAgua, idInternet;

    private LinearLayout lnRopa, lnTransporte, lnMascotas, lnCasa,
            lnFood, lnBaby, lnPhone, lnFitness, lnEduc, lnElect, lnAgua, lnInternet;

    private Calendar calendar;
    private String currentDate;
    private Button dateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Cambiar el título del Toolbar
        if (getActivity() != null) {
            getActivity().setTitle(R.string.title_home);
        }

        idRopa = view.findViewById(R.id.id_ropa);
        idTransporte = view.findViewById(R.id.id_transporte);
        idMascotas = view.findViewById(R.id.id_mascotas);
        idCasa = view.findViewById(R.id.id_casa);
        idFood = view.findViewById(R.id.id_food);
        idBaby = view.findViewById(R.id.id_baby);
        idPhone = view.findViewById(R.id.id_phone);
        idFitness = view.findViewById(R.id.id_fitness);
        idEduc = view.findViewById(R.id.id_educ);
        idElect = view.findViewById(R.id.id_elect);
        idAgua = view.findViewById(R.id.id_agua);
        idInternet = view.findViewById(R.id.id_inter);


        lnRopa = view.findViewById(R.id.ln_ropa);
        lnTransporte = view.findViewById(R.id.ln_transporte);
        lnMascotas = view.findViewById(R.id.ln_mascotas);
        lnCasa = view.findViewById(R.id.ln_casa);
        lnFood = view.findViewById(R.id.ln_food);
        lnBaby = view.findViewById(R.id.ln_baby);
        lnPhone = view.findViewById(R.id.ln_phone);
        lnFitness = view.findViewById(R.id.ln_fitness);
        lnEduc = view.findViewById(R.id.ln_educ);
        lnElect = view.findViewById(R.id.ln_elect);
        lnAgua = view.findViewById(R.id.ln_agua);
        lnInternet = view.findViewById(R.id.ln_inter);

        lnRopa.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Ropa");
            }
        });

        lnTransporte.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Transporte");
            }
        });

        lnMascotas.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Mascotas");
            }
        });

        lnCasa.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Casa");
            }
        });

        lnFood.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Comida");
            }
        });

        lnBaby.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Bebes");
            }
        });

        lnPhone.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Móvil");
            }
        });

        lnFitness.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Fitness");
            }
        });

        lnEduc.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Educacion");
            }
        });
        lnElect.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Electricidad");
            }
        });
        lnAgua.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
            } else {
                abrirActivityQuitar("Agua");
                }
        });
        lnInternet.setOnClickListener(v -> {
            if (isDatabaseEmpty()) {
                showDatabaseEmptyMessage();
                } else {
                abrirActivityQuitar("Internet");
            }
        });


        cash = view.findViewById(R.id.textView3);
        tarjeta = view.findViewById(R.id.textView2);

        fab = view.findViewById(R.id.floatingActionButton);

        dbHelper = new ResultadoDbHelper(getActivity());
        formatoDecimal = new DecimalFormat("#0.00");

        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate = dateFormat.format(calendar.getTime());

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityAgregar.class);
            startActivity(intent);
        });


        dateButton = view.findViewById(R.id.date_button); // Botón para seleccionar la fecha
        if (dateButton != null) {
            dateButton.setText(currentDate);// Establecer la fecha actual al iniciar la aplicación
            dateButton.setOnClickListener(v -> showDatePickerDialog());
        } else {
            Log.e("Fragment_Home", "dateButton is null");
        }

        mostrarResultados();
        actualizarTextViewsDesdeSharedPreferences(currentDate); // Actualiza con la fecha actual por defecto

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarResultados();
        actualizarTextViewsDesdeSharedPreferences(currentDate); // Nueva función para actualizar los TextViews.
    }

    private void showDatePickerDialog() {
        // Crear un rango de restricciones para el selector de fechas si es necesario
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Crear el MaterialDatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(R.string.seleccionar_fecha);
        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Obtener la fecha seleccionada
            calendar.setTimeInMillis(selection);

            // Restar un día a la fecha seleccionada
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            // Formatear la fecha seleccionada
            currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());

            // Actualiza el texto del botón
            dateButton.setText(currentDate);

            // Actualiza los TextViews
            actualizarTextViewsDesdeSharedPreferences(currentDate);
        });

        datePicker.show(getParentFragmentManager(), datePicker.toString());
    }

    private void abrirActivityQuitar(String categoria) {
        Intent intent = new Intent(getActivity(), ActivityQuitar.class);
        intent.putExtra("categoria", categoria);
        startActivityForResult(intent, 1); // Usamos startActivityForResult para manejar el resultado.
    }

    private void actualizarTextViewsDesdeSharedPreferences(String date) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);

        String ropa = sharedPreferences.getString(date + "_Ropa", "0.00");
        String transporte = sharedPreferences.getString(date + "_Transporte", "0.00");
        String mascotas = sharedPreferences.getString(date + "_Mascotas", "0.00");
        String casa = sharedPreferences.getString(date + "_Casa", "0.00");
        String food = sharedPreferences.getString(date + "_Comida", "0.00");
        String baby = sharedPreferences.getString(date + "_Bebes", "0.00");
        String phone = sharedPreferences.getString(date + "_Móvil", "0.00");
        String fitness = sharedPreferences.getString(date + "_Fitness", "0.00");
        String educ = sharedPreferences.getString(date + "_Educacion", "0.00");
        String elect = sharedPreferences.getString(date + "_Electricidad", "0.00");
        String agua = sharedPreferences.getString(date + "_Agua", "0.00");
        String internet = sharedPreferences.getString(date + "_Internet", "0.00");

        idRopa.setText("$" + ropa);
        idTransporte.setText("$" + transporte);
        idMascotas.setText("$" + mascotas);
        idCasa.setText("$" + casa);
        idFood.setText("$" + food);
        idBaby.setText("$" + baby);
        idPhone.setText("$" + phone);
        idFitness.setText("$" + fitness);
        idEduc.setText("$" + educ);
        idElect.setText("$" + elect);
        idAgua.setText("$" + agua);
        idInternet.setText("$" + internet);
    }

    private void mostrarResultados() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                ResultadoContract.ResultadoEntry.COLUMN_RESULTADO,
                ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA
        };

        Cursor cursor = db.query(
                ResultadoContract.ResultadoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resultado = cursor.getString(cursor.getColumnIndexOrThrow(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow(ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA));

                // Verifica si el resultado es un número válido antes de usarlo
                if (esNumeroValido(resultado)) {
                    String resultadoFormateado = formatoDecimal.format(Double.parseDouble(resultado));
                    if (categoria.equals("Efectivo")) {
                        cash.setText("$" + resultadoFormateado);
                    } else if (categoria.equals("Tarjeta")) {
                        tarjeta.setText("$" + resultadoFormateado);
                    }
                } else {
                    // Maneja el caso en que el resultado no es un número válido
                    if (categoria.equals("Efectivo")) {
                        cash.setText("$0.00");
                    } else if (categoria.equals("Tarjeta")) {
                        tarjeta.setText("$0.00");
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            cash.setText("$0.00");
            tarjeta.setText("$0.00");
        }
        db.close();
    }

    private boolean esNumeroValido(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDatabaseEmpty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + ResultadoContract.ResultadoEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    private void showDatabaseEmptyMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.la_base_de_datos_est_vac_a)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }


}