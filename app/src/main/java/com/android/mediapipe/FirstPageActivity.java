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
    ArrayList<String> applist = new ArrayList<>();
    ArrayList<Integer> iclist = new ArrayList<>();
    ArrayList<String> appnamelist = new ArrayList<>();
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



            applist.add("com.instagram.android");
            applist.add("com.google.android.youtube");
            applist.add("com.facebook.android");
            applist.add("com.linkedin.android");
            applist.add("com.twitter.android");

            iclist.add(R.drawable.insta);
            iclist.add(R.drawable.yt);
            iclist.add(R.drawable.fb);
            iclist.add(R.drawable.link);
            iclist.add(R.drawable.tw);

        appnamelist.add("Instagram");
        appnamelist.add("Youtube");
        appnamelist.add("Facebook");
        appnamelist.add("LinkedIn");
        appnamelist.add("Twitter");


        for (int i = 0; i < applist.size(); i++) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(applist.get(i));
            App a = new App(launchIntent,iclist.get(i), applist.get(i),appnamelist.get(i));
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