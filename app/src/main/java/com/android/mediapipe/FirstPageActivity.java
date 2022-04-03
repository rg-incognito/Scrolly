package com.android.mediapipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


import com.android.mediapipe.model.App;
import com.android.mediapipe.rrecView.AppRecView;

import java.util.ArrayList;

public class FirstPageActivity extends AppCompatActivity {
    String[] applist = new String[10];
    ArrayList<App> apps = new ArrayList<>();
    RecyclerView rc;
    final int requestCode = 109;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        startService(new Intent(this, SmartAutoClickerService.LocalService.class));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    requestCode);
        }

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

        for (int i = 0; i < 10; i++) {
            applist[i] = "com.vanced.android.youtube";
        }
        for (int i = 0; i < applist.length; i++) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(applist[i]);
            App a = new App(launchIntent, applist[i]);
            Log.d("TAGne", "onCreate: " + a);
            apps.add(a);
        }
        rc = findViewById(R.id.appRecView);
        AppRecView appRecViewAdapter = new AppRecView(apps, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rc.setAdapter(appRecViewAdapter);
        rc.setLayoutManager(layoutManager);

    }



}