package com.android.mediapipe.rrecView;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mediapipe.FirstPageActivity;
import com.android.mediapipe.MainActivity;
import com.android.mediapipe.R;
import com.android.mediapipe.model.App;

import java.util.ArrayList;

public class AppRecView extends RecyclerView.Adapter<AppRecView.AppViewHolder> {
    String TAG = "Adpter";
    ArrayList<App> applist ;
    Activity context;

    public AppRecView(ArrayList<App> applist, FirstPageActivity firstPageActivity) {
        this.applist = applist;
        context = firstPageActivity;

    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_app_layout,parent,false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.iv.setImageResource(R.drawable.ic_launcher_background);
        holder.tv.setText(applist.get(position).packageName);
        Log.d(TAG, "onBindViewHolder: "+applist.get(position).packageName);
        holder.tv.setOnClickListener(view -> {
            enterPIP();
            context.startActivity(new Intent(context, MainActivity.class));


            Log.d(TAG, "onBindViewHolder: integt trigerr");
            context.startActivity(applist.get(position).i);

        });

    }

    @Override
    public int getItemCount() {
        return applist.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.applogo);
            tv= itemView.findViewById(R.id.appname);
        }
    }
    private void enterPIP(){
        Log.d(TAG, "enterPIP: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "enterPIP: nnnnnn");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "enterPIP: ooooooooooo");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.d(TAG, "enterPIP: ssssss");
                    context.setPictureInPictureParams(new PictureInPictureParams.Builder()
                            .setAspectRatio(Rational.ZERO)
                            .setAutoEnterEnabled(false)
                            .build());
                }
            }
            context.enterPictureInPictureMode();

        }
    }
}
