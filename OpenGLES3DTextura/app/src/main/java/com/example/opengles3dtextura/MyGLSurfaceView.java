package com.example.opengles3dtextura;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context){
        super(context);

        // Crea un contexto OpenGL ES 2.0
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(getContext());

        // Asigna el objeto encargado de renderizar los dibujos.
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reporta los detalles de entrada desde la pantalla táctil
        // y otros controles de entrada.

        // En este caso, solo los eventos en los que cambia
        // la posición cuando se toca la pantalla tactil.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }
}