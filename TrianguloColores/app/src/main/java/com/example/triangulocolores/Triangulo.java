package com.example.triangulocolores;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangulo {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private final int mProgram;

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes por vertice.

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Definimos el número de coordenadas para vertices.
    static final int COORDS_PER_VERTEX = 3;

    // Coordenadas de la figura.
    static float triangleCoords[] = {
            0.0f,  0.622008459f, 0.0f, // Arriba
            -0.5f, -0.311004243f, 0.0f, // Izquierda
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // Colores para los vertices.
    private float colors[] = {
            0.0f, 1.0f, 0.0f, 1.0f, // Rojo
            0.0f, 0.0f, 1.0f, 1.0f, // Verde
            1.0f, 0.0f, 0.0f, 1.0f  // Azul
    };

    // Definimos color.
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    // Declaración de los shaders.
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_color;" + // (Nuevo)
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  vColor = a_color;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Triangulo() {

        // Inicializa el vertex byte buffer para las coordenadas de la figura.
        ByteBuffer vbb = ByteBuffer.allocateDirect(

                // (cantidad de  valores de las coordenadas * 4 bytes por flotante).
                triangleCoords.length * 4);

        // Usa el orden de bytes nativo del hardware del dispositivo.
        vbb.order(ByteOrder.nativeOrder());

        // Crea un buffer de punto flotante desde el ByteBuffer.
        vertexBuffer = vbb.asFloatBuffer();

        // Agrega las coordenadas al FloatBuffer.
        vertexBuffer.put(triangleCoords);

        // Configurar el buffer para leer la primera coordenada.
        vertexBuffer.position(0);

        // Buffer para los colores
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        // Se cargan los shaders.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // Crea un programa OpenGL ES vacío.
        mProgram = GLES20.glCreateProgram();

        // Agrega el vertex shader al programa.
        GLES20.glAttachShader(mProgram, vertexShader);

        // Agrega el fragment shader al programa.
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Crea un programa OpenGL ES ejecutable.
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {

        // Agrega el programa al ambiente de OpenGL ES.
        GLES20.glUseProgram(mProgram);

        // Obtiene el identificador vPosition desde vertex shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Habilita un controlador para los vértices de la figura.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepara los datos de coordenadas de la figura.
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Obtiene el identificador a_color desde fragment shader.
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        // Dibuja la figura.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Desactiva el identificador de vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
    }
}