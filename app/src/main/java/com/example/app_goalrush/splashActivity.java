package com.example.app_goalrush;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class splashActivity extends AppCompatActivity {
    ProgressBar splashProgress;
    int SPLASH_TIME = 7000; //This is 7 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        splashProgress =findViewById(R.id.splashProgress);
        playProgress();


        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mySignIn= new Intent(splashActivity.this, MainActivity.class);
                startActivity(mySignIn);
                finish();
            }

        }, SPLASH_TIME);
    }
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress,"progress",100)
                .setDuration(5000)
                .start();
    }


    }

