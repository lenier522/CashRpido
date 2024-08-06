package cu.lenier.cashrpido.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cu.lenier.cashrpido.R;
import cu.lenier.cashrpido.adapter.SettingItem;
import cu.lenier.cashrpido.adapter.SettingsAdapter;

public class Fragment_Setting extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        // Cambiar el título del Toolbar
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.configuraci_n));
        }

        RecyclerView recyclerView = view.findViewById(R.id.settingsRecyclerView);

        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(getString(R.string.agregar_m_s_tarjetas_de_cr_dito), R.drawable.ic_credit_card));
        settingItems.add(new SettingItem(getString(R.string.cambiar_el_lenguaje_de_la_app), R.drawable.ic_language));
        settingItems.add(new SettingItem(getString(R.string.cambiar_el_tema_de_la_aplicaci_n), R.drawable.ic_theme));
        settingItems.add(new SettingItem(getString(R.string.obtener_licencia_de_pago), R.drawable.ic_license));
        settingItems.add(new SettingItem(getString(R.string.compartir), R.drawable.ic_share));
        settingItems.add(new SettingItem(getString(R.string.notificaciones), R.drawable.ic_notifications));
        settingItems.add(new SettingItem(getString(R.string.backup), R.drawable.ic_backup));
        settingItems.add(new SettingItem(getString(R.string.acerca_de), R.drawable.ic_about));

        SettingsAdapter adapter = new SettingsAdapter(getContext(), settingItems);
        recyclerView.setAdapter(adapter);


        return view;
    }


}
