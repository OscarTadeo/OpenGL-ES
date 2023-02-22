package com.example.opengles3dtextura;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {

    private int m_iTextureId;

    public Texture(Context ctx){
        m_iTextureId = loadTexture(ctx, R.drawable.pikachu);
    }

    private int loadTexture(Context ctx, int rsrcId){

        /* OPGLES maneja unidades de textura (GL_TEXTURE0)
        * que sirven para extraer un pixel dada una coordenada. */
        int[] iTextureId = new int[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Se crea el objeto textura.
        GLES20.glGenTextures(1,iTextureId, 0);

        // Se verifica que tengamos una textura valida.
        if (iTextureId[0] != 0){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTextureId[0]);
            BitmapFactory.Options options = new BitmapFactory.Options();

            /* Opción para que al cargar la imagen conserve su tamaño original
               Si no android por automatico reescala a la resolución del dispositivo. */
            options.inScaled = false;

            /* Bitmap nos da mejor rendimiento en nuestra apliación.
            * Por ejemplo si un objeto esta lejos de camara no se renderiza en alta calidad*/
            Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(),rsrcId,options);

            // Se carga al imagen OpenGL ES.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap, 0);

            // Se libera memoria.
            bitmap.recycle();

            // Aplicamos filtros.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                                    GLES20.GL_TEXTURE_MIN_FILTER,
                                    GLES20.GL_NEAREST);

            // Aplicamos tipos de filtrados.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);

        }else{
            throw new RuntimeException("Error al cargar la textura");
        }

        return iTextureId[0];
    }
}
