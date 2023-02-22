package com.example.triangulodearchivo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    // Obtenemos el contexto para poder extraer los shaderCode
    private Context context;
    // Creamos el objeto Triangulo.
    private Triangulo mTriangulo;

    public MyGLRenderer(Context c) {
        context = c;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Definimos el color del fondo asignando un numero flotante en cada atributo.
        // void glClearColor(GLfloat rojo, GLfloat verde, GLfloat azul, GLfloat alpha);

        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

        // Inicializamos el objeto Triangulo.

            mTriangulo = new Triangulo(context);
    }

    public void onDrawFrame(GL10 unused) {

        // Dibujamos el color del fondo
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Dibujamos el triangulo
        mTriangulo.draw();

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {

        // Definimos el tamaño de la venta para nuestra vista.
        GLES20.glViewport(0, 0, width, height);

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}