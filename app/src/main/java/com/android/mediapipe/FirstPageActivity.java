package com.android.mediapipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FirstPageActivity extends AppCompatActivity {
    String[] applist = new String[10];
    ArrayList<App> apps = new ArrayList<>();
    RecyclerView rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        for (int i = 0; i < 10; i++) {
            applist[i] = "com.instagram.android";
        }
        for (int i = 0; i < applist.length; i++) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(applist[i]);
            App a = new App(launchIntent,applist[i]);
            Log.d("TAGne", "onCreate: "+a);
            apps.add(a);
        }
        rc = findViewById(R.id.appRecView);
        AppRecView appRecViewAdapter = new AppRecView(apps,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        rc.setAdapter(appRecViewAdapter);
rc.setLayoutManager(layoutManager);

    }


}