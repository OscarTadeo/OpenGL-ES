package com.example.triangulodearchivo;

import android.content.Context;
import android.opengl.GLSurfaceView;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context){

        super(context);

// Create an OpenGL ES 2.0 context

        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(getContext());

// Set the Renderer for drawing on the MyGLSurfaceView

        setRenderer(renderer);

    }

}