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

    private FloatBuffer vertexBuffer;
    private FloatBuffer mCubeTextureCoordinates;
    private ShortBuffer indexBuffer;

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int colorHandle;
    private int positionHandle;

    /** Variable para pasar la información de coordenadas de la textura del modelo. */
    private int mTextureCoordinateHandle;

    private int mTextureUniformHandle;

    private int mTextureDataHandle0;
    private int mTextureDataHandle1;
    private int mTextureDataHandle2;
    private int mTextureDataHandle3;
    private int mTextureDataHandle4;
    private int mTextureDataHandle5;

    // Número de coordenadas por vertice en vertices[].
    static final int COORDS_PER_VERTEX = 3;

    // Tamaño de vértice en bytes.
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Numero de caras a dibujar.
    private int numCaras = 6;

    private final int mTextureCoordinateDataSize = 2;

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
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +

                    "attribute vec2 a_TexCoordinate;"+
                    "varying vec2 v_TexCoordinate;" +

                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "v_TexCoordinate = a_TexCoordinate;"+

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


    public static int loadTexture(Context ctx, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
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
        // Se inicializa el contexto

        // Vertex Buffer.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);

        vbb.order(ByteOrder.nativeOrder()); // Usar orden de bytes nativo.
        vertexBuffer = vbb.asFloatBuffer(); // Convierte de 'byte' a 'float'.
        vertexBuffer.put(vertices);         // Se copian los datos en el buffer.
        vertexBuffer.position(0); // Reinicia.

        /** Texture buffer*/
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        // Index buffer.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        /** Nuevas linea */
        mTextureDataHandle0 = loadTexture(c, R.drawable.pikachu);
        mTextureDataHandle1 = loadTexture(c, R.drawable.pokebola);
        mTextureDataHandle2 = loadTexture(c, R.drawable.charizard);
        mTextureDataHandle3 = loadTexture(c, R.drawable.blastoise);
        mTextureDataHandle4 = loadTexture(c, R.drawable.venusaur);
        mTextureDataHandle5 = loadTexture(c, R.drawable.pokemon_trainer);



        // Se inicializa el programa (Creacion del programa "OpenGL ES" en vacio).
        mProgram = GLES20.glCreateProgram();

        // Se agregan los shader code al programa.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Se agregan los shader code al programa.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        /** Nueva linea*/
        GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
        /*GLES20.glBindAttribLocation(mProgram, 1, "a_Color");*/
        GLES20.glBindAttribLocation(mProgram, 2, "a_TexCoordinate");

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
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        /** Nueva linea.*/
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");

        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");







        /** Se preparan los datos de la textura del cubo.*/
        /*mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);*/

        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        /*for (int cara = 0; cara < numCaras; cara++) {
            // Se asigna el color a una de las caras.

            GLES20.glUniform4fv(colorHandle, 1, colores[cara], 0);
            indexBuffer.position(cara * 6);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        }*/

        /** Nueva linea.*/
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        indexBuffer.position(0 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        /** Nueva linea.*/
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle1);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 1);

        indexBuffer.position(1 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        /** Nueva linea.*/
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle2);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 2);

        indexBuffer.position(2 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        /** Nueva linea.*/
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle3);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 3);

        indexBuffer.position(3 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        /** Nueva linea.*/
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle4);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 4);

        indexBuffer.position(4 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        /** Nueva linea.*/
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle5);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 5);

        indexBuffer.position(5 * 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Se pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

    }
}

