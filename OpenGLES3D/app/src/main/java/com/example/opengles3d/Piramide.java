package com.example.opengles3d;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Piramide {

    private FloatBuffer vertexBuffer;  // Buffer para el arreglo de vertices.
    private FloatBuffer colorBuffer;   // Buffer para el arreglo de colores.
    private ByteBuffer indexBuffer;    // Buffer para el arreglo de indices.

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int positionHandle;
    private int colorHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes por vertice.


    // Definimos el número de coordenadas para vertices.
    static final int COORDS_PER_VERTEX = 3;

    // Variable para acceder y asignar la transformación de la vista.
    private int vPMatrixHandle;

    // Coordenadas de los vertices.
    static float triangleCoords[] = {
            -1.0f, -1.0f, -1.0f,  // 0. derecha-abajo-frente.
            1.0f, -1.0f, -1.0f,  // 1. izquierda-abajo-frente.
            1.0f, -1.0f,  1.0f,  // 2. izquierda-abajo-atras.
            -1.0f, -1.0f,  1.0f,  // 3. derecha-abajo-atras.
            0.0f,  1.0f,  0.0f   // 4. Punta-arriba.
    };

    // Colores para los vertices.
    private float colors[] = {
            0.0f, 0.0f, 1.0f, 1.0f, // Azul.
            0.0f, 1.0f, 0.0f, 1.0f, // Verde.
            0.0f, 0.0f, 0.0f, 1.0f, // Negro
            1.0f, 1.0f, 0.0f, 1.0f, // Amarillo.
            1.0f, 0.0f, 0.0f, 1.0f // Rojo.
    };

    // Indices de los vertices para formar los 6 Triangulos.
    private byte[] indices = {
            2, 4, 3,   // Cara trasera.
            1, 4, 2,   // Cara izquierda.
            1, 4, 0,   // Cara frontal.
            4, 0, 3,    // Cara derecha.
            0, 1, 2, 2, 3, 0 // Base.

    };

    private String vertexShaderCode = "";

    private String fragmentShaderCode = "";

    // Metodo para leer archivos desde las carpeta assets.
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

    public Piramide(Context c) {

        // Se inicializa el contexto.
        mContext = c;

        // Vertex buffer.
        // Inicializa el vertex byte buffer para las coordenadas de la figura.
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (cantidad de  valores de las coordenadas * 4 bytes por flotante).
                triangleCoords.length * 4);

        // Agrega las coordenadas al FloatBuffer.
        vbb.order(ByteOrder.nativeOrder());

        // Crea un buffer de punto flotante desde el ByteBuffer.
        vertexBuffer = vbb.asFloatBuffer();

        // Agrega las coordenadas al FloatBuffer.
        vertexBuffer.put(triangleCoords);

        // Configurar el buffer para leer la primera coordenada.
        vertexBuffer.position(0);

        // Color buffer.
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        // Indice buffer.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length);
        indexBuffer = ibb.order(ByteOrder.nativeOrder());
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Se lee el vertex shader desde archivo
        vertexShaderCode = leerArchivo("vertexShader.glsl",c);

        // Se lee el fragment shader desde archivo
        fragmentShaderCode = leerArchivo("fragmentShader.glsl",c);

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

        // Habilita un controlador para los vértices de la figura.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepara los datos de coordenadas de la figura.
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Obtiene el identificador a_color desde fragment shader.
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        // Dibuja la figura.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        // Obtiene el identificador para la matriz de transformacion de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pasa la transformación de proyección y vista al shader.
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Desactiva el identificador de vertices.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
