package com.android.mediapipe;

import android.content.Intent;

import androidx.annotation.NonNull;

public class App {
    Intent i;

    public App(Intent i, String packageName) {
        this.i = i;
        this.packageName = packageName;
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

    String packageName;
}
