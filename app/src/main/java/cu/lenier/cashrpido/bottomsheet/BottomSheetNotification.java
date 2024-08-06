package cu.lenier.cashrpido.bottomsheet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.concurrent.TimeUnit;

import cu.lenier.cashrpido.R;
import cu.lenier.cashrpido.servicio.NotificationWorker;

public class BottomSheetNotification extends BottomSheetDialogFragment {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String SWITCH_STATE = "switchState";
    public static final String WORK_TAG = "NotificationWork";

    private MaterialSwitch swiNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_noti, container, false);

        swiNo = view.findViewById(R.id.switch_noti);

        // Restaurar el estado del Switch desde SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean switchState = sharedPreferences.getBoolean(SWITCH_STATE, false);
        swiNo.setChecked(switchState);

        swiNo.setText(switchState ? "Desactivar" : "Activar");

        swiNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startNotificationWork();
                    saveSwitchState(true);
                    swiNo.setText(R.string.desactivar);
                    Toast.makeText(requireContext(), R.string.notificaciones_activadas, Toast.LENGTH_SHORT).show();
                } else {
                    stopNotificationWork();
                    saveSwitchState(false);
                    swiNo.setText(R.string.activar);
                    Toast.makeText(requireContext(), R.string.notificaciones_desactivadas, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Método para iniciar el WorkManager
    private void startNotificationWork() {
        // Cancelar cualquier trabajo previo con la misma etiqueta
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(WORK_TAG);

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 60, TimeUnit.MINUTES)
                .addTag(WORK_TAG)
                .build();
        WorkManager.getInstance(requireContext()).enqueue(notificationWork);
    }

    // Método para detener el WorkManager
    private void stopNotificationWork() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(WORK_TAG);
    }

    // Método para guardar el estado del Switch en SharedPreferences
    private void saveSwitchState(boolean state) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH_STATE, state);
        editor.apply();
    }
}