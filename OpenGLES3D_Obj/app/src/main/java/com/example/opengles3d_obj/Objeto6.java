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

public class Objeto6 {

    private FloatBuffer verticesBuffer;
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

    private final int mTextureCoordinateDataSize = 2;

    private int vPMatrixHandle;

    /** Lista de vertices*/
    private ArrayList<String> verticesList;

    /** Indices en los que se toman los vertices. */
    private ArrayList<String> facesList;

    /** Datos de las coordenadas de la textura. */
    private ArrayList<String> texturaList;


    private float[] coords;
    private float[] vertices;

    private ArrayList<Float> coordText;
    private ArrayList<Float> vertList;
    private ArrayList<Short> indList;


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
                } else if (line.startsWith("vt ")) {
                    // Add texture line to texture list
                    texturaList.add(line);
                    Log.d("Leyo","textura leida");
                }
            }
            Log.d("Leyo","Exito! Se encontró el archivo: " + archivo);
        }catch (IOException e){
            e.printStackTrace();
            Log.d("Leyo","Fallo! No se encontró el archivo: " + archivo);
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

    public Objeto6(Context c){

        mContext = c;

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        texturaList = new ArrayList<>();

        coordText =  new ArrayList<>();
        vertList =  new ArrayList<>();
        indList =  new ArrayList<>();

        cargarObjeto(c, "Cubo.obj");

        /** Carga de la textura */
        mTextureDataHandle0 = loadTexture(c, R.drawable.cubo);

        /** Vertex Buffer. */
        for(String face: facesList) {
            String vertexIndices[] = face.split(" ");

            /** indice 1*/
            String indices1[] = vertexIndices[1].split("/");
            short coord1 = Short.parseShort(indices1[0]);
            Log.d("tester","coord 1: "+ (coord1-1));

            String cadena = verticesList.get(coord1-1);
            Log.d("tester","inside: "+ cadena);

            String subCadena[] = cadena.split(" ");

            float x = Float.parseFloat(subCadena[1]);
            Log.d("tester","x: "+ x);
            vertList.add(x);

            float y = Float.parseFloat(subCadena[2]);
            Log.d("tester","y: "+ y);
            vertList.add(y);

            float z = Float.parseFloat(subCadena[3]);
            Log.d("tester","z: "+ z);
            vertList.add(z);

            /** indice 2*/
            String indices2[] = vertexIndices[2].split("/");
            short coord2 = Short.parseShort(indices2[0]);
            Log.d("tester","coord 2: "+ (coord2-1));

            String cadena2 = verticesList.get(coord2-1);
            Log.d("tester","inside: "+ cadena2);

            String subCadena2[] = cadena2.split(" ");

            float x2 = Float.parseFloat(subCadena2[1]);
            Log.d("tester","x: "+ x2);
            vertList.add(x2);

            float y2 = Float.parseFloat(subCadena2[2]);
            Log.d("tester","y: "+ y2);
            vertList.add(y2);

            float z2 = Float.parseFloat(subCadena2[3]);
            Log.d("tester","z: "+ z2);
            vertList.add(z2);

            /** indice 3*/
            String indices3[] = vertexIndices[3].split("/");
            short coord3 = Short.parseShort(indices3[0]);
            Log.d("tester","coord 3: "+ (coord3-1));

            String cadena3 = verticesList.get(coord3-1);
            Log.d("tester","inside: "+ cadena3);

            String subCadena3[] = cadena3.split(" ");

            float x3 = Float.parseFloat(subCadena3[1]);
            Log.d("tester","x: "+ x3);
            vertList.add(x3);

            float y3 = Float.parseFloat(subCadena3[2]);
            Log.d("tester","y: "+ y3);
            vertList.add(y3);

            float z3 = Float.parseFloat(subCadena3[3]);
            Log.d("tester","z: "+ z3);
            vertList.add(z3);
        }

        vertices = new float[vertList.size()];

        Log.d("tictoc","vertList "+ vertList.size());
        Log.d("tictoc","vertices length "+ vertices.length);

        for (int i =0; i<vertList.size() ; i++){
//            Log.d("tictoc","i: "+ vertList.get(i));
            vertices[i] = vertList.get(i);
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        verticesBuffer = vbb.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        /** Textura Buffer*/
        for(String face: facesList) {
            String vertexIndices[] = face.split(" ");

            /** indice 1*/

            String indices1[] = vertexIndices[1].split("/");
            short coord1 = Short.parseShort(indices1[1]);
//            Log.d("tester","coord 1: "+ (coord1-1));

            String cadena = texturaList.get(coord1-1);
//            Log.d("tester","inside: "+ cadena);

            String subCadena[] = cadena.split(" ");

            float u = Float.parseFloat(subCadena[1]);
//            Log.d("tester","u: "+ u);

//            textureBuffer.put(u);}
            coordText.add(u);

            float v = Float.parseFloat(subCadena[2]);
//            Log.d("tester","v: "+ v);

//            textureBuffer.put(v);
            coordText.add(1-v);

            /** indice 2*/

            String indices2[] = vertexIndices[2].split("/");
            short coord2 = Short.parseShort(indices2[1]);
//            Log.d("tester","coord 2: "+ (coord2-1));

            String cadena2 = texturaList.get(coord2-1);
//            Log.d("tester","inside: "+ cadena2);

            String subCadena2[] = cadena2.split(" ");

            float u2 = Float.parseFloat(subCadena2[1]);
//            Log.d("tester","u: "+ u2);

            coordText.add(u2);

            float v2 = Float.parseFloat(subCadena2[2]);
//            Log.d("tester","v: "+ v2);

            coordText.add(1-v2);


            /** indice 3*/

            String indices3[] = vertexIndices[3].split("/");
            short coord3 = Short.parseShort(indices3[1]);
//            Log.d("tester","coord 3: "+ (coord3-1));

            String cadena3 = texturaList.get(coord3-1);
//            Log.d("tester","inside: "+ cadena3);

            String subCadena3[] = cadena3.split(" ");

            float u3 = Float.parseFloat(subCadena3[1]);
//            Log.d("tester","u: "+ u3);

            coordText.add(u3);

            float v3 = Float.parseFloat(subCadena3[2]);
//            Log.d("tester","v: "+ v3);

            coordText.add(1-v3);
        }

        coords = new float[coordText.size()];

//        Log.d("tictoc","coords length "+ coords.length);

        for (int i =0; i<coordText.size() ; i++){
            Log.d("coordes","i: "+ coordText.get(i));
            coords[i] = coordText.get(i);
        }

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