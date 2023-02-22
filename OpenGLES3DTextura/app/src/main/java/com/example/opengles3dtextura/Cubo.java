package com.example.opengles3dtextura;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cubo {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer mCubeTextureCoordinates;
    private ByteBuffer indexBuffer;

    private final int mProgram;

    // Contexto necesario para leer archivos de proyecto.
    private Context mContext;

    private int positionHandle;
    private int colorHandle;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    private int mTextureDataHandle;

    // Número de coordenadas por vertice en vertices[].
    private static final int COORDS_PER_VERTEX = 3;

    // Tamaño de vértice en bytes.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Número de valores por colores en colores[].
    private static final int VALUES_PER_COLOR = 4;

    // Tamaño del color en bytes
    private final int COLOR_STRIDE = VALUES_PER_COLOR * 4;

    private int vPMatrixHandle;

    // Vertices del cubo
    private static final float vertices[] = {

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
    private static final float colores[] = {
            // Cara frontal
            0.0f, 1.0f, 1.0f, 1.0f, // CYAN
            1.0f, 0.0f, 1.0f, 1.0f, // ROSA
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO
            0.0f, 1.0f, 0.0f, 1.0f, // VERDE

            // Cara izquierda
            1.0f, 0.0f, 0.0f, 1.0f, // ROJO
            0.0f, 1.0f, 1.0f, 1.0f, // CYAN
            0.0f, 1.0f, 0.0f, 1.0f, // VERDE
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO

            // Cara Trasera.
            1.0f, 1.0f, 1.0f, 1.0f, // BLANCO
            1.0f, 0.0f, 0.0f, 1.0f, // ROJO
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO
            0.0f, 0.0f, 1.0f, 1.0f, // AZUL

            // Cara derecha.
            1.0f, 0.0f, 1.0f, 1.0f, // ROSA
            1.0f, 1.0f, 1.0f, 1.0f, // BLANCO
            0.0f, 0.0f, 1.0f, 1.0f, // AZUL
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO

            // Cara superior.
            0.0f, 1.0f, 0.0f, 1.0f, // VERDE
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO
            0.0f, 0.0f, 1.0f, 1.0f, // AZUL
            1.0f, 1.0f, 0.0f, 1.0f, // AMARILLO

            // Cara Inferior.
            0.0f, 1.0f, 1.0f, 1.0f, // CYAN
            1.0f, 0.0f, 1.0f, 1.0f, // ROSA
            1.0f, 1.0f, 1.0f, 1.0f, // BLANCO
            1.0f, 0.0f, 0.0f, 1.0f, // ROJO
    };

    // Orden en que se dibujan los triangulos
    private static final byte indices[] = {
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
            Log.d("Leyo","Abriendo recurso: "+ file);
            int size = instream.available();
            byte[] buffer = new byte[size];
            instream.read(buffer);
            mytext = new String(buffer);
            instream.close();
            Log.d("Leyo","Exito al leer el archivo: "+ file);
            return mytext;

        }catch (IOException e){
            e.printStackTrace();
            Log.d("Leyo","Fallo! No se encontró el archivo: "+file);
            return mytext;
        }
    }

    public int loadTexture(Context ctx, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        // Se verifica que tengamos una textura valida.
        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();

            /* Opción para que al cargar la imagen conserve su tamaño original, de lo
               contrario android en automatico reescala a la resolución del dispositivo. */
            options.inScaled = false;

            /* Bitmap nos da mejor rendimiento en nuestra apliación.
               Por ejemplo si un objeto esta lejos de camara no se renderiza en alta calidad
               Se lee de fuente*/

            // Se accede a los recursos y se extraela imagen solicitada.
            final Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);

            // Se vincula a la textura en OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Establecer tipo de filtrado.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Se carga al imagen OpenGL ES (mapa de bits en la textura enlazada).
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Se carga al imagen OpenGL ES.
            // Se libera memoria.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error al cargar textura.");
        }

        return textureHandle[0];
    }

    public Cubo(Context c) {

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

        /** Texture buffer*/
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        // Se lee el código vertex shader desde archivo
        vertexShaderCode = leerArchivo("CuboVertexShader.glsl",c);

        // Se lee el código fragment shader desde archivo
        fragmentShaderCode = leerArchivo("CuboFragmentShader.glsl",c);

        /** Nueva linea */
        mTextureDataHandle = loadTexture(c, R.drawable.pikachu);

        // Se inicializa el programa (Creacion del programa "OpenGL ES" en vacio).
        mProgram = GLES20.glCreateProgram();

        /*// Se agregan los shader code al programa.
        GLES20.glAttachShader(mProgram, MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        GLES20.glAttachShader(mProgram, MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));*/

        // Se agregan los shader code al programa.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Creación del programa "OpenGL ES" ejecutable.
        GLES20.glLinkProgram(mProgram);
    }

    /*
     * Encapsula las instrucciones de OpenGL ES para dibujar la figura.
     *
     * @param mvpMatrix
     *      La matriz Model View Project en la que se dibuja la figura.
     */
    public void draw(float[] mvpMatrix) {


        // Se agrega el programa al entorno OpenGL ES.
        GLES20.glUseProgram(mProgram);

        // Se preparan los datos de las coordenadas del cubo.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Se preparan los datos de color del cubo.
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(
                colorHandle, 4, GLES20.GL_FLOAT, false, COLOR_STRIDE, colorBuffer);

        /** Se preparan los datos de la textura del cubo.*/
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);
        /*GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);*/


        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        /** Nueva linea*/
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");

        /** Con esta linea definimos que textura es asignada
         * Se tomará la textura definida en GLES20.GL_TEXTURE0, ..., GLES20.GL_TEXTUREN*/
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Se dibuja el cubo.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        // Se Deshabilitan las matrices de vértices.
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);


        /*GLES20.glEnable(GLES20.GL_CULL_FACE);*/


    }
}
