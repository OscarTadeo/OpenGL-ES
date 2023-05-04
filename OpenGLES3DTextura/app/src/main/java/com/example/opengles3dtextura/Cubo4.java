package com.example.opengles3dtextura;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cubo4 {

    private FloatBuffer vertexBuffer; // Buffer para el arreglo de vertices.
    private FloatBuffer mCubeTextureCoordinates; // Buffer para el arreglo de coords de la textura.
    private ShortBuffer indexBuffer; // Buffer para el arreglo de indices.

    private final int mProgram;

    private int positionHandle;

    // Variable para pasar la informacion de las coordenadas de la textura del modelo (Cubo).
    private int mTextureCoordinateHandle;

    // Variable para pasar la textura al programa.
    private int mTextureUniformHandle;

    // Cada una de esta variables esta encargada de manejar una textura diferente.
    private int mTextureDataHandle0;
    private int mTextureDataHandle1;
    private int mTextureDataHandle2;
    private int mTextureDataHandle3;
    private int mTextureDataHandle4;
    private int mTextureDataHandle5;

    // Definimos el número de coordenadas para vertices.
    static final int COORDS_PER_VERTEX = 3;

    // 4 bytes por vertice.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Numero de caras a dibujar.
    private int numCaras = 6;

    // Constante para el definir el número de coord que la textura lee.
    private final int mTextureCoordinateDataSize = 2;

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Vertices del cubo
    static float vertices[]=
            {
                    // Cara Frontal.
                    -1.4f, -1.4f,  1.4f,
                    1.4f, -1.4f,  1.4f,
                    1.4f,  1.4f,  1.4f,
                    -1.4f,  1.4f,  1.4f,

                    // Cara izquierda.
                    -1.7f, -1.7f, -1.7f,
                    -1.7f, -1.7f,  1.7f,
                    -1.7f,  1.7f,  1.7f,
                    -1.7f,  1.7f, -1.7f,

                    // Cara Trasera.
                    1.4f, -1.4f, -1.4f,
                    -1.4f, -1.4f, -1.4f,
                    -1.4f,  1.4f, -1.4f,
                    1.4f,  1.4f, -1.4f,

                    // Cara Derecha.
                    1.6f, -1.6f,  1.6f,
                    1.6f, -1.6f, -1.6f,
                    1.6f,  1.6f, -1.6f,
                    1.6f,  1.6f,  1.6f,

                    // Cara superior.
                    -2.0f,  2.0f,  2.0f,
                    2.0f,  2.0f,  2.0f,
                    2.0f,  2.0f, -2.0f,
                    -2.0f,  2.0f, -2.0f,

                    // Cara Inferior.
                    -2.0f, -2.0f,  2.0f,
                    2.0f, -2.0f,  2.0f,
                    2.0f, -2.0f, -2.0f,
                    -2.0f, -2.0f, -2.0f,
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

                    // Cara superior.
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,

                    // Cara inferior.
                    1.0f, 0.0f,
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
            };

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +

                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_TexCoordinate;"+
                    "varying vec2 v_TexCoordinate;" +

                    "void main() {" +
                    "  v_TexCoordinate = a_TexCoordinate;"+
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +

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

    public Cubo4(Context c)
    {
        // Vertex Buffer.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Textura buffer
        // (Forma alternativa de su implementación)
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        // Index buffer.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Cargamos las texturas.
        mTextureDataHandle0 = loadTexture(c, R.drawable.pikachu);
        mTextureDataHandle1 = loadTexture(c, R.drawable.pokebola);
        mTextureDataHandle2 = loadTexture(c, R.drawable.charizard);
        mTextureDataHandle3 = loadTexture(c, R.drawable.blastoise);
        mTextureDataHandle4 = loadTexture(c, R.drawable.venusaur);
        mTextureDataHandle5 = loadTexture(c, R.drawable.pokemon_trainer);

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
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Obtiene el identificador a_TexCoordinate de la textura del cubo.
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        // Activa la unidad de textura.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Une la textura a esta unidad.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0);

        // Indica al shader usar la textura de la unidad.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Dibuja la cara.
        indexBuffer.position(0 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Unidad de textura 1.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle1);
        GLES20.glUniform1i(mTextureUniformHandle, 1);

        // Dibuja la cara.
        indexBuffer.position(1 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Unidad de textura 2.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle2);
        GLES20.glUniform1i(mTextureUniformHandle, 2);

        // Dibuja la cara.
        indexBuffer.position(2 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Unidad de textura 3.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle3);
        GLES20.glUniform1i(mTextureUniformHandle, 3);

        // Dibuja la cara.
        indexBuffer.position(3 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Unidad de textura 4.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle4);
        GLES20.glUniform1i(mTextureUniformHandle, 4);

        // Dibuja la cara.
        indexBuffer.position(4 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Unidad de textura 5.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle5);
        GLES20.glUniform1i(mTextureUniformHandle, 5);

        // Dibuja la cara.
        indexBuffer.position(5 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Se pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Desactiva los identificador de los vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}