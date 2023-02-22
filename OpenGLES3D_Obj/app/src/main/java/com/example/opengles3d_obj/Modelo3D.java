package com.example.opengles3d_obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Modelo3D {

    private float[] coords;
    private float[] vertices;

    /**
     * Lista de vertices
     */
    private ArrayList<String> verticesList;

    /**
     * Indices en los que se toman los vertices.
     */
    public ArrayList<String> facesList;

    /**
     * Datos de las coordenadas de la textura.
     */
    private ArrayList<String> texturaList;

    private ArrayList<Float> coordText;
    private ArrayList<Float> vertList;

    public Modelo3D() {

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        texturaList = new ArrayList<>();

        coordText = new ArrayList<>();
        vertList = new ArrayList<>();

    }

    public void cargarObjeto(Context context, String archivo) {
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
//                    Log.d("Leyo","cara leida");

                } else if (line.startsWith("vt ")) {
                    // Add texture line to texture list
//                    Log.d("Leyo","textura leida");
                    texturaList.add(line);
                }
            }
            Log.d("Leyo", "Exito! Se encontró el archivo: " + archivo);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Leyo", "Fallo! No se encontró el archivo: " + archivo);
        }

        poblar();
    }

    public int loadTexture(Context ctx, int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
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

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    public void poblar() {

        /** Vertex Buffer. */
        for (String face : facesList) {
            String vertexIndices[] = face.split(" ");

            /** indice 1*/
            String indices1[] = vertexIndices[1].split("/");
            short coord1 = Short.parseShort(indices1[0]);
            Log.d("tester", "coord 1: " + (coord1 - 1));

            String cadena = verticesList.get(coord1 - 1);
            Log.d("tester", "inside: " + cadena);

            String subCadena[] = cadena.split(" ");

            float x = Float.parseFloat(subCadena[1]);
            Log.d("tester", "x: " + x);
            vertList.add(x);

            float y = Float.parseFloat(subCadena[2]);
            Log.d("tester", "y: " + y);
            vertList.add(y);

            float z = Float.parseFloat(subCadena[3]);
            Log.d("tester", "z: " + z);
            vertList.add(z);

            /** indice 2*/
            String indices2[] = vertexIndices[2].split("/");
            short coord2 = Short.parseShort(indices2[0]);
            Log.d("tester", "coord 2: " + (coord2 - 1));

            String cadena2 = verticesList.get(coord2 - 1);
            Log.d("tester", "inside: " + cadena2);

            String subCadena2[] = cadena2.split(" ");

            float x2 = Float.parseFloat(subCadena2[1]);
            Log.d("tester", "x: " + x2);
            vertList.add(x2);

            float y2 = Float.parseFloat(subCadena2[2]);
            Log.d("tester", "y: " + y2);
            vertList.add(y2);

            float z2 = Float.parseFloat(subCadena2[3]);
            Log.d("tester", "z: " + z2);
            vertList.add(z2);

            /** indice 3*/
            String indices3[] = vertexIndices[3].split("/");
            short coord3 = Short.parseShort(indices3[0]);
            Log.d("tester", "coord 3: " + (coord3 - 1));

            String cadena3 = verticesList.get(coord3 - 1);
            Log.d("tester", "inside: " + cadena3);

            String subCadena3[] = cadena3.split(" ");

            float x3 = Float.parseFloat(subCadena3[1]);
            Log.d("tester", "x: " + x3);
            vertList.add(x3);

            float y3 = Float.parseFloat(subCadena3[2]);
            Log.d("tester", "y: " + y3);
            vertList.add(y3);

            float z3 = Float.parseFloat(subCadena3[3]);
            Log.d("tester", "z: " + z3);
            vertList.add(z3);
        }

        vertices = new float[vertList.size()];

        Log.d("tictoc", "vertList " + vertList.size());
        Log.d("tictoc", "vertices length " + vertices.length);

        for (int i = 0; i < vertList.size(); i++) {
//            Log.d("tictoc","i: "+ vertList.get(i));
            vertices[i] = vertList.get(i);
        }

        /** Textura Buffer*/
        for (String face : facesList) {
            String vertexIndices[] = face.split(" ");

            /** indice 1*/

            String indices1[] = vertexIndices[1].split("/");
            short coord1 = Short.parseShort(indices1[1]);
//            Log.d("tester","coord 1: "+ (coord1-1));

            String cadena = texturaList.get(coord1 - 1);
//            Log.d("tester","inside: "+ cadena);

            String subCadena[] = cadena.split(" ");

            float u = Float.parseFloat(subCadena[1]);
//            Log.d("tester","u: "+ u);

//            textureBuffer.put(u);}
            coordText.add(u);

            float v = Float.parseFloat(subCadena[2]);
//            Log.d("tester","v: "+ v);

//            textureBuffer.put(v);
            coordText.add(1 - v);

            /** indice 2*/

            String indices2[] = vertexIndices[2].split("/");
            short coord2 = Short.parseShort(indices2[1]);
//            Log.d("tester","coord 2: "+ (coord2-1));

            String cadena2 = texturaList.get(coord2 - 1);
//            Log.d("tester","inside: "+ cadena2);

            String subCadena2[] = cadena2.split(" ");

            float u2 = Float.parseFloat(subCadena2[1]);
//            Log.d("tester","u: "+ u2);

            coordText.add(u2);

            float v2 = Float.parseFloat(subCadena2[2]);
//            Log.d("tester","v: "+ v2);

            coordText.add(1 - v2);


            /** indice 3*/

            String indices3[] = vertexIndices[3].split("/");
            short coord3 = Short.parseShort(indices3[1]);
//            Log.d("tester","coord 3: "+ (coord3-1));

            String cadena3 = texturaList.get(coord3 - 1);
//            Log.d("tester","inside: "+ cadena3);

            String subCadena3[] = cadena3.split(" ");

            float u3 = Float.parseFloat(subCadena3[1]);
//            Log.d("tester","u: "+ u3);

            coordText.add(u3);

            float v3 = Float.parseFloat(subCadena3[2]);
//            Log.d("tester","v: "+ v3);

            coordText.add(1 - v3);
        }

        coords = new float[coordText.size()];

//        Log.d("tictoc","coords length "+ coords.length);

        for (int i = 0; i < coordText.size(); i++) {
            Log.d("coordes", "i: " + coordText.get(i));
            coords[i] = coordText.get(i);
        }

    }

    public float[] getTextura() {
        return coords;
    }

    public float[] getVertices() {
        return vertices;
    }
}
