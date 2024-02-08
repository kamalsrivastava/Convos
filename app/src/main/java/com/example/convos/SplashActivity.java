package com.example.convos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 4000;

    private ImageView logo;
    private TextView name;
    private ImageView bng;
    private Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeViews();
        setAnimations();
        navigateToLogin();
    }

    private void initializeViews() {
        logo = findViewById(R.id.imgLogo);
        name = findViewById(R.id.txtAppName);
        bng = findViewById(R.id.imgbng);
    }

    private void setAnimations() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.topanimation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottomanimation);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        bng.setAnimation(bottomAnim);
    }

    private void navigateToLogin() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
