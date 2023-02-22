package com.example.opengles3d_obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class Objeto7 {

    private FloatBuffer verticesBuffer;
    private FloatBuffer textureBuffer;
    private Modelo3D modelo3D;

    private final int mProgram;

    private Context mContext;

    private int positionHandle;

    /** Variable para pasar la información de coordenadas de la textura del modelo. */
    private int mTextureCoordinateHandle;

    private int mTextureUniformHandle;

    private int mTextureDataHandle0;

    // Número de coordenadas por vertice en vertices[].
    private static final int COORDS_PER_VERTEX = 3;

    // Tamaño de vértice en bytes.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private final int mTextureCoordinateDataSize = 2;

    private int vPMatrixHandle;

    private float[] coords;
    private float[] vertices;

    private final String vertexShaderCode =
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec2 a_TexCoordinate;"+
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "   v_TexCoordinate = a_TexCoordinate;"+
                    "   gl_Position = u_MVPMatrix * a_Position;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
                    "}";

    public Objeto7(Context c){

        mContext = c;

        modelo3D = new Modelo3D();

        modelo3D.cargarObjeto(c, "Cubo.obj");
//        modelo3D.poblar();

        Log.d("testeo", "faces " + modelo3D.facesList.size());
        Log.d("testeo", "vertices " + modelo3D.getVertices().length);
        Log.d("testeo", "textura " + modelo3D.getTextura().length);


        /** Carga de la textura */
//        mTextureDataHandle0 = loadTexture(c, R.drawable.cubierta);
        mTextureDataHandle0 = modelo3D.loadTexture(c, R.drawable.cubo);

        /** Vertex Buffer. */

        vertices = modelo3D.getVertices();

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        verticesBuffer = vbb.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        /** Textura Buffer*/

        coords = modelo3D.getTextura();

        ByteBuffer tbb = ByteBuffer.allocateDirect(coords.length * vertexStride);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(coords);
        textureBuffer.position(0);


        mProgram = GLES20.glCreateProgram();

        // Se agregan los shader code al programa.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Creación del programa "OpenGL ES" ejecutable.
        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix){

        // Se agrega el programa al entorno OpenGL ES.
        GLES20.glUseProgram(mProgram);

        // Se preparan los datos de las coordenadas de la figura.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, verticesBuffer);

        /** Se preparan los datos de la textura de la figura.*/
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0);

        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vertices.length / COORDS_PER_VERTEX);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}