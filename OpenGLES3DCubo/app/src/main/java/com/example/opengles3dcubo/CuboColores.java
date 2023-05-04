package com.example.opengles3dcubo;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CuboColores {

    private FloatBuffer vertexBuffer; // Buffer para el arreglo de vertices.
    private FloatBuffer colorBuffer;  // Buffer para el arreglo de colores.
    private ByteBuffer indexBuffer;   // Buffer para el arreglo de indices.

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int positionHandle;
    private int colorHandle;

    // Definimos el número de coordenadas para vertices.
    private static final int COORDS_PER_VERTEX = 3;

    // 4 bytes por vertice.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Número de valores por colores en colores[].
    private static final int VALUES_PER_COLOR = 4;

    // Tamaño del color en bytes
    private final int COLOR_STRIDE = VALUES_PER_COLOR * 4;

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Vertices del cubo.
    private static final float vertices[] = {
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f
    };

    // Colores para los vertices.
    private static final float colores[] = {
            0.0f, 1.0f, 1.0f, 1.0f, // Cyan.
            1.0f, 0.0f, 0.0f, 1.0f, // Rojo.
            1.0f, 1.0f, 0.0f, 1.0f, // Amarillo.
            0.0f, 1.0f, 0.0f, 1.0f, // Verde.
            0.0f, 0.0f, 1.0f, 1.0f, // Azul.
            1.0f, 0.0f, 1.0f, 1.0f, // Rosa.
            1.0f, 1.0f, 1.0f, 1.0f, // Blanco.
            0.0f, 1.0f, 1.0f, 1.0f, // Cyan.
    };

    // Orden en que se dibujan los triangulos
    private static final byte indices[] = {
            0, 1, 3, 3, 1, 2, // Cara Frontal.
            0, 1, 4, 4, 5, 1, // Cara Inferior.
            1, 2, 5, 5, 6, 2, // Cara Derecha.
            2, 3, 6, 6, 7, 3, // Cara Superior.
            3, 7, 4, 4, 3, 0, // Cara Izquierda.
            4, 5, 7, 7, 6, 5 // Cara Trasera.
    };

    // Codigo shader para los vertices.
    private static String vertexShaderCode = "";

    // Codigo shader para el fragment.
    private static String fragmentShaderCode = "";

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

    public CuboColores(Context c) {

        // Se inicializa el contexto
        mContext = c;

        // Vertex Buffer.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Color buffer.
        byteBuffer = ByteBuffer.allocateDirect(colores.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuffer.asFloatBuffer();
        colorBuffer.put(colores);
        colorBuffer.position(0);

        // Index buffer.
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Se lee el código vertex shader desde archivo.
        vertexShaderCode = leerArchivo("CuboColoresVertexShader.glsl",c);

        // Se lee el código fragment shader desde archivo.
        fragmentShaderCode = leerArchivo("CuboColoresFragmentShader.glsl",c);

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
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Obtiene el identificador a_color desde fragment shader.
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(
                colorHandle, 4, GLES20.GL_FLOAT, false, COLOR_STRIDE, colorBuffer);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Se dibuja el cubo.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        // Desactiva los identificadores.
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
