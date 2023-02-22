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

public class Piramide {

    private FloatBuffer vertexBuffer;  // Buffer para el arreglo de vertices.
    private FloatBuffer colorBuffer;   // Buffer para el arreglo de colores.
    private ByteBuffer indexBuffer;    // Buffer para el arreglo de indices.

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    // Variable para el manejo de los vertices.
    private int positionHandle;

    // Variable para el manejo de colores.
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    // number of coordinates per vertex in this array
    // Definimos el numero de coordenadas por vertices
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            -1.0f, -1.0f, -1.0f,  // 0. left-bottom-back
            1.0f, -1.0f, -1.0f,  // 1. right-bottom-back
            1.0f, -1.0f,  1.0f,  // 2. right-bottom-front
            -1.0f, -1.0f,  1.0f,  // 3. left-bottom-front
            0.0f,  1.0f,  0.0f   // 4. top
    };

    private float colors[] = { // Colors for the vertices (NEW)
            0.0f, 0.0f, 1.0f, 1.0f, // AZUL
            0.0f, 1.0f, 0.0f, 1.0f, // VERDE
            0.0f, 0.0f, 0.0f, 1.0f, // NEGRO
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO
            1.0f, 0.0f, 0.0f, 1.0f // ROJO
    };

    private byte[] indices = { // Indices de los vertices para formar los 4 Triangulos
            2, 4, 3,   // front face (CCW)
            1, 4, 2,   // right face
            0, 4, 1,   // back face
            4, 0, 3    // left face
    };

    private String vertexShaderCode = "";

    private String fragmentShaderCode = "";

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

    private int vPMatrixHandle;

    public Piramide(Context c) {

        // Se inicializa el contexto
        mContext = c;

        // Vertex buffer
        // initialize vertex byte buffer for shape coordinates
        // Se inicializa bytebuffer para la coordenadas de la figura
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                // (valor del número de coordenadas * 4 bytes por flotante).
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        // Se utiliza el orden nativo de bytes de hardware  del dispotivo
        vbb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        // Pasamos de Bytebuffer a FloatBuffer
        vertexBuffer = vbb.asFloatBuffer();

        // add the coordinates to the FloatBuffer
        // Se agregan las coordenadas al FloatBuffer
        vertexBuffer.put(triangleCoords);

        // set the buffer to read the first coordinate
        // Indicamos al buffer el indice a leer la primer coordenada.
        vertexBuffer.position(0);

        // Color buffer
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder()); // Use native byte order (NEW)
        colorBuffer = cbb.asFloatBuffer();  // Convert byte buffer to float (NEW)
        colorBuffer.put(colors);            // Copy data into buffer (NEW)
        colorBuffer.position(0);            // Rewind (NEW)

        // Setup index-array buffer. Indices in byte.
        /*indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder());*/
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length);
        indexBuffer = ibb.order(ByteOrder.nativeOrder());
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Se lee el vertex shader desde archivo
        vertexShaderCode = leerArchivo("CuboColoresVertexShader.glsl",c);

        // Se lee el fragment shader desde archivo
        fragmentShaderCode = leerArchivo("CuboColoresFragmentShader.glsl",c);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        // Se inicializa el programa ( Creacion del programa OpenGL ES vacio  )
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        // Se agrega vertex shader al programa
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        // Se agrega el fragment shader al programa
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        // Creación del programa OpenGL Es ejecutable
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        // Agregamos el programa al ambiente de OpenGL ES
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        // Obtenemos el identificador para el manejo de los vertices

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        // Habilitamos el manejador de los vertices del triangulo
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        // Preparamos los datos de coordenadas del triangulo
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        // Habilitamos el manejador para eñ fragment shader
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);


        /*// Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);*/

        // Dibujamos la piramide
        // Indicamos que se dibuje la figura conforme al arreglo de indices.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        // get handle to shape's transformation matrix
        // Manejo de la matriz de transformación de la figura.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        // Pasamos la transformacion proyeccion y vista al shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        /*// Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);*/

        // Disable vertex array
        // Desabilitamos el manejador de las posiciones
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
