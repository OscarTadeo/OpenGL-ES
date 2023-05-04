package com.example.opengles3dcubo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.opengl.GLSurfaceView;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Creamos una instacia de GLSurfaceView y la configuramos
          como ContentView para esta actividad*/
        glView = new MyGLSurfaceView(this);
        setContentView(glView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}