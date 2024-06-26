package com.example.triangulodearchivo;

// Importamos la bibliotecas necesarias.

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Triangulo {

    private FloatBuffer vertexBuffer;
    private int mProgram;
    private Context mContext ;

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public String leerArchivo(String file, Context con){
        //Log.d("Leyo","1");
        AssetManager assetManager = con.getResources().getAssets();
        Log.d("Leyo","Se accedio a la carpeta de assets");
        String mytext = "";
        try{
            //Log.d("Leyo","try");
            InputStream instream = assetManager.open(file);
            Log.d("Leyo","Abriendo recurso: "+file);
            int size = instream.available();
            byte[] buffer = new byte[size];
            instream.read(buffer);
            mytext = new String(buffer);
            instream.close();
            Log.d("Leyo","Exito al leer el archivo: "+file);
            return mytext;

        }catch (IOException e){
            e.printStackTrace();
            Log.d("Leyo","Fallo! No se encontró el archivo: "+file);
            return mytext;
        }
    }

    private String vertexShaderCode = "";

    private String fragmentShaderCode = "";

    // Definimos las coordenadas de los vertices
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:

                0.0f, 0.0f, 0.0f, // top

                -1.0f, -1.0f, 0.0f, // bottom left

                1.0f, -1.0f, 0.0f // bottom right

    };

    // Definimos el color con rojo, verde, azul and alpha (transparencia)
    // float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };

    public Triangulo(Context context){
        mContext = context;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(

        // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);

        // use the device hardware’s native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();

        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);

        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // Leemos el archivo que contiene el codigo para vertex shader.
        vertexShaderCode = leerArchivo("vertexShader.glsl",mContext);

        // Leemos el archivo que contiene el codigo para fragment shader.
        fragmentShaderCode = leerArchivo("fragmentShader.glsl",mContext);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // Log.d("Leyo","Si paso");
        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
