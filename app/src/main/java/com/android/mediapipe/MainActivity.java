package com.android.mediapipe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mediapipe.service.SmartAutoClickerService;
import com.android.mediapipe.utils.HandsResultGlRenderer;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivitykkkkk";
    SmartAutoClickerService.LocalService localService =
            SmartAutoClickerService.Companion.getLocalServiceInstance();
    private Hands hands;


    private static final boolean RUN_ON_GPU = true;

    public enum InputSource {
        UNKNOWN,
        CAMERA
    }

    private InputSource inputSource = InputSource.UNKNOWN;

    private CameraInput cameraInput;


    private SolutionGlSurfaceView<HandsResult> glSurfaceView;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localService.start();
        setupLiveDemoUiComponents();
//        checkOverlayPermission();


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame ->
                    hands.send(textureFrame));
            glSurfaceView.post(this::startCamera);
            glSurfaceView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource == InputSource.CAMERA) {
//            glSurfaceView.setVisibility(View.GONE);
//            cameraInput.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private Bitmap rotateBitmap(Bitmap inputBitmap, InputStream imageData)
            throws IOException {
        int orientation =
                new ExifInterface(imageData)
                        .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            return inputBitmap;
        }
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                matrix.postRotate(0);
        }
        return Bitmap.createBitmap(
                inputBitmap, 0, 0, inputBitmap.getWidth(), inputBitmap.getHeight(), matrix, true);
    }

    private void setupLiveDemoUiComponents() {

                    if (inputSource == InputSource.CAMERA) {
                        return;
                    }
                    stopCurrentPipeline();
                    setupStreamingModePipeline(InputSource.CAMERA);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: enterpip");

    }
//    private void enterPIP(){
//        Log.d(TAG, "enterPIP: ");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Log.d(TAG, "enterPIP: nnnnnn");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Log.d(TAG, "enterPIP: ooooooooooo");
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    Log.d(TAG, "enterPIP: ssssss");
//                    setPictureInPictureParams(new PictureInPictureParams.Builder()
//                            .setAspectRatio(Rational.ZERO)
//                            .setAutoEnterEnabled(false)
//                            .build());
//                }
//            }
//            this.enterPictureInPictureMode();
//
//        }
//    }


    private void setupStreamingModePipeline(InputSource inputSource) {
        this.inputSource = inputSource;
        // Initializes a new MediaPipe Hands solution instance in the streaming mode.
        hands =
                new Hands(
                        this,
                        HandsOptions.builder()
                                .setStaticImageMode(false)
                                .setMaxNumHands(2)
                                .setRunOnGpu(RUN_ON_GPU)
                                .build());
        hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        }
        glSurfaceView =
                new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
        glSurfaceView.setRenderInputImage(true);
        hands.setResultListener(
                handsResult -> {
                    logWristLandmark(handsResult, /*showPixelValues=*/ false);
                    glSurfaceView.setRenderData(handsResult);
                    glSurfaceView.requestRender();
                });

        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.post(this::startCamera);
        }

        FrameLayout frameLayout = findViewById(R.id.preview_display_layout);
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);
        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }

    private void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight());
        Log.d("camere", "startCamera: "+ glSurfaceView.getWidth());
        Log.d("camere", "startCamera: "+ glSurfaceView.getHeight());

    }

    private void stopCurrentPipeline() {
        if (cameraInput != null) {

        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {

            hands.close();
        }
    }

    private void logWristLandmark(HandsResult result, boolean showPixelValues) {
        if (result.multiHandLandmarks().isEmpty()) {
            return;
        }
        LandmarkProto.Landmark f =
                result.multiHandWorldLandmarks().get(0).getLandmarkList()
                        .get(HandLandmark.INDEX_FINGER_TIP);
        Log.d("fore", "forefinger: "+f.getX());
        LandmarkProto.Landmark m1 =
                result.multiHandWorldLandmarks().get(0).getLandmarkList()
                        .get(HandLandmark.MIDDLE_FINGER_TIP);
        double d = Math.sqrt(Math.pow(f.getX()- m1.getX(),2)
                +Math.pow(f.getY()- m1.getY(),2)
                +Math.pow(f.getZ()- m1.getZ(),2));
        Log.d("fore", "middlefinger: "+m1.getX());

        Log.d("dis", "distance: "+ d);
        if (d<0.014657539422214935){

        }

//        LandmarkProto.NormalizedLandmark wristLandmark =
//                result.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
//        // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
//        if (showPixelValues) {
//            int width = result.inputBitmap().getWidth();
//            int height = result.inputBitmap().getHeight();
//            Log.i(
//                    TAG,
//                    String.format(
//                            "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
//                            wristLandmark.getX() * width, wristLandmark.getY() * height));
//        } else {
//            Log.i(
//                    TAG,
//                    String.format(
//                            "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
//                            wristLandmark.getX(), wristLandmark.getY()));
//        }
//        if (result.multiHandWorldLandmarks().isEmpty()) {
//            return;
//        }
//        LandmarkProto.Landmark wristWorldLandmark =
//                result.multiHandWorldLandmarks().get(0).getLandmarkList()
//                        .get(HandLandmark.WRIST);
//        Log.i(
//                TAG,
//                String.format(
//                        "MediaPipe Hand wrist world coordinates (in meters with the origin at the hand's"
//                                + " approximate geometric center): x=%f m, y=%f m, z=%f m",
//                        wristWorldLandmark.getX(), wristWorldLandmark.getY(),
//                        wristWorldLandmark.getZ()));
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy: ");


    }

//    public void checkOverlayPermission(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 0);
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            Toast.makeText(this, "Granted ", Toast.LENGTH_SHORT).show();
        }
    }


}