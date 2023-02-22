package com.example.opengles3dcubo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    /*private Piramide mPiramide;*/
    private Cubo mCubo;

    private CuboColores mCuboColores;

    // Obtenemos el contexto para poder extraer los shaderCode
    private Context context;

    // vPMatrix es la abreviatura de "Matriz de proyección de vista de modelo"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    //Matriz de rotacion.
    private float[] matrizRotacionCubo = new float[16];
    private float[] matrizRotacionCuboC = new float[16];


    // Matriz de escala.
    private float[] matrizEscalaCubo = new float[16];
    private float[] matrizEscalaCuboC = new float[16];

    // Matriz de traslación.
    private float[] matrizTraslacionCubo = new float[16];
    private float[] matrizTraslacionCuboC = new float[16];

    // Matriz auxiliar para almacenar el producto de matrices.
    private float[] matrizTemp = new float[16];
    private float[] matrizTempC = new float[16];

    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public MyGLRenderer(Context c) {
        context = c;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Asignamos el color del fondo
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Nos permite saber que esta mas al fondo y no poder ver atravez de otras figuras.
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );

        GLES20.glClearDepthf(1.0f);


        mCubo = new Cubo();

        mCuboColores =  new CuboColores(context);

    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Dibujamos el fondo.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        /*------------------ Cámara -----------------*/
        // Asignamos la posición de la camara.
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -8, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Se calcula la transformación de proyección y vista
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Create a rotation for the Piramide
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        /*------------------ Cubo -----------------*/

        // Matrices para el Cubo.
        float[] mExTCubo = new float[16];
        float[] mExTxRCubo = new float[16];
        float[] matrizFinalCubo = new float[16];

        // Traslación.
        Matrix.setIdentityM(matrizTraslacionCubo,0);
        Matrix.translateM(matrizTraslacionCubo, 0,0f,1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCubo,0);
        Matrix.scaleM(matrizEscalaCubo,0, .5f,.5f,.5f);
        Matrix.multiplyMM(mExTCubo,0, matrizTraslacionCubo, 0, matrizEscalaCubo,0);

        // Rotacion.
        matrizTemp = mExTCubo.clone();

        Matrix.setRotateM(matrizRotacionCubo, 0, mAngle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRCubo, 0 , matrizTemp, 0, matrizRotacionCubo,0);
        Matrix.multiplyMM(matrizFinalCubo, 0, vPMatrix,0, mExTxRCubo, 0);

        /*------------------ Cubo Colorido -----------------*/

        float[] mExTCC = new float[16];
        float[] mExTxRCC = new float[16];
        float[] matrizFinalCC = new float[16];

        // Traslación.
        Matrix.setIdentityM(matrizTraslacionCuboC,0);
        Matrix.translateM(matrizTraslacionCuboC, 0,0f,-1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCuboC,0);
        Matrix.scaleM(matrizEscalaCuboC,0, .5f,.5f,.5f);
        Matrix.multiplyMM(mExTCC,0, matrizTraslacionCuboC, 0, matrizEscalaCuboC,0);

        // Rotacion.
        matrizTempC = mExTCC.clone();

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(matrizRotacionCuboC, 0, angle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRCC, 0 , matrizTempC, 0, matrizRotacionCuboC,0);
        Matrix.multiplyMM(matrizFinalCC, 0, vPMatrix,0, mExTxRCC, 0);

        // Dibujamos las figuras.

        mCubo.draw(matrizFinalCubo);
        mCuboColores.draw(matrizFinalCC);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 14);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // Se agrega el código fuente al shader y compilamos
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


}

