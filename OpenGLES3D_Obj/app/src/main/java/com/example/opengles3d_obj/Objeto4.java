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

public class Objeto4 {

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

    private final int mTextureCoordinateDataSize = 2;

    private int vPMatrixHandle;

    /** Lista de vertices*/
    private ArrayList<String> verticesList;

    /** Indices en los que se toman los vertices. */
    private ArrayList<String> facesList;

    /** Datos de las coordenadas de la textura. */
    private ArrayList<String> texturaList;

    //Arrays
    private float [] vertices;
    private float texturas [];
    private short indices [];

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

    public Objeto4(Context c){

        mContext = c;

        verticesList = new ArrayList<>();
        texturaList = new ArrayList<>();
        facesList = new ArrayList<>();

        cargarObjeto(c, "Plano.obj");

        vertices = new float[verticesList.size() * 3];
        texturas = new float[texturaList.size() * 3];
        indices = new short[facesList.size() * 3];

        // Preprocesamiento

        //Llenamos los arreglos con los datos.
        for (int i=0; i < verticesList.size(); i++){

            String coords[] = verticesList.get(i).split(" "); // Split by space

                float x = Float.parseFloat(coords[1]);
//                Log.d("coord","X: "+x);
                vertices[3*i] = x;

                float y = Float.parseFloat(coords[2]);
//                Log.d("coord","Y: "+y);
                vertices[(3*i)+1] = y;

                float z = Float.parseFloat(coords[3]);
//                Log.d("coord","Z: "+z);
                vertices[(3*i)+2] = z;
        }

//        for (int i=0; i < vertices.length; i++){
//            Log.d("Leyo","vertice "+ i + ": " +vertices[i]);
//        }

//        Log.d("Leyo","vertices size: "+ vertices.length);
//        Log.d("Leyo","verList size: "+ verticesList.size());
//        Log.d("Leyo","textu 0: "+ texturaList.get(475));

        /** Vertex Buffer. */
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(verticesList.size() * vertexStride);
        byteBuffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer1.asFloatBuffer();
        // Poblacion del buffer.
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        /** ---------------------------------------------------------------------------------- */

        Log.d("Leyo","texturas size: "+ texturas.length);
        Log.d("Leyo","texturaList size: "+ texturaList.size());

        for (int i = 0; i < facesList.size(); i++){
            String caras[] = facesList.get(i).split(" ");

//            Log.d("tester","Cara: " + i);

            // Lectura de la cara 1.
            String ind_vert_1[] = caras[1].split("/");
            short indice1 = Short.parseShort(ind_vert_1[0]);
//            Log.d("tester","indice vert 1: " + indice1);

            // Al arreglo
            indices[3*i] = (short)(indice1-1);

            // Lectura de la cara 2.
            String ind_vert_2[] = caras[2].split("/");
            short indice2 = Short.parseShort(ind_vert_2[0]);
//            Log.d("tester","indice vert 2: " + indice2);
            indices[3*i+1] = (short)(indice2-1);

            // Lectura de la cara 3.
            String ind_vert_3[] = caras[3].split("/");
            short indice3 = Short.parseShort(ind_vert_3[0]);
//            Log.d("tester","indice vert 3: " + indice3);
            indices[3*i+2] = (short)(indice3-1);

//            Log.d("tester"," ");
        }

//        for (int i=0; i < indices.length; i++){
//            Log.d("tester","indice "+ i + ": " +indices[i]);
//        }

        /** Faces Buffer. */
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(facesList.size() * vertexStride);
        byteBuffer2.order(ByteOrder.nativeOrder());
        facesBuffer = byteBuffer2.asShortBuffer();
        // Población
        facesBuffer.put(indices);
        facesBuffer.position(0);

        /** ---------------------------------------------------------------------------------- */

        /** Textura Buffer*/

        for(int i = 0; i < facesList.size(); i++) {

            String caras[] = facesList.get(i).split(" ");

            Log.d("tester","Cara: " + i);

            // Lectura de la cara 1.
            String ind_textura_1[] = caras[1].split("/");
            short indice1 = Short.parseShort(ind_textura_1[1]);
            Log.d("tester","indice text 1: " + indice1);

                // Agregamos 'u' y 'v' al arreglo 'texturas'.
                String uv[] = texturaList.get(indice1-1).split(" ");

                float u = Float.parseFloat(uv[1]);
                Log.d("tester","u: " + u);
                texturas[3*(2*i)] = u;

                float v = Float.parseFloat(uv[2]);
                Log.d("tester","v: " + v);
                texturas[(3*(2*i))+1] = v;

            // Lectura de la cara 2.
            String ind_textura_2[] = caras[2].split("/");
            short indice2 = Short.parseShort(ind_textura_2[1]);
            Log.d("tester","indice text 2: " + indice2);

                // Agregamos 'u' y 'v' al arreglo 'texturas'.
                String uv2[] = texturaList.get(indice2-1).split(" ");

                float u2 = Float.parseFloat(uv2[1]);
                Log.d("tester","u: " + u2);
                texturas[(3*(2*i))+2] = u2;

                float v2 = Float.parseFloat(uv2[2]);
                Log.d("tester","v: " + v2);
                texturas[(3*(2*i))+3] = v2;

            // Lectura de la cara 3.
            String ind_textura_3[] = caras[3].split("/");
            short indice3 = Short.parseShort(ind_textura_3[1]);
            Log.d("tester","indice text 3: " + indice3);

                // Agregamos 'u' y 'v' al arreglo 'texturas'.
                String uv3[] = texturaList.get(indice3-1).split(" ");

                float u3 = Float.parseFloat(uv3[1]);
                Log.d("tester","u: " + u3);
                texturas[(3*(2*i))+4] = u3;

                float v3 = Float.parseFloat(uv3[2]);
                Log.d("tester","v: " + v3);
                texturas[(3*(2*i))+5] = v3;
        }

        for (int i=0; i < texturas.length; i++){
            Log.d("tester","indice "+ i + ": " +texturas[i]);
        }

        ByteBuffer byteBuffer3 = ByteBuffer.allocateDirect(texturaList.size() * vertexStride);
        byteBuffer3.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer3.asFloatBuffer();
        //Población.
        textureBuffer.put(texturas);
        textureBuffer.position(0);

        /** Carga de la textura */
        mTextureDataHandle0 = loadTexture(c, R.drawable.texture_mapping_test_image);

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
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, textureBuffer);

        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);

    }
}