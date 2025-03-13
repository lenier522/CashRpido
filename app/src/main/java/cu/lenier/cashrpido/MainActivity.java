package cu.lenier.cashrpido;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.snackbar.Snackbar;
import com.lenier.update_chaker.UpdateChecker;

import java.util.Locale;

import cu.lenier.cashrpido.fragment.Fragment_Count;
import cu.lenier.cashrpido.fragment.Fragment_Home;
import cu.lenier.cashrpido.fragment.Fragment_Setting;
import cu.lenier.cashrpido.fragment.Fragment_Static;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView btnNavigation;
    private FloatingActionButton fab;
    private boolean exitPressedOnce = false;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppLocale(); // Establecer el idioma antes de inflar la vista
        setContentView(R.layout.activity_main);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = pinfo.versionCode;
        //   String currentVersionCode = String.valueOf(currentVersion);

        String jsonUrl = "https://perf3ctsolutions.com/update.json"; // URL del JSON
        UpdateChecker.checkForUpdate(this,currentVersion,jsonUrl,false);


        btnNavigation = findViewById(R.id.bottom_navigation);

        // Cargar el fragmento por defecto al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new Fragment_Home())
                    .commit();
        }

        btnNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Home()).commit();
            } else if (id == R.id.nav_static) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Static()).commit();
            } else if (id == R.id.nav_settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Setting()).commit();
            } else if (id == R.id.nav_card) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new Fragment_Count()).commit();
            }
            return true;
        });


        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityAgregar.class);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        if (exitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.exitPressedOnce = true;

        // Mostrar Snackbar en vez de Toast para mejor diseño
        Snackbar.make(findViewById(android.R.id.content), R.string.presione_de_nuevo_para_salir_de_la_app, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> exitPressedOnce = false, 2000); // Resetear flag después de 2 segundos
    }


    private void setAppLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = preferences.getString("language", "es"); // Idioma por defecto

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

}
