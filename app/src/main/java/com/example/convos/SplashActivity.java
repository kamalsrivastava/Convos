package com.example.convos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    TextView name;
    ImageView bng;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.imgLogo);
        name = findViewById(R.id.txtAppName);
        bng = findViewById(R.id.imgbng);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.topanimation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottomanimation);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        bng.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is already signed in
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // User is already signed in, navigate to the main activity
                    navigateToMainActivity();
                } else {
                    // User is not signed in, navigate to the login activity
                    navigateToLoginActivity();
                }
            }
        }, 3000);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
