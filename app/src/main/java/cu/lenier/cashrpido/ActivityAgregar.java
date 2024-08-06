package cu.lenier.cashrpido;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

import cu.lenier.cashrpido.sql.ResultadoContract;
import cu.lenier.cashrpido.sql.ResultadoDbHelper;

public class ActivityAgregar extends AppCompatActivity {

    private static final String SUMA = "+";
    private static final String RESTA = "-";
    private static final String MULTIPLICACION = "*";
    private static final String DIVISION = "/";
    private static final String PORCENTAJE = "%";
    private String operacionActual = "";
    private double primerNumero = Double.NaN;
    private double segundoNumero = Double.NaN;
    private TextView tvTemp;
    private TextView tvResult;
    private DecimalFormat formatoDecimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        formatoDecimal = new DecimalFormat("#.##########");
        tvTemp = findViewById(R.id.tvTemp);
        tvResult = findViewById(R.id.tvResult);
    }

    public void cambiarOperador(View b) {
        if (!tvTemp.getText().toString().isEmpty() || !Double.isNaN(primerNumero)) {
            calcular();
            Button boton = (Button) b;
            if (boton.getText().toString().trim().equals("÷")) {
                operacionActual = "/";
            } else if (boton.getText().toString().trim().equals("X")) {
                operacionActual = "*";
            } else {
                operacionActual = boton.getText().toString().trim();
            }
            if (tvTemp.getText().toString().isEmpty()) {
                tvTemp.setText(tvResult.getText());
            }

            tvResult.setText(formatoDecimal.format(primerNumero) + operacionActual);
            tvTemp.setText("");
        }
    }

    public void calcular() {
        try {
            if (!Double.isNaN(primerNumero)) {
                if (tvTemp.getText().toString().isEmpty()) {
                    tvTemp.setText(tvResult.getText().toString());
                }
                segundoNumero = Double.parseDouble(tvTemp.getText().toString());
                tvTemp.setText("");

                switch (operacionActual) {
                    case SUMA:
                        primerNumero = primerNumero + segundoNumero;
                        break;
                    case RESTA:
                        primerNumero = primerNumero - segundoNumero;
                        break;
                    case MULTIPLICACION:
                        primerNumero = primerNumero * segundoNumero;
                        break;
                    case DIVISION:
                        primerNumero = primerNumero / segundoNumero;
                        break;
                    case PORCENTAJE:
                        primerNumero = primerNumero % segundoNumero;
                        break;
                }
            } else {
                primerNumero = Double.parseDouble(tvTemp.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seleccionarNumero(View b) {
        Button boton = (Button) b;
        tvTemp.setText(tvTemp.getText().toString() + boton.getText().toString());
    }

    public void igual(View b) {
        calcular();
        tvResult.setText(formatoDecimal.format(primerNumero));
        operacionActual = "";
    }

    public void borrar(View b) {
        Button boton = (Button) b;
        if (boton.getText().toString().trim().equals("C")) {
            if (!tvTemp.getText().toString().isEmpty()) {
                CharSequence datosActuales = tvTemp.getText();
                tvTemp.setText(datosActuales.subSequence(0, datosActuales.length() - 1));
            } else {
                primerNumero = Double.NaN;
                segundoNumero = Double.NaN;
                tvTemp.setText("");
                tvResult.setText("");
            }
        } else if (boton.getText().toString().trim().equals("CA")) {
            primerNumero = Double.NaN;
            segundoNumero = Double.NaN;
            tvTemp.setText("");
            tvResult.setText("");
        }
    }

    public void agregar(View v) {
        showPaymentDialog();
    }

    @SuppressLint("SetTextI18n")
    private void showPaymentDialog() {
        // Verificar si hay un resultado de la calculadora
        if (tvResult.getText().toString().isEmpty()) {
            // Mostrar un Snackbar si no hay ningún resultado
            Snackbar.make(findViewById(android.R.id.content), R.string.primero_realiza_un_c_lculo, Snackbar.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.alert_dialog_agregar, null);

        // Acceder al TextView del AlertDialog
        TextView textViewResultado = dialogView.findViewById(R.id.dialog_text);

        // Obtener el resultado actual de la calculadora
        String resultadoCalculadora = tvResult.getText().toString();

        // Establecer el resultado en el TextView del AlertDialog
        textViewResultado.setText(getString(R.string.seleccione_la_categor_a_para_agregar) + resultadoCalculadora + "$");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.btn_agregar, null); // Configuramos el OnClickListener más tarde

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_payment);
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                // Mostrar un mensaje si no se ha seleccionado ninguna opción
                Toast.makeText(this,R.string.por_favor_seleccione_una_categor_a, Toast.LENGTH_SHORT).show();
            } else {
                String categoria = "";
                if (selectedId == R.id.radio_cash) {
                    categoria = "Efectivo";
                } else if (selectedId == R.id.radio_card) {
                    categoria = "Tarjeta";
                }

                // Verificar si el resultado de la calculadora es NaN
                if (Double.isNaN(primerNumero)) {
                    Toast.makeText(this, R.string.el_resultado_de_la_calculadora_no_es_v_lido, Toast.LENGTH_SHORT).show();
                } else {
                    // Insertar el resultado y la categoría en la base de datos
                    insertarResultadoEnBaseDeDatos(resultadoCalculadora, categoria);

                    // Cerrar el diálogo
                    dialog.dismiss();
                }
            }
        });
    }

    private void insertarResultadoEnBaseDeDatos(String resultado, String categoria) {
        // Validar que el resultado es un número válido
        if (!esNumeroValido(resultado)) {
            Toast.makeText(this, R.string.resultado_no_v_lido, Toast.LENGTH_SHORT).show();
            return;
        }

        ResultadoDbHelper dbHelper = new ResultadoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Verificar si ya existe un resultado para la misma categoría
        Cursor cursor = db.rawQuery("SELECT * FROM " + ResultadoContract.ResultadoEntry.TABLE_NAME +
                " WHERE " + ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA + "=?", new String[]{categoria});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") double valorExistente = cursor.getDouble(cursor.getColumnIndex(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO));
            double nuevoValor = Double.parseDouble(resultado);
            double resultadoSumado = valorExistente + nuevoValor;

            ContentValues values = new ContentValues();
            values.put(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO, resultadoSumado);

            int rowsAffected = db.update(ResultadoContract.ResultadoEntry.TABLE_NAME, values,
                    ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA + "=?", new String[]{categoria});

            if (rowsAffected > 0) {
                Toast.makeText(this, R.string.resultado_actualizado_con_xito, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_al_actualizar_el_resultado, Toast.LENGTH_SHORT).show();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO, resultado);
            values.put(ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA, categoria);

            long newRowId = db.insert(ResultadoContract.ResultadoEntry.TABLE_NAME, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, R.string.resultado_agregado_con_xito, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_al_agregar_el_resultado, Toast.LENGTH_SHORT).show();
            }
        }

        cursor.close();
        db.close();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
}
