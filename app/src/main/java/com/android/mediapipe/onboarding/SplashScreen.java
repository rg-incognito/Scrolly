package com.android.mediapipe.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.android.mediapipe.FirstPageActivity;
import com.android.mediapipe.MainActivity;
import com.android.mediapipe.R;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            SharedPreferences myshredpref = getSharedPreferences("myprefrerance", 0);
            int entry = myshredpref.getInt("entry", 0);
            if (entry==0){
                Intent i = new Intent(SplashScreen.this, AppIntroActivity.class);
                startActivity(i);
                finish();
            }else{
                Intent i = new Intent(SplashScreen.this, FirstPageActivity.class);
                startActivity(i);
                finish();
            }
        },3000);




    }
}