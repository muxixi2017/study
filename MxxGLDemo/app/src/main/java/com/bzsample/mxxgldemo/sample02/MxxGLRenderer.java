package com.bzsample.mxxgldemo.sample02;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MxxGLRenderer implements GLSurfaceView.Renderer {
    private MxxShape mShape;
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mShape = new MxxShape();
    }

    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mShape.draw();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // sets the viewport
        GLES20.glViewport(0, 0, width, height);
    }
}