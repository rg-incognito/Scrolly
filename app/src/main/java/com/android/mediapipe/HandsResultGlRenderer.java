package com.android.mediapipe;

import android.opengl.GLES20;
import android.os.IBinder;
import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutioncore.ResultGlRenderer;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsResult;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/** A custom implementation of {@link ResultGlRenderer} to render {@link HandsResult}. */
public class HandsResultGlRenderer implements ResultGlRenderer<HandsResult> {
    private static final String TAG = "HandsResultGlRenderer";
    GlobalActionBarService globalActionBarService = GlobalActionBarService.getSharedInstance();

    private static final float[] LEFT_HAND_CONNECTION_COLOR = new float[] {0.2f, 1f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_CONNECTION_COLOR = new float[] {2f, 0.1f, 0.21f, 1f};
    private static final float CONNECTION_THICKNESS = 20.0f;
    private static final float[] LEFT_HAND_HOLLOW_CIRCLE_COLOR = new float[] {0.2f, 1f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_HOLLOW_CIRCLE_COLOR = new float[] {1f, 0.2f, 0.2f, 1f};
    private static final float HOLLOW_CIRCLE_RADIUS = 0.01f;
    private static final float[] LEFT_HAND_LANDMARK_COLOR = new float[] {1f, 0.2f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_LANDMARK_COLOR = new float[] {0.2f, 1f, 0.2f, 1f};
    private static final float LANDMARK_RADIUS = 0.008f;
    private static final int NUM_SEGMENTS = 120;
    private static final String VERTEX_SHADER =
            "uniform mat4 uProjectionMatrix;\n"
                    + "attribute vec4 vPosition;\n"
                    + "void main() {\n"
                    + "  gl_Position = uProjectionMatrix * vPosition;\n"
                    + "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n"
                    + "uniform vec4 uColor;\n"
                    + "void main() {\n"
                    + "  gl_FragColor = uColor;\n"
                    + "}";
    private int program;
    private int positionHandle;
    private int projectionMatrixHandle;
    private int colorHandle;

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void setupRendering() {
        program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        colorHandle = GLES20.glGetUniformLocation(program, "uColor");
    }

    @Override
    public void renderResult(HandsResult result, float[] projectionMatrix) {
        if (result == null) {
            return;
        }
        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glLineWidth(CONNECTION_THICKNESS);

        int numHands = result.multiHandLandmarks().size();
        for (int i = 0; i < numHands; ++i) {
            boolean isLeftHand = result.multiHandedness().get(i).getLabel().equals("Left");



            // Drawing single finger forefinger
            NormalizedLandmark landmark =  result.multiHandLandmarks().get(i).getLandmarkList().get(8);
            drawCircle(
                        landmark.getX(),
                        landmark.getY(),
                        isLeftHand ? LEFT_HAND_LANDMARK_COLOR : RIGHT_HAND_LANDMARK_COLOR);

            drawHollowCircle(
                        landmark.getX(),
                        landmark.getY(),
                        isLeftHand ? LEFT_HAND_HOLLOW_CIRCLE_COLOR : RIGHT_HAND_HOLLOW_CIRCLE_COLOR);

            // for middle finger
            NormalizedLandmark landmark1 =  result.multiHandLandmarks().get(i).getLandmarkList().get(12);
            drawCircle(
                    landmark1.getX(),
                    landmark1.getY(),
                    isLeftHand ? LEFT_HAND_LANDMARK_COLOR : RIGHT_HAND_LANDMARK_COLOR);

            drawHollowCircle(
                    landmark1.getX(),
                    landmark1.getY(),
                    isLeftHand ? LEFT_HAND_HOLLOW_CIRCLE_COLOR : RIGHT_HAND_HOLLOW_CIRCLE_COLOR);
//            double d = Math.sqrt(Math.pow(
//                    Double.parseDouble(String.valueOf(landmark.getX()))
//                    - Double.parseDouble(String.valueOf(landmark1.getX())), 2)
//                    + Math.pow(Double.parseDouble(String.valueOf(landmark.getY()))
//                    - Double.parseDouble(String.valueOf(landmark1.getY())),
//                    2));

            double d = Math.sqrt(Math.pow(landmark.getX()- landmark1.getX(),2)
                    +Math.pow(landmark.getY()- landmark1.getY(),2)
                    +Math.pow(landmark.getZ()- landmark1.getZ(),2));

            Log.d(TAG, "renderResult dddd : "+d +"  "+ globalActionBarService.toString());
            globalActionBarService.test();
            if (globalActionBarService != null){
                Log.d("CheckScroll", "renderResult: "+ (d < 0.050906282163080734));

                if(d < 0.050906282163080734 ){
                    Log.d("CheckScroll", "renderResultkkkkkkkkk: ");
                    globalActionBarService.configureScrollButton();
                }
            }
            else{
                Log.d("CheckScroll", "renderResult:Error ");
            }
            Log.d(TAG, "L1 X-"+landmark.getX()+ "L1 Y"+landmark.getY());
            Log.d(TAG, "L2 X-"+landmark1.getX()+ "L2 Y"+landmark1.getY());
            // Drawing whole hand
//            drawConnections(
//                    result.multiHandLandmarks().get(i).getLandmarkList(),
//                    isLeftHand ? LEFT_HAND_CONNECTION_COLOR : RIGHT_HAND_CONNECTION_COLOR);

//            for (NormalizedLandmark landmark : result.multiHandLandmarks().get(i).getLandmarkList()) {
//
//                // Draws the landmark.
//                drawCircle(
//                        landmark.getX(),
//                        landmark.getY(),
//                        isLeftHand ? LEFT_HAND_LANDMARK_COLOR : RIGHT_HAND_LANDMARK_COLOR);
//                // Draws a hollow circle around the landmark.
//                drawHollowCircle(
//                        landmark.getX(),
//                        landmark.getY(),
//                        isLeftHand ? LEFT_HAND_HOLLOW_CIRCLE_COLOR : RIGHT_HAND_HOLLOW_CIRCLE_COLOR);
//            }
        }
    }

    /**
     * Deletes the shader program.
     *
     * <p>This is only necessary if one wants to release the program while keeping the context around.
     */
    public void release() {
        GLES20.glDeleteProgram(program);
    }

    private void drawConnections(List<NormalizedLandmark> handLandmarkList, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        for (Hands.Connection c : Hands.HAND_CONNECTIONS) {
            NormalizedLandmark start = handLandmarkList.get(c.start());
            NormalizedLandmark end = handLandmarkList.get(c.end());
            float[] vertex = {start.getX(), start.getY(), end.getX(), end.getY()};
            FloatBuffer vertexBuffer =
                    ByteBuffer.allocateDirect(vertex.length * 4)
                            .order(ByteOrder.nativeOrder())
                            .asFloatBuffer()
                            .put(vertex);
            vertexBuffer.position(0);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        }
    }

    private void drawCircle(float x, float y, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        int vertexCount = NUM_SEGMENTS + 2;
        float[] vertices = new float[vertexCount * 3];
        vertices[0] = x;
        vertices[1] = y;
        vertices[2] = 0;
        for (int i = 1; i < vertexCount; i++) {
            float angle = 2.0f * i * (float) Math.PI / NUM_SEGMENTS;
            int currentIndex = 3 * i;
            vertices[currentIndex] = x + (float) (LANDMARK_RADIUS * Math.cos(angle));
            vertices[currentIndex + 1] = y + (float) (LANDMARK_RADIUS * Math.sin(angle));
            vertices[currentIndex + 2] = 0;
        }
        FloatBuffer vertexBuffer =
                ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .put(vertices);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
    }

    private void drawHollowCircle(float x, float y, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        int vertexCount = NUM_SEGMENTS + 1;
        float[] vertices = new float[vertexCount * 3];
        for (int i = 0; i < vertexCount; i++) {
            float angle = 2.0f * i * (float) Math.PI / NUM_SEGMENTS;
            int currentIndex = 3 * i;
            vertices[currentIndex] = x + (float) (HOLLOW_CIRCLE_RADIUS * Math.cos(angle));
            vertices[currentIndex + 1] = y + (float) (HOLLOW_CIRCLE_RADIUS * Math.sin(angle));
            vertices[currentIndex + 2] = 0;
        }
        FloatBuffer vertexBuffer =
                ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .put(vertices);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);
    }
}