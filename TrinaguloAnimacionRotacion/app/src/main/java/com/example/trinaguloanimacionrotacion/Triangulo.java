package com.example.trinaguloanimacionrotacion;

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

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes por vertice.

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    private String vertexShaderCode = "";

    private String fragmentShaderCode = "";

    // Metodo para leer una archivo desde el ambiente del proyecto.
    public String leerArchivo(String file, Context con){

        AssetManager assetManager = con.getResources().getAssets();
        Log.d("Leyo","Se accedio a la carpeta de assets");
        String mytext = "";
        try{

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

    // Definimos el numero de coordenadas para vertices.
    static final int COORDS_PER_VERTEX = 3;

    // Coordenadas de la figura.
    static float triangleCoords[] = {
            0.0f,  0.622008459f, 0.0f, // Arriba.
            -0.5f, -0.311004243f, 0.0f, // Izquierda.
            0.5f, -0.311004243f, 0.0f  // Derecha.
    };

    // Definimos color.
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangulo(Context c) {
        mContext = c;

        // Inicializa el vertex byte buffer para las coordenadas de la figura
        ByteBuffer bb = ByteBuffer.allocateDirect(

                // (cantidad de  valores de las coordenadas * 4 bytes por flotante).
                triangleCoords.length * 4);

        // Usa el orden de bytes nativo del hardware del dispositivo.
        bb.order(ByteOrder.nativeOrder());

        // Crea un buffer de punto flotante desde el ByteBuffer.
        vertexBuffer = bb.asFloatBuffer();

        // Agrega las coordenadas al FloatBuffer.
        vertexBuffer.put(triangleCoords);

        // Configurar el buffer para leer la primera coordenada.
        vertexBuffer.position(0);

        // Leemos el archivo que contiene el codigo para vertex shader.
        vertexShaderCode = leerArchivo("VertexShader.glsl",c);

        // Leemos el archivo que contiene el codigo para fragment shader.
        fragmentShaderCode = leerArchivo("FragmentShader.glsl",c);

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

        // Habilita un controlador para los vértices del triángulo.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepara los datos de coordenadas del triángulo.
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Obtiene el identificador vColor desde fragment shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Asigna el color para dibujar el triangulo.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Dibuja el triangulo.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Desactiva el arreglo de vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);

        // Obtiene el identificador para la matriz de transformacion del triangulo.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
    }
}
