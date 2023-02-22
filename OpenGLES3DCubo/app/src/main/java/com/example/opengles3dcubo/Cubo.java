package com.example.opengles3dcubo;

import android.content.Context;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cubo {

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int colorHandle;
    private int positionHandle;

    // Número de coordenadas por vertice en vertices[].
    static final int COORDS_PER_VERTEX =3;

    // Tamaño de vértice en bytes.
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Numero de caras a dibujar.
    private int numCaras = 6;

    private int vPMatrixHandle;

    // Vertices del cubo
    static float vertices[]=
            {
                    // Cara de frente
                    -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
                    1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
                    -1.0f,  1.0f,  1.0f,  // 2. left-top-front
                    1.0f,  1.0f,  1.0f,  // 3. right-top-front

                    // Cara trasera
                    1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
                    -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
                    1.0f,  1.0f, -1.0f,  // 7. right-top-back
                    -1.0f,  1.0f, -1.0f,  // 5. left-top-back

                    // Cara izquierda
                    -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
                    -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
                    -1.0f,  1.0f, -1.0f,  // 5. left-top-back
                    -1.0f,  1.0f,  1.0f,  // 2. left-top-front

                    // Cara derecha
                    1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
                    1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
                    1.0f,  1.0f,  1.0f,  // 3. right-top-front
                    1.0f,  1.0f, -1.0f,  // 7. right-top-back

                    // Cara superior.
                    -1.0f,  1.0f,  1.0f,  // 2. left-top-front
                    1.0f,  1.0f,  1.0f,  // 3. right-top-front
                    -1.0f,  1.0f, -1.0f,  // 5. left-top-back
                    1.0f,  1.0f, -1.0f,  // 7. right-top-back

                    // Cara inferior.
                    -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
                    1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
                    -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
                    1.0f, -1.0f,  1.0f   // 1. right-bottom-front
            };

    // Colores para los vertices
    private float[][] colores = {
            {1.0f, 0.0f, 1.0f, 1.0f},  // 0. ROSA.
            {1.0f, 1.0f, 0.0f, 1.0f},  // 1. AMARiLLO.
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. VERDE.
            {0.0f, 0.0f, 1.0f, 1.0f},  // 3. AZUL.
            {1.0f, 0.0f, 0.0f, 1.0f},  // 4. ROJO.
            {1.0f, 0.5f, 0.0f, 1.0f}   // 5. NARANJA.
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
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
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
        // Se inicializa el contexto

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

        /*
        // Forma alternativa.

        indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(indices).position(0);*/

        // Se inicializa el programa (Creacion del programa "OpenGL ES" en vacio).
        mProgram = GLES20.glCreateProgram();

        // Se agregan los shader code al programa.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Se agregan los shader code al programa.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Creación del programa "OpenGL ES" ejecutable.
        GLES20.glLinkProgram(mProgram);
    }
    public void draw(float[] mvpMatrix) {

        // Se agrega el programa al entorno OpenGL ES.
        GLES20.glUseProgram(mProgram);


        // Se preparan los datos de las coordenadas del cubo.

        // Se obtiene identificador vPosition del vertex shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Se habilita un controlador para los vértices del triángulo.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Preparar los datos de coordenadas del triángulo
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Se pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");


        for (int cara = 0; cara < numCaras; cara++) {
            // Se asigna el color a una de las caras.

            GLES20.glUniform4fv(colorHandle, 1, colores[cara], 0);
            indexBuffer.position(cara * 6);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

    }
}
