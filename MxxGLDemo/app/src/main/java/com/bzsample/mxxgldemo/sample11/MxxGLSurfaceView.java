package com.bzsample.mxxgldemo.sample11;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MxxGLSurfaceView extends GLSurfaceView {

    public MxxGLRenderer mRenderer = null;
    private MxxContext mContext;

    public MxxGLSurfaceView(Context context) {
        super(context);
        mContext = new MxxContext(context);

        // Pick an OpenGL ES 2.0 context.
        // Use this method to create an OpenGL ES 2.0-compatible context.
        setEGLContextClientVersion(2);

        // create GLRenderer object
        mRenderer = new MxxGLRenderer(mContext);

        // Set the renderer associated with GLSurfaceView
        setRenderer(mRenderer);
    }


}
