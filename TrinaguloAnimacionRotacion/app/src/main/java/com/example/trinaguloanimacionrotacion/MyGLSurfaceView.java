package com.example.trinaguloanimacionrotacion;

import android.content.Context;
import android.opengl.GLSurfaceView;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Crea un contexto OpenGL ES 2.0
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(getContext());

        // Asigna el objeto encargado de renderizar los dibujos.
        setRenderer(renderer);
    }
}

