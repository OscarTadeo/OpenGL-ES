package com.example.opengles3dtextura;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cubo3 {

    private FloatBuffer vertexBuffer; // Buffer para el arreglo de vertices.
    private FloatBuffer textureBuffer; // Buffer para el arreglo de coords de la textura.
    private ShortBuffer indexBuffer; // Buffer para el arreglo de indices.

    private final int mProgram;

    private int positionHandle;

    // Variable para pasar la informacion de las coordenadas de la textura del modelo (Cubo).
    private int mTextureCoordinateHandle;

    // Variable donde se carga la textura.
    private int mTextureDataHandle;

    // Definimos el número de coordenadas para vertices.
    static final int COORDS_PER_VERTEX = 3;

    // 4 bytes por vertice.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Numero de caras a dibujar.
    private int numCaras = 6;

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Vertices del cubo
    static float vertices[]=
            {
                    // Cara Frontal.
                    -1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f,  1.0f,
                    1.0f,  1.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f,

                    // Cara izquierda.
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f,
                    -1.0f,  1.0f, -1.0f,

                    // Cara Trasera.
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f,  1.0f, -1.0f,
                    1.0f,  1.0f, -1.0f,

                    // Cara Derecha.
                    1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f,  1.0f, -1.0f,
                    1.0f,  1.0f,  1.0f,

                    // Cara superior.
                    -1.0f,  1.0f,  1.0f,
                    1.0f,  1.0f,  1.0f,
                    1.0f,  1.0f, -1.0f,
                    -1.0f,  1.0f, -1.0f,

                    // Cara Inferior.
                    -1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f,  1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
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
            0, 1, 2, 2, 3, 0,
            4, 5, 7, 5, 6, 7,
            8, 9, 11, 9, 10, 11,
            12, 13, 15, 13, 14, 15,
            16, 17, 19, 17, 18, 19,
            20, 21, 23, 21, 22, 23
    };

    // Coordenadas para la textura.
    final float[] cubeTextureCoordinateData =
            {
                    // Cara frontal.
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // Cara izquierda.
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // Cara trasera.
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // Cara derecha.
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // Cara inferior.
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // Cara inferior.
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,
                    0.0f, 1.0f,
            };

    private final String vertexShaderCode =

            "uniform mat4 uMVPMatrix;" +

                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_TexCoordinate;"+
                    "varying vec2 v_TexCoordinate;" +

                    "void main() {" +
                    "v_TexCoordinate = a_TexCoordinate;"+

                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +

                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "gl_FragColor = texture2D(u_Texture, v_TexCoordinate);"+
                    "}";

    // Metodo para mandar cargar la textura.
    public static int loadTexture(Context ctx, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public Cubo3(Context c)
    {

        // Vertex Buffer.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Textura buffer
        // (Forma alternativa de su implementación)
        textureBuffer = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(cubeTextureCoordinateData).position(0);

        // Index buffer.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Cargamos la textura.
        mTextureDataHandle = loadTexture(c, R.drawable.pokebola);

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

        // Se agrega el programa al entorno OpenGL ES.
        GLES20.glUseProgram(mProgram);

        // Obtiene el identificador vPosition desde vertex shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Obtiene el identificador a_TexCoordinate de la textura del cubo.
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, textureBuffer);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Se pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Se dibuja la figura.
        for (int cara = 0; cara < numCaras; cara++) {
            indexBuffer.position(cara * 6);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        }

        // Desactiva los identificadores.
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}