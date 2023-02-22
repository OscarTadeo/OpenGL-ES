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

public class Objeto {

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;

    private final int mProgram;

    private Context mContext;

    private int positionHandle;

    // Número de coordenadas por vertice en vertices[].
    private static final int COORDS_PER_VERTEX = 3;

    // Tamaño de vértice en bytes.
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // Número de valores por colores en colores[].
    private static final int VALUES_PER_COLOR = 4;

    private int vPMatrixHandle;

    /** Lista de vertices*/
    private ArrayList<String> verticesList;

    /** Indices en los que se toman los vertices. */
    private ArrayList<String> facesList;

    private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "gl_FragColor = vec4(1.0, 0.5, 0, 1.0);"+
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
                    // Log.d("Leyo","cara leida");
                }
            }
            Log.d("Leyo","Exito! Se encontró el archivo: "+ archivo);
        }catch (IOException e){
            e.printStackTrace();
            Log.d("Leyo","Fallo! No se encontró el archivo: "+ archivo);
        }

    }


    public Objeto(Context c){
        mContext = c;

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();

        cargarObjeto(c, "Dona.obj");
        int tama = verticesList.size();
        Log.d("Leyo","vertices size: "+tama);

        int tama1 = facesList.size();
        Log.d("Leyo","face size: "+tama1);
        Log.d("Leyo","face elem: "+facesList.get(0));


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
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, verticesBuffer);

        // Se aplica la transformación de proyección y vista.
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Se dibuja el cubo.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}