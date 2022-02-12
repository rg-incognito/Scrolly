package com.android.mediapipe;

import android.app.PictureInPictureParams;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
        setupLiveDemoUiComponents();
//        checkOverlayPermission();
//        startService();


    }
//    private void touchEventt(){
//
//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 8000;
//        int x= this.getResources().getDisplayMetrics().widthPixels;
//        int y= this.getResources().getDisplayMetrics().heightPixels;
//
//// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
//        int metaState = 0;
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime,
//                MotionEvent.ACTION_SCROLL,
//                x,
//                y,
//                metaState
//        );
//
//
//// Dispatch touch event to view
//        MainActivity.this.dispatchTouchEvent(motionEvent);
//        Log.d("TAG", "touchEventt: sssssssssssssssssssssssss"+ MainActivity.this.dispatchTouchEvent(motionEvent));
//
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame ->
                    hands.send(textureFrame));
            glSurfaceView.post(this::startCamera);
            glSurfaceView.setVisibility(View.VISIBLE);
//            touchEventt();
            enterPIP();
//            startService();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        enterPIP();
//        touchEventt();

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
        Button startCameraButton = findViewById(R.id.button_start_camera);
        startCameraButton.setOnClickListener(
                v -> {
                    if (inputSource == InputSource.CAMERA) {
                        return;
                    }
                    stopCurrentPipeline();
                    setupStreamingModePipeline(InputSource.CAMERA);
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        enterPIP();
    }
    private void enterPIP(){
        Log.d(TAG, "enterPIP: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "enterPIP: nnnnnn");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "enterPIP: ooooooooooo");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.d(TAG, "enterPIP: ssssss");
                    setPictureInPictureParams(new PictureInPictureParams.Builder()
                            .setAspectRatio(Rational.NaN)
                            .setAutoEnterEnabled(false)
                            .build());
                }
            }
            this.enterPictureInPictureMode();
        }
    }


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
//                    logWristLandmark(handsResult, /*showPixelValues=*/ false);
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

    }

    private void stopCurrentPipeline() {
        if (cameraInput != null) {
            enterPIP();
        }
        if (glSurfaceView != null) {
            enterPIP();
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {

            hands.close();
        }
    }

//    private void logWristLandmark(HandsResult result, boolean showPixelValues) {
//        if (result.multiHandLandmarks().isEmpty()) {
//            return;
//        }
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
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
//    public void startService(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // check if the user has already granted
//            // the Draw over other apps permission
//            if(Settings.canDrawOverlays(this)) {
//                Log.d("TAG", "startService: ");
//                // start the service based on the android version
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(new Intent(this, ForegroundService.class));
//                } else {
//                    startService(new Intent(this, ForegroundService.class));
//                }
//            }
//        }else{
//            Log.d("TAG", "startService: ");
//
//            startService(new Intent(this, ForegroundService.class));
//        }
//    }
//
//    // method to ask user to grant the Overlay permission
//    public void checkOverlayPermission(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                // send user to the device settings
//                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                startActivity(myIntent);
//            }
//        }
//    }

}