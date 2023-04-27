package com.example.triangulosimple;

// Importamos la bibliotecas necesarias.

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class Triangulo {

    private FloatBuffer vertexBuffer;
    private final int mProgram;

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // Definimos las coordenadas de los vertices
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {

            0.0f, 0.0f, 0.0f, // Arriba

            -1.0f, -1.0f, 0.0f, // Izquierda

            1.0f, -1.0f, 0.0f // Derecha

    };

    // Definimos el color con rojo, verde, azul and alpha (transparencia)
    // float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    float color[] = { 0.5f, 1.0f, 0.5f, 1.0f };

    public Triangulo() {
        // Inicializa el vertex byte buffer para las coordenadas de la figura.
        ByteBuffer bb = ByteBuffer.allocateDirect(

                // (cantidad de  valores de las coordenadas * 4 bytes por flotante).
                triangleCoords.length * 4);

        // Usar el orden de bytes nativo del hardware del dispositivo.
        bb.order(ByteOrder.nativeOrder());

        // Crear un búfer de punto flotante desde el ByteBuffer.
        vertexBuffer = bb.asFloatBuffer();

        // Agrega las coordenadas al FloatBuffer.
        vertexBuffer.put(triangleCoords);

        // Configurar el búfer para leer la primera coordenada.
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Crea un programa OpenGL ES vacío.
        mProgram = GLES20.glCreateProgram();

        // Agrega el vertex shader al programa.
        GLES20.glAttachShader(mProgram, vertexShader);

        // Agrega el fragment shader al programa.
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Crea un programa OpenGL ES ejecutable.
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        // Agrega el programa al ambiente de OpenGL ES.
        GLES20.glUseProgram(mProgram);

        // Obtiene el identificador vPosition desde vertex shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Habilita un controlador para los vértices del triángulo.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Preparar los datos de coordenadas del triángulo
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Obtiene el identificador vColor desde fragment shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Asigna el color para dibujar el triangulo.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Dibuja el triangulo.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Desactiva el arreglo de vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
