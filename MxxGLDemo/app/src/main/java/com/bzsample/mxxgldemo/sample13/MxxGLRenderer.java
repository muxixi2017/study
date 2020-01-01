package com.bzsample.mxxgldemo.sample13;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MxxGLRenderer implements GLSurfaceView.Renderer {
    protected MxxBaseFilter mShape;
    private MxxContext mContext;

    public MxxGLRenderer(MxxContext context) {
        mContext = context;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mShape = new MxxFluidFilter(mContext);
        mShape.onSurfaceCreated();
    }

    public void onDrawFrame(GL10 gl) {
        mShape.onDrawFrame();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mShape.onSurfaceChanged(width, height);
    }
}