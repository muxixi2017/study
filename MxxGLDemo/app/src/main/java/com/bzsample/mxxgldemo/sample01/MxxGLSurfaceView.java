package com.bzsample.mxxgldemo.sample01;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MxxGLSurfaceView extends GLSurfaceView {

    private MxxGLRenderer mRenderer = null;

    public MxxGLSurfaceView(Context context) {
        super(context);

        // Pick an OpenGL ES 2.0 context.
        // Use this method to create an OpenGL ES 2.0-compatible context.
        setEGLContextClientVersion(2);

        // create GLRenderer object
        mRenderer = new MxxGLRenderer();

        // Set the renderer associated with GLSurfaceView
        setRenderer(mRenderer);
    }
}
