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
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class Objeto5 {

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private FloatBuffer textureBuffer;

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

    // Número de valores por colores en colores[].
    private static final int VALUES_PER_COLOR = 4;

    // Tamaño del color en bytes
    private final int COLOR_STRIDE = VALUES_PER_COLOR * 4;

    private int vPMatrixHandle;

    /** Lista de vertices*/
    private ArrayList<String> verticesList;

    /** Indices en los que se toman los vertices. */
    private ArrayList<String> facesList;

    /** Indices para las normales*/
    private ArrayList<String> normalList;

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

    private float[] vertices = {
            1.0f, -1.0f, -0.0f,
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, -0.0f,
            1.0f, -1.0f, -0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f};

    private float[] coords = {
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    private void cargarObjeto(Context context, String archivo){
        // Open the OBJ file with a Scanner
        try {
            Scanner scanner = new Scanner(context.getAssets().open(archivo));
            // Loop through all its lines
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("v ")) {
                    // Add vertex line to list of vertices
                    verticesList.add(line);
                    //Log.d("Leyo","vertice leido");
                } else if (line.startsWith("f ")) {
                    // Add face line to faces list
                    facesList.add(line);
                    //Log.d("Leyo","cara leida");
                } else if (line.startsWith("vn ")){
                    // Agrega linea de de las normales a la lista de las normales.
                    normalList.add(line);
                    //Log.d("Leyo","normal leida");
                }
            }
            Log.d("Leyo","Exito! Se encontró el archivo: "+ archivo);
        }catch (IOException e){
            e.printStackTrace();
            Log.d("Leyo","Fallo! No se encontró el archivo: "+ archivo);
        }

    }

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
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

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

    public Objeto5(Context c){
        mContext = c;

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        normalList = new ArrayList<>();

        cargarObjeto(c, "Plano.obj");

        /** Carga de la textura */
        mTextureDataHandle0 = loadTexture(c, R.drawable.texture_mapping_test_image);

        // Vertex Buffer.
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(vertices.length * vertexStride);
        byteBuffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer1.asFloatBuffer();

        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        // Texture buffer
        ByteBuffer tbb = ByteBuffer.allocateDirect(coords.length * vertexStride);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(coords);
        textureBuffer.position(0);

        // Faces Buffer.

        Log.d("tester","1");
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
        Log.d("tester","2");
        byteBuffer2.order(ByteOrder.nativeOrder());
        Log.d("tester","3");
        facesBuffer = byteBuffer2.asShortBuffer();
        Log.d("tester","4");

        /** Población */

        for(String face: facesList) {
            String vertexIndices[] = face.split(" ");

            /** Cara 1*/
            Log.d("tester","cara: " + vertexIndices[1]);
            String indices1[] = vertexIndices[1].split("/");
            short vertex1 = Short.parseShort(indices1[0]);
            facesBuffer.put((short)(vertex1-1));
            Log.d("tester","v1: "+ (vertex1-1));
            Log.d("tester","v1.1: "+ (indices1[1]));

            /** Cara 2*/
            Log.d("tester","cara 2: " + vertexIndices[2]);
            String indices2[] = vertexIndices[2].split("/");
            short vertex2 = Short.parseShort(indices2[0]);
            facesBuffer.put((short)(vertex2-1));
            Log.d("tester","v2: "+ (vertex2-1));

            /** Cara 3*/
            Log.d("tester","cara 3: " + vertexIndices[3]);
            String indices3[] = vertexIndices[3].split("/");
            short vertex3 = Short.parseShort(indices3[0]);
            facesBuffer.put((short)(vertex3-1));
            Log.d("tester","v3: "+ (vertex3-1));

        }
        facesBuffer.position(0);

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

        // Se preparan los datos de las coordenadas del cubo.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, verticesBuffer);


        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        /** Se preparan los datos de la textura de la figura.*/
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0);


        // Se dibuja el cubo.
//        GLES20.glDrawElements(
//                GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vertices.length / COORDS_PER_VERTEX);




        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}