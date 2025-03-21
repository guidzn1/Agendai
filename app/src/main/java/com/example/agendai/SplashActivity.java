package com.example.agendai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Tempo de exibição da splash (em milissegundos)
    private static final long SPLASH_DURATION = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageLogo = findViewById(R.id.imageLogo);
        // Carrega a animação de escala
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        imageLogo.startAnimation(scaleAnim);

        // Após o tempo definido, inicia a MainActivity e finaliza a splash
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
