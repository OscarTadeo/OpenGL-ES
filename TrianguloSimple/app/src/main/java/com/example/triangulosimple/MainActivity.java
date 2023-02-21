package com.example.triangulosimple;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /* Creamos el objeto de tipo GLSurfaceView para definir el area
       de visualización en pantalla */
    private GLSurfaceView gLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Creamos una instacia de GLSurfaceView y la configuramos
           como ContentView para esta actividad*/
        gLView = new MyGLSurfaceView(this);

        setContentView(gLView);
    }
}