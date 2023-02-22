package com.example.opengles3dtextura;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Cubo mCubo;
    private Cubo3 mCubo3;
    private Cubo2 mCubo2;
    private Cubo4 mCubo4;

    // Obtenemos el contexto para poder extraer los shaderCode
    private Context context;

    // vPMatrix es la abreviatura de "Matriz de proyección de vista de modelo"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    //Matriz de rotacion.
    private float[] matrizRotacionCubo = new float[16];
    private float[] matrizRotacionCubo2 = new float[16];
    private float[] matrizRotacionCubo3 = new float[16];
    private float[] matrizRotacionCubo4 = new float[16];


    // Matriz de escala.
    private float[] matrizEscalaCubo = new float[16];
    private float[] matrizEscalaCubo2 = new float[16];
    private float[] matrizEscalaCubo3 = new float[16];
    private float[] matrizEscalaCubo4 = new float[16];

    // Matriz de traslación.
    private float[] matrizTraslacionCubo = new float[16];
    private float[] matrizTraslacionCubo2 = new float[16];
    private float[] matrizTraslacionCubo3 = new float[16];
    private float[] matrizTraslacionCubo4 = new float[16];

    // Matriz auxiliar para almacenar el producto de matrices.
    private float[] matrizTemp = new float[16];
    private float[] matrizTempC2 = new float[16];
    private float[] matrizTempC3 = new float[16];
    private float[] matrizTempC4 = new float[16];

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
        GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);



        // Nos permite saber que esta mas al fondo y no poder ver atravez de otras figuras.
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );

        GLES20.glDisable(GLES20.GL_DEPTH_BITS);

        GLES20.glClearDepthf(1.0f);

        mCubo = new Cubo(context);
        mCubo2 = new Cubo2(context);
        mCubo3 = new Cubo3(context);
        mCubo4 = new Cubo4(context);

    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Dibujamos el fondo.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);



        /*------------------ Cámara -----------------*/
        // Asignamos la posición de la camara.
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 9, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

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
        Matrix.translateM(matrizTraslacionCubo, 0,-0.7f,1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCubo,0);
        Matrix.scaleM(matrizEscalaCubo,0, .5f,.5f,.5f);
        Matrix.multiplyMM(mExTCubo,0, matrizTraslacionCubo, 0, matrizEscalaCubo,0);

        // Rotacion.
        matrizTemp = mExTCubo.clone();

        Matrix.setRotateM(matrizRotacionCubo, 0, mAngle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRCubo, 0 , matrizTemp, 0, matrizRotacionCubo,0);
        Matrix.multiplyMM(matrizFinalCubo, 0, vPMatrix,0, mExTxRCubo, 0);




        /*------------------ Cubo2 -----------------*/

        float[] mExTC2 = new float[16];
        float[] mExTxRC2 = new float[16];
        float[] matrizFinalC2 = new float[16];

        // Traslación.
        Matrix.setIdentityM(matrizTraslacionCubo2,0);
        Matrix.translateM(matrizTraslacionCubo2, 0,-0.7f,-1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCubo2,0);
        Matrix.scaleM(matrizEscalaCubo2,0, .5f,.5f,.5f);
        Matrix.multiplyMM(mExTC2,0, matrizTraslacionCubo2, 0, matrizEscalaCubo2,0);

        // Rotacion.
        matrizTempC2 = mExTC2.clone();

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(matrizRotacionCubo2, 0, angle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRC2, 0 , matrizTempC2, 0, matrizRotacionCubo2,0);
        Matrix.multiplyMM(matrizFinalC2, 0, vPMatrix,0, mExTxRC2, 0);

        /*------------------ Cubo3 -----------------*/

        float[] mExTC3 = new float[16];
        float[] mExTxRC3 = new float[16];
        float[] matrizFinalC3 = new float[16];

        // Traslación.
        Matrix.setIdentityM(matrizTraslacionCubo3,0);
        Matrix.translateM(matrizTraslacionCubo3, 0,0.7f,1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCubo3,0);
        Matrix.scaleM(matrizEscalaCubo3,0, .5f,.5f,.5f);
        Matrix.multiplyMM(mExTC3,0, matrizTraslacionCubo3, 0, matrizEscalaCubo3,0);

        // Rotacion.
        matrizTempC3 = mExTC3.clone();
        Matrix.setRotateM(matrizRotacionCubo3, 0, mAngle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRC3, 0 , matrizTempC3, 0, matrizRotacionCubo3,0);
        Matrix.multiplyMM(matrizFinalC3, 0, vPMatrix,0, mExTxRC3, 0);



        /*------------------ Cubo4 -----------------*/

        float[] mExTC4 = new float[16];
        float[] mExTxRC4 = new float[16];
        float[] matrizFinalC4 = new float[16];

        // Traslación.
        Matrix.setIdentityM(matrizTraslacionCubo4,0);
        Matrix.translateM(matrizTraslacionCubo4, 0,0.7f,-1f,0f);

        // Escala.
        Matrix.setIdentityM(matrizEscalaCubo4,0);
        Matrix.scaleM(matrizEscalaCubo4,0, .25f,.25f,.25f);
        Matrix.multiplyMM(mExTC4,0, matrizTraslacionCubo4, 0, matrizEscalaCubo4,0);

        // Rotacion.
        matrizTempC4 = mExTC4.clone();
        Matrix.setRotateM(matrizRotacionCubo4, 0, mAngle, 1.0f, 1.0f, -1.0f);

        Matrix.multiplyMM(mExTxRC4, 0 , matrizTempC4, 0, matrizRotacionCubo4,0);
        Matrix.multiplyMM(matrizFinalC4, 0, vPMatrix,0, mExTxRC4, 0);

        // Dibujamos las figuras.

        mCubo.draw(matrizFinalCubo);
        mCubo3.draw(matrizFinalC3);
        mCubo2.draw(matrizFinalC2);
        mCubo4.draw(matrizFinalC4);


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

