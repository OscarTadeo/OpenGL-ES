package com.example.opengles3dcubo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cubo {

    private FloatBuffer vertexBuffer; // Buffer para el arreglo de vertices.
    private ShortBuffer indexBuffer; // Buffer para el arreglo de indices.

    private final int mProgram;

    private int colorHandle;
    private int positionHandle;

    // Definimos el número de coordenadas para vertices.
    static final int COORDS_PER_VERTEX =3;

    // 4 bytes por vertice.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Numero de caras a dibujar.
    private int numCaras = 6;

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Vertices del cubo
    static float vertices[]=
            {
                    // Cara de frente
                    -1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f,
                    1.0f,  1.0f,  1.0f,

                    // Cara trasera
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f,  1.0f, -1.0f,
                    -1.0f,  1.0f, -1.0f,

                    // Cara izquierda
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f,  1.0f,
                    -1.0f,  1.0f, -1.0f,
                    -1.0f,  1.0f,  1.0f,

                    // Cara derecha
                    1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f,  1.0f,  1.0f,
                    1.0f,  1.0f, -1.0f,

                    // Cara superior.
                    -1.0f,  1.0f,  1.0f,
                    1.0f,  1.0f,  1.0f,
                    -1.0f,  1.0f, -1.0f,
                    1.0f,  1.0f, -1.0f,

                    // Cara inferior.
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f,  1.0f
            };

    // Colores para los vertices
    private float[][] colores = {
            {1.0f, 0.0f, 1.0f, 1.0f},  // 0. Rosa.
            {1.0f, 1.0f, 0.0f, 1.0f},  // 1. Amarillo.
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. Verde.
            {0.0f, 0.0f, 1.0f, 1.0f},  // 3. Azul.
            {1.0f, 0.0f, 0.0f, 1.0f},  // 4. Rojo.
            {1.0f, 0.5f, 0.0f, 1.0f}   // 5. Naranja.
    };

    // Orden en que se dibujan los triangulos
    short[] indices={
            0, 1, 2, 2, 1, 3,
            5, 4, 7, 7, 4, 6,
            8, 9, 10, 10, 9, 11,
            12, 13, 14, 14, 13, 15,
            16, 17, 18, 18, 17, 19,
            22, 23, 20, 20, 23, 21
    };

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Cubo()
    {

        // Vertex Buffer.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Usar orden de bytes nativo.
        vertexBuffer = vbb.asFloatBuffer(); // Convierte de 'byte' a 'float'.
        vertexBuffer.put(vertices);         // Se copian los datos en el buffer.
        vertexBuffer.position(0); // Reinicia.

        // Index buffer.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Crea un programa OpenGL ES vacío.
        mProgram = GLES20.glCreateProgram();

        // Se cargan los shaders.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

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

        // Obtiene el identificador vColor desde fragment shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Ciclo para recorrer cada una de las caras y agregarles color.
        for (int cara = 0; cara < numCaras; cara++) {

            // Se asigna el color a una de las caras.
            GLES20.glUniform4fv(colorHandle, 1, colores[cara], 0);
            indexBuffer.position(cara * 6);

            // Dibuja cada cara.
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        }

        // Desactiva los identificadores.
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
