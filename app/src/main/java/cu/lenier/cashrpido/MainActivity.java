package cu.lenier.cashrpido;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import cu.lenier.cashrpido.fragment.Fragment_Home;
import cu.lenier.cashrpido.fragment.Fragment_Setting;
import cu.lenier.cashrpido.fragment.Fragment_Static;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private boolean exitPressedOnce = false;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppLocale(); // Establecer el idioma antes de inflar la vista
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Home()).commit();
            } else if (id == R.id.nav_static) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Static()).commit();
            } else if (id == R.id.nav_settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment_Setting()).commit();
            } else if (id == R.id.nav_salir) {
                ConstraintLayout dialog = (ConstraintLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_dialog, null);

                TextView txt_title = dialog.findViewById(R.id.dialog_text_title);
                TextView txt_descrip = dialog.findViewById(R.id.dialog_text_descrip);
                Button btn = dialog.findViewById(R.id.dialog_btn);
                Button btn2 = dialog.findViewById(R.id.dialog_btn2);

                txt_title.setText(R.string.salir);
                txt_descrip.setText(R.string.seguro_que_desea_salir_de_la_app);
                btn.setText(R.string.salir);
                btn2.setText(R.string.cancelar);

                btn.setOnClickListener(v -> finish());

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialog);
                final AlertDialog alert = builder.create();

                btn2.setOnClickListener(v -> alert.dismiss());

                alert.setCancelable(false);
                if (alert.getWindow() != null) {
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alert.show();
            }

            navigationView.setCheckedItem(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


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


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (exitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.exitPressedOnce = true;
            Snackbar.make(drawerLayout, R.string.presione_de_nuevo_para_salir_de_la_app, Snackbar.LENGTH_SHORT).show();

            // Restablecer el flag después de un tiempo (e.g., 2 segundos)
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitPressedOnce = false;
                }
            }, 2000);
        }
    }
}