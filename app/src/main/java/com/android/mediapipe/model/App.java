package com.android.mediapipe.model;

import android.content.Intent;

import androidx.annotation.NonNull;

public class App {
    public Intent i;
    int icName;

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String packageName;

    public App(Intent i, int icName, String packageName, String appName) {
        this.i = i;
        this.icName = icName;
        this.packageName = packageName;
        AppName = appName;
    }

    String AppName;





    public int getIcName() {
        return icName;
    }

    public void setIcName(int icName) {
        this.icName = icName;
    }




    @NonNull
    @Override
    public String toString() {
        return "App{" +
                "i=" + i +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    public Intent getI() {
        return i;
    }

    public void setI(Intent i) {
        this.i = i;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
