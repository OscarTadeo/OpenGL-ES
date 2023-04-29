package com.example.trinaguloanimacionrotacion;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cuadrado {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private final int mProgram;

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertice.

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // Coordenadas de la figura.
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right

    // Orden de para dibujar los vertices.
    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

    private final String vertexShaderCode =
            // Esta variable matriz proporciona el enlace para manipular las coordenadas.
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // La matriz debe ser incluida como modificador de gl_Position.

                    // Tenga en cuenta que el factor uMVPMatrix va al principio
                    // para que el producto de multiplicación de matrices sea correcto.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // Asignamos color.
    float color[] = { 1.0f, 0.7f, 0.3f, 1.0f };

    public Cuadrado() {
        // Inicializa el byte buffer para las coordenadas de los vertices de la figura.
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (Número de coordenadas * 4 bytes por float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        // Inicializa el byte buffer para rl arreglo de orden de dibujado.
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (Número de coordenadas * 2 bytes por short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // Crea un programa de OpenGL ES vacio.
        mProgram = GLES20.glCreateProgram();

        // Agrega el vertex shader al programa.
        GLES20.glAttachShader(mProgram, vertexShader);

        // Agrega el fragment shader al programa.
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Crea el programa OpenGL ES ejecutable.
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

        // Obtiene el identificador vColor desde fragment shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Asigna el color para dibujar la figura.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Dibuja la figura.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Desactiva el arreglo de vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
    }
}