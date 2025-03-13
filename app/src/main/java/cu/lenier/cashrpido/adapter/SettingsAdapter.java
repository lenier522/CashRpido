package cu.lenier.cashrpido.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cu.lenier.cashrpido.MainActivity;
import cu.lenier.cashrpido.R;
import cu.lenier.cashrpido.bottomsheet.BottomSheetAbout;
import cu.lenier.cashrpido.bottomsheet.BottomSheetLenguaje;
import cu.lenier.cashrpido.bottomsheet.BottomSheetNotification;
import cu.lenier.cashrpido.sql.ResultadoDbHelper;


public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingItem> settingItems;
    private Context context;

    public SettingsAdapter(Context context, List<SettingItem> settingItems) {
        this.context = context;
        this.settingItems = settingItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem item = settingItems.get(position);
        holder.settingTitle.setText(item.getTitle());
        holder.settingIcon.setImageResource(item.getIconResId());



        holder.itemView.setOnClickListener(v -> {
            if (item.getTitle().equals(context.getString(R.string.acerca_de))) {
                BottomSheetAbout bottomSheet = new BottomSheetAbout();
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            } else if (item.getTitle().equals(context.getString(R.string.notificaciones))) {
                BottomSheetNotification bottomSheet = new BottomSheetNotification();
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            } else if (item.getTitle().equals(context.getString(R.string.cambiar_el_lenguaje_de_la_app))) {
                BottomSheetLenguaje bottomSheet = new BottomSheetLenguaje();
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            } else if (item.getTitle().equals(context.getString(R.string.compartir))) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.descarga_la_aplicaci_n_cashrapido_de_la_google_play));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.compartir_cashrapido));
                context.startActivity(shareIntent);
            } else if (item.getTitle().equals(context.getString(R.string.backup))) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_layout_backup, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                // Obtener referencias a vistas dentro del diálogo
                RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
                Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
                Button buttonAccept = dialogView.findViewById(R.id.button_accept);

                buttonCancel.setOnClickListener(v1 -> dialog.dismiss());
                buttonAccept.setOnClickListener(v2 -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // Verificar qué radio button está seleccionado
                    if (selectedId == R.id.radio_importar) {
                        importData();
                    } else if (selectedId == R.id.radio_exportar) {
                        exportData();
                    } else if (selectedId == R.id.radio_reiniciar) {
                        showResetConfirmationDialog();
                    } else {
                        Snackbar snackbar = Snackbar.make(dialogView, R.string.seleccione_una_opci_n, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

                dialog.show();
            }


            // Agregar más condiciones para otros elementos si es necesario
        });
    }

    private void importData() {
        File importDir = new File(context.getExternalFilesDir(null), "CashRapido/salvas");
        File backupFile = new File(importDir, "backup.cr");

        if (!backupFile.exists()) {
            Toast.makeText(context, R.string.archivo_de_respaldo_no_encontrado, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Leer el contenido del archivo
            FileInputStream fis = new FileInputStream(backupFile);
            byte[] buffer = new byte[(int) backupFile.length()];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer);
            JSONObject backupJson = new JSONObject(jsonStr);

            // Restaurar SharedPreferences
            JSONObject sharedPrefsJson = backupJson.getJSONObject("shared_prefs");
            Log.d("SharedPrefsImport", sharedPrefsJson.toString()); // Registro de depuración

            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            for (Iterator<String> it = sharedPrefsJson.keys(); it.hasNext(); ) {
                String key = it.next();
                Object value = sharedPrefsJson.get(key);
                if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Float) {
                    editor.putFloat(key, (Float) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else if (value instanceof Long) {
                    editor.putLong(key, (Long) value);
                } else if (value instanceof String) {
                    editor.putString(key, (String) value);
                }
            }
            editor.apply();

            // Restaurar base de datos
            String databaseBase64 = backupJson.getJSONObject("database").getString("database");
            byte[] databaseBuffer = Base64.decode(databaseBase64, Base64.DEFAULT);

            File dbFile = context.getDatabasePath(ResultadoDbHelper.DATABASE_NAME);
            FileOutputStream fos = new FileOutputStream(dbFile);
            fos.write(databaseBuffer);
            fos.close();

            Toast.makeText(context, context.getString(R.string.datos_importados_correctamente_desde) + backupFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_al_importar_los_datos, Toast.LENGTH_SHORT).show();
        }

        // Reiniciar la aplicación después de importar los datos
        restartApp();
    }


    private void restartApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        Runtime.getRuntime().exit(0);
    }




    //Metodo para exportar configuracion
    private void exportData() {
        try {
            // Crear JSON para respaldar SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            Map<String, ?> allEntries = sharedPreferences.getAll();
            JSONObject sharedPrefsJson = new JSONObject();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                sharedPrefsJson.put(entry.getKey(), entry.getValue());
            }

            Log.d("SharedPrefsExport", sharedPrefsJson.toString()); // Registro de depuración

            // Crear JSON para respaldar la base de datos
            File dbFile = context.getDatabasePath(ResultadoDbHelper.DATABASE_NAME);
            FileInputStream fis = new FileInputStream(dbFile);
            byte[] buffer = new byte[(int) dbFile.length()];
            fis.read(buffer);
            fis.close();
            String databaseBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);
            JSONObject databaseJson = new JSONObject();
            databaseJson.put("database", databaseBase64);

            // Crear JSON final de respaldo
            JSONObject backupJson = new JSONObject();
            backupJson.put("shared_prefs", sharedPrefsJson);
            backupJson.put("database", databaseJson);

            // Guardar JSON en un archivo
            File exportDir = new File(context.getExternalFilesDir(null), "CashRapido/salvas");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File backupFile = new File(exportDir, "backup.cr");
            FileOutputStream fos = new FileOutputStream(backupFile);
            fos.write(backupJson.toString().getBytes());
            fos.close();

            Toast.makeText(context, context.getString(R.string.datos_exportados_correctamente_a) + backupFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_al_exportar_los_datos, Toast.LENGTH_SHORT).show();
        }
    }





    //Metodo para restablecer la App
    private void showResetConfirmationDialog() {
        ConstraintLayout dialog = (ConstraintLayout) LayoutInflater.from(context)
                .inflate(R.layout.custom_dialog, null);

        TextView txt_title = dialog.findViewById(R.id.dialog_text_title);
        TextView txt_descrip = dialog.findViewById(R.id.dialog_text_descrip);
        Button btn = dialog.findViewById(R.id.dialog_btn);
        Button btn2 = dialog.findViewById(R.id.dialog_btn2);

        txt_title.setText(R.string.restablecer_app);
        txt_descrip.setText(R.string.est_s_seguro_de_que_deseas_restablecer_la_aplicacis);
        btn.setText(R.string.borrar);
        btn2.setText(R.string.cancelar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialog);
        final AlertDialog alert = builder.create();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetApp();
                alert.dismiss();
            }
        });

        btn2.setOnClickListener(v -> alert.dismiss());

        alert.setCancelable(false);
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alert.show();
    }

    private void resetApp() {
        // Eliminar todos los SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Eliminar la base de datos
        context.deleteDatabase(ResultadoDbHelper.DATABASE_NAME);

        // Reiniciar la actividad principal
        Intent intent = new Intent(context, MainActivity.class);  // Reemplaza MainActivity con tu actividad principal
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Limpiar la pila de actividades
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return settingItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView settingIcon;
        TextView settingTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            settingIcon = itemView.findViewById(R.id.settingIcon);
            settingTitle = itemView.findViewById(R.id.settingTitle);
        }
    }
}
