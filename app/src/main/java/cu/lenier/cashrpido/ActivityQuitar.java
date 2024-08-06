package cu.lenier.cashrpido;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cu.lenier.cashrpido.sql.ResultadoContract;
import cu.lenier.cashrpido.sql.ResultadoDbHelper;

public class ActivityQuitar extends AppCompatActivity {
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

    Button btn;
    private DecimalFormat formatoDecimal;
    private String categoria; // Agregar variable para la categoría

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quitar);

        formatoDecimal = new DecimalFormat("#.##########");
        tvTemp = findViewById(R.id.tvTemp);
        tvResult = findViewById(R.id.tvResult);

        btn = findViewById(R.id.button24);
        btn.setText(R.string.quitar);

        findViewById(R.id.button24).setOnClickListener(v -> showPaymentDialog());

        // Obtener la categoría pasada desde MainActivity
        categoria = getIntent().getStringExtra("categoria");
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

    private void showPaymentDialog() {
        if (tvResult.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.primero_realiza_un_c_lculo, Snackbar.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.alert_dialog_agregar, null);
        TextView textViewResultado = dialogView.findViewById(R.id.dialog_text);

        String resultadoCalculadora = tvResult.getText().toString();
        textViewResultado.setText(getString(R.string.seleccione_la_categor_a_para_quitar) + resultadoCalculadora + "$");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.quitar, null); // Configuramos el OnClickListener más tarde

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_payment);
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, R.string.por_favor_seleccione_una_categor_a, Toast.LENGTH_SHORT).show();
            } else {
                String categoria = "";
                if (selectedId == R.id.radio_cash) {
                    categoria = "Efectivo";
                } else if (selectedId == R.id.radio_card) {
                    categoria = "Tarjeta";
                }

                if (!esNumeroValido(resultadoCalculadora)) {
                    Toast.makeText(this, R.string.resultado_no_v_lido, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un intent para devolver el resultado a MainActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categoriaId", this.categoria); // Usar la categoría recibida
                returnIntent.putExtra("resultado", resultadoCalculadora);
                setResult(Activity.RESULT_OK, returnIntent);

                // Restar el resultado y la categoría en la base de datos
                restarResultadoEnBaseDeDatos(resultadoCalculadora, categoria);

                // Guardar el resultado en SharedPreferences
                guardarResultadoEnSharedPreferences(resultadoCalculadora);

                dialog.dismiss();
                finish(); // Terminar la actividad después de enviar el resultado
            }
        });
    }

    private void guardarResultadoEnSharedPreferences(String nuevoResultado) {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String valorExistente = sharedPreferences.getString(currentDate + "_" + categoria, "0.00");

        double valorExistenteDouble = Double.parseDouble(valorExistente);
        double nuevoResultadoDouble = Double.parseDouble(nuevoResultado);

        double resultadoActualizado = valorExistenteDouble + nuevoResultadoDouble;

        editor.putString(currentDate + "_" + categoria, String.valueOf(resultadoActualizado));
        editor.apply();

    }

    private void restarResultadoEnBaseDeDatos(String resultado, String categoria) {
        ResultadoDbHelper dbHelper = new ResultadoDbHelper(this);

        // Obtener la base de datos en modo escritura
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Verificar si ya existe un resultado para la misma categoría
        Cursor cursor = db.rawQuery("SELECT * FROM " + ResultadoContract.ResultadoEntry.TABLE_NAME +
                " WHERE " + ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA + "=?", new String[]{categoria});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") double valorExistente = cursor.getDouble(cursor.getColumnIndex(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO));
            double nuevoValor = Double.parseDouble(resultado);
            double resultadoSumado = valorExistente - nuevoValor;

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
            double nuevoValor = Double.parseDouble(resultado);
            double resultadoSumado = -nuevoValor; // Debes restar, así que el valor es negativo

            ContentValues values = new ContentValues();
            values.put(ResultadoContract.ResultadoEntry.COLUMN_RESULTADO, resultadoSumado);
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
    }


    private boolean esNumeroValido(String numero) {
        try {
            double valor = Double.parseDouble(numero);
            return !Double.isNaN(valor);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}