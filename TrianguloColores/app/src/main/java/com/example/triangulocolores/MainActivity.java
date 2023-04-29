package com.example.triangulocolores;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    private GLSurfaceView gLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Creamos una instacia de GLSurfaceView y la configuramos
          como ContentView para esta actividad*/
        gLView = new MyGLSurfaceView(this);
        setContentView(gLView);
    }
}