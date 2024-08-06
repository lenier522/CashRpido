package cu.lenier.cashrpido.bottomsheet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

import cu.lenier.cashrpido.R;

public class BottomSheetLenguaje extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_language_selection, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_language);
        Button buttonApplyLanguage = view.findViewById(R.id.button_apply_language);

        // Obtener idioma guardado y preseleccionarlo
        String savedLanguage = getSavedLanguage();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getTag().equals(savedLanguage)) {
                radioButton.setChecked(true);
                break;
            }
        }

        buttonApplyLanguage.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
                String languageCode = (String) selectedRadioButton.getTag();
                setAppLocale(languageCode);
                Toast.makeText(getContext(), "Idioma cambiado a " + selectedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Por favor selecciona un idioma", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setAppLocale(String languageCode) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Resources resources = activity.getResources();
            Configuration configuration = resources.getConfiguration();
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            // Save the selected language to SharedPreferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", languageCode);
            editor.apply();

            // Restart the activity to apply the language change
            Intent refresh = new Intent(activity, activity.getClass());
            activity.startActivity(refresh);
            activity.finish();
        }
    }

    private String getSavedLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString("language", "es"); // Default to Spanish if no language is saved
    }
}
