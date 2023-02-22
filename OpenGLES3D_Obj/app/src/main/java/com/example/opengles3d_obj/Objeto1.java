package com.example.opengles3d_obj;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class Objeto1 {

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    private final int mProgram;

    private Context mContext;

    private int positionHandle;
    private int colorHandle;
    private int mLightPos;
    private int mNormalHandle;

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

    /*private final String vertexShaderCode =
                    "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "uniform vec3 u_LightPos;"+     // A constant representing the light source position
                    "attribute vec3 a_Normal;"+
                            "varying vec4 v_Color;"+

                    "void main() {" +
                            "   vec3 modelViewVertex = vec3(u_MVPMatrix * a_Position);" +
                            "   vec3 modelViewNormal = vec3(u_MVPMatrix * vec4(a_Normal, 0.0));" +
                            "   float distance = length(u_LightPos - modelViewVertex);" +
                            "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);" +
                            "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);" +
                            "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));" +
                            "   v_Color = a_Color * vec4(diffuse);" +

                    "  gl_Position = u_MVPMatrix * a_Position;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 v_Color;"+
                    "void main() {" +
                    "gl_FragColor = v_Color"+
                    "}";*/

    private float colores[] = {
            1.0f, 0.0f, 1.0f, 1.0f  // 0. ROSA.
            /*{1.0f, 1.0f, 0.0f, 1.0f},  // 1. AMARiLLO.
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. VERDE.
            {0.0f, 0.0f, 1.0f, 1.0f},  // 3. AZUL.
            {1.0f, 0.0f, 0.0f, 1.0f},  // 4. ROJO.
            {1.0f, 0.5f, 0.0f, 1.0f}   // 5. NARANJA.*/
    };

    private final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec4 a_color;"+
                    "uniform vec4 v_Color;"+

                    "void main() {" +

                    /*"  v_Color = a_color;"+*/
                    "  gl_Position = u_MVPMatrix * a_Position;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 v_Color;"+
                    "void main() {" +
//                    "v_Color = vec4(1.0, 0.5, 0, 1.0);" +
//                    "gl_FragColor = vec4(1.0, 0.5, 0, 1.0);"+
                        "gl_FragColor = v_Color;"+
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


    public Objeto1(Context c){
        mContext = c;

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        normalList = new ArrayList<>();

        cargarObjeto(c, "Dona.obj");

        /*int tama = verticesList.size();
        Log.d("Leyo","vertices size: "+tama);

        int tama1 = facesList.size();
        Log.d("Leyo","face size: "+tama1);
        Log.d("Leyo","face elem: "+facesList.get(0));*/
        /*Log.d("Leyo","normal size: "+ normalList.size());
        Log.d("Leyo","normal 0: "+ normalList.get(89));*/



        // Vertex Buffer.
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(verticesList.size() * vertexStride);
        byteBuffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer1.asFloatBuffer();

        /** Población */

        for(String vertex: verticesList) {
            String coords[] = vertex.split(" "); // Split by space
            float x = Float.parseFloat(coords[1]);

            Log.d("coord","X: "+x);
            float y = Float.parseFloat(coords[2]);
            Log.d("coord","Y: "+y);
            float z = Float.parseFloat(coords[3]);
            Log.d("coord","Z: "+x);
            verticesBuffer.put(x);
            verticesBuffer.put(y);
            verticesBuffer.put(z);
        }
        verticesBuffer.position(0);

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

        // Color buffer.
        byteBuffer1 = ByteBuffer.allocateDirect(colores.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuffer1.asFloatBuffer();
        colorBuffer.put(colores);
        colorBuffer.position(0);

        // Normal Buffer.

        ByteBuffer byteBuffer3 = ByteBuffer.allocateDirect(normalList.size() * vertexStride);
        byteBuffer3.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuffer3.asFloatBuffer();

        for(String vertex: normalList) {
            /*Log.d("prueba","SI");*/
            String coords[] = vertex.split(" "); // Split by space
            /*Log.d("prueba","SI: "+coords[1]);*/

            float x = Float.parseFloat(coords[1]);
            Log.d("coordN","X: "+x);
            float y = Float.parseFloat(coords[2]);
            Log.d("coordN","Y: "+y);
            float z = Float.parseFloat(coords[3]);
            Log.d("coordN","Z: "+z);
            normalBuffer.put(x);
            normalBuffer.put(y);
            normalBuffer.put(z);
        }
        normalBuffer.position(0);



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

        // Se preparan los datos de color del cubo.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "v_Color");
        GLES20.glUniform4fv(colorHandle, 1, colores, 0);

        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(
                mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);


        /*GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(
                mNormalHandle, 3, GLES20.GL_FLOAT, false, vertexStride, normalBuffer);*/




        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

//        /** Luz */
//        mLightPos = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
//
//        /** Incidencia Luz */
//        GLES20.glUniform3f(mLightPos, 0.0f, 0.0f, 0.0f);

        //mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");

//        colorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");



        // Se dibuja el cubo.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);





        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}