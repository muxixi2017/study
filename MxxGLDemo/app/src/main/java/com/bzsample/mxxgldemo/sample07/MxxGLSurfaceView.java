package com.bzsample.mxxgldemo.sample07;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MxxGLSurfaceView extends GLSurfaceView {

    private MxxGLRenderer mRenderer = null;
    private MxxContext mContext;
    private String mBlendingID = "";

    public MxxGLSurfaceView(Context context) {
        super(context);
        mContext = new MxxContext(context);

        // Pick an OpenGL ES 2.0 context.
        // Use this method to create an OpenGL ES 2.0-compatible context.
        setEGLContextClientVersion(2);

        // create GLRenderer object
        mRenderer = new MxxGLRenderer(mContext);
        //mRenderer.setBlending(mBlendingID);

        // Set the renderer associated with GLSurfaceView
        setRenderer(mRenderer);
    }

    public void setBlending(String blendingID) {
        mBlendingID = blendingID;
        if (mRenderer != null) {
            mRenderer.setBlending(blendingID);
        }
    }
}
