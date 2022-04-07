package com.android.mediapipe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.android.mediapipe.model.App;
import com.android.mediapipe.rrecView.AppRecView;
import com.android.mediapipe.service.SmartAutoClickerService;

import java.util.ArrayList;
import java.util.List;

public class FirstPageActivity extends AppCompatActivity {
    String[] applist = new String[10];
    ArrayList<App> apps = new ArrayList<>();
    RecyclerView rc;
    SwitchCompat serviceBtn;
    final int requestCode = 109;

    SmartAutoClickerService.LocalService localService;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        serviceBtn  = findViewById(R.id.servicebtn);
        localService = SmartAutoClickerService.Companion.getLocalServiceInstance();
    serviceBtn.setChecked(SmartAutoClickerService.Companion.getStatus());

        serviceBtn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (localService == null){
                localService = SmartAutoClickerService.Companion.getLocalServiceInstance();
            }
            if(isChecked){
                localService.start();
                Log.d("serrr", "onCreate: Service started");
            }
            else {
                localService.stop();
                Log.d("serrr", "onCreate: Service stopped");

            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    requestCode);
        }
        if (isAccessibilityServiceEnabled(this,SmartAutoClickerService.class)){
            localService.start();
        }else{
            Intent settingsIntent = new Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);

        }


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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

}