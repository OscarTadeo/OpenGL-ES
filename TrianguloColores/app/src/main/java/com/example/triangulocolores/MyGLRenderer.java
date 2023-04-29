package com.example.triangulocolores;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangulo mTriangulo;

    // vPMatrix es la abreviación para "Matriz Modelo Vista Proyección."
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] rotationMatrix = new float[16];

    public volatile float mAngle;

    // Variable para obtener el cambio de la transfromación
    // desde la pantalla tactil.
    public float getAngle() {
        return mAngle;
    }

    // Variable para asignar el cambio a la transformación.
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Asignamos un color al fondo
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Inicializamos un triangulo.
        mTriangulo = new Triangulo();
    }

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Aplica el color al fondo
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Asigna la posición de la camara (Matriz de la vista).
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calcula la proyección y la transformación de la vista.
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Create a rotation for the triangle
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        // Eje y dirección de rotacion.
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0.0f, -1.0f, 0.0f);

        // Combina la matriz de rotación con la proyección y la vista de la cámara.
        // Tenga en cuenta que el factor vPMatrix *debe ser el primero* para que el
        // producto de multiplicación de matrices sea correcto.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        // Dibuja la figura.
        mTriangulo.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // Esta matriz de proyección se aplica a las coordenadas
        // del objeto en el método onDrawFrame().
        // Parametros de las dimenciones de la vista.
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // Crea vertex shader de tipo (GLES20.GL_VERTEX_SHADER)
        // o fragment shader de tipo (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // Agrega el código fuente al shader y lo compíla.
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}