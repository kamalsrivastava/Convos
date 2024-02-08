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

    ImageView logo;
    TextView name;
    ImageView bng;
    Animation topAnim,bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo=findViewById(R.id.imgLogo);
        name=findViewById(R.id.txtAppName);
        bng=findViewById(R.id.imgbng);

        topAnim= AnimationUtils.loadAnimation(this, R.anim.topanimation);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottomanimation);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        bng.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}