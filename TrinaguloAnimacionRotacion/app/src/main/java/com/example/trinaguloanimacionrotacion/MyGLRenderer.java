package com.example.trinaguloanimacionrotacion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangulo mTriangulo;
    private Cuadrado mCuadrado;

    // Obtenemos el contexto para poder extraer los shaderCode
    private Context context;

    // vPMatrix es la abreviación para "Matriz Modelo Vista Proyección."
    private final float[] vPMatrix = new float[16];

    // Matriz de la figura.
    private final float[] projectionMatrix = new float[16];

    // Matriz de la vista
    private final float[] viewMatrix = new float[16];

    // Matriz de rotacion Triangulo.
    private float[] rotationMatrix = new float[16];

    // Matriz de rotacion Cuadrado.
    private float[] rotationMatrixC = new float[16];

    // Matriz de traslacion Triangulo.
    private float[] translateMatrix = new float[16];

    // Matriz de traslacion Cuadrado.
    private float[] translateMatrixC = new float[16];

    // Matriz auxiliar Triangulo.
    private float[] tempMatrix = new float[16];

    // Matriz de traslacion Cuadrado.
    private float[] tempMatrixC = new float[16];

    public MyGLRenderer(Context c) {
        context = c;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Asignamos un color al fondo
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Inicializamos un triangulo.
        mTriangulo = new Triangulo(context);

        // Inicializamos un cuadrado.
        mCuadrado = new Cuadrado();
    }

    public void onDrawFrame(GL10 unused) {

        float[] producto = new float[16];
        float[] ultima = new float[16];
        float[] productoC = new float[16];
        float[] ultimaC = new float[16];
        float[] scratch = new float[16];


        // Aplica el color al fondo
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Asigna la posición de la camara (Matriz de la vista).
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -4, 0f, 0f, 0f, 0f, 1f, 0.0f);

        // Calcula la proyección y la transformación de la vista.
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        /*// Dibujamos un trinagulo sin animacion.
        mTriangulo.draw(vPMatrix);*/

        // ----- Render Triangulo -----
        // Traslacion
        Matrix.setIdentityM(translateMatrix,0);
        Matrix.translateM(translateMatrix, 0,1.5f,0f,0f);

        /*// Matriz que traslada el triangulo sin animación
        Matrix.multiplyMM(scratch, 0, vPMatrix,0, translateMatrix, 0);*/

        // Crear una transformación de rotación para la figura.
        long time = SystemClock.uptimeMillis() % 4000L;

        // Velocidad de rotacion
        float angle = 0.090f * ((int) time);

        // Eje y dirección de rotacion.
        Matrix.setRotateM(rotationMatrix, 0, angle, 1, 0, 0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        /*Matrix.multiplyMM(scratch, 0, vPMatrix, 0,rotationMatrix , 0);*/

        tempMatrix = translateMatrix.clone();

        // Multiplicamos la matriz de 'traslacion' por
        // matriz 'rotacion' para obtener la matriz 'producto'.
        Matrix.multiplyMM(producto, 0, tempMatrix, 0, rotationMatrix,0);

        // Multiplicamos la matriz 'vPMatrix' por la matriz 'producto'
        Matrix.multiplyMM(ultima, 0,vPMatrix, 0,producto,0);

        // Dibujar Triangulo
        mTriangulo.draw(ultima);

        // ----- Render Cuadrado -----

        // Traslacion
        Matrix.setIdentityM(translateMatrixC,0);
        Matrix.translateM(translateMatrixC, 0,-1.5f,0f,0f);

        /*// Matriz que traslada el triangulo sin animación
        Matrix.multiplyMM(scratch, 0, vPMatrix,0, translateMatrixC, 0);*/

        // Dirección de rotacion
        Matrix.setRotateM(rotationMatrixC, 0, angle, 1, 1, 1);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        /*Matrix.multiplyMM(scratch, 0, vPMatrix, 0,rotationMatrix , 0);*/

        tempMatrixC = translateMatrixC.clone();

        // Multiplicamos la matriz de 'traslacion' por
        // matriz 'rotacion' para obtener la matriz 'producto'.
        Matrix.multiplyMM(productoC, 0, tempMatrixC, 0, rotationMatrixC,0);

        // Multiplicamos la matriz 'vPMatrix' por la matriz 'producto'
        Matrix.multiplyMM(ultimaC, 0,vPMatrix, 0,productoC,0);
        mCuadrado.draw(ultimaC);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // Esta matriz de proyección se aplica a las coordenadas
        // del objeto en el método onDrawFrame().
        // Parametros de las dimenciones de la vista.
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 14);
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