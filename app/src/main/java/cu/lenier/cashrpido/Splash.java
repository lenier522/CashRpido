package cu.lenier.cashrpido;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView lottieAnimation = findViewById(R.id.lottieAnimation);
        TextView appName = findViewById(R.id.appName);

        // Cargar animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Aplicar animaciones
        appName.startAnimation(fadeIn);
        lottieAnimation.startAnimation(slideUp);

        // Transición a la siguiente actividad
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finaliza la actividad para que no regrese al splash
        }, 5000); // 5 segundos
    }
}