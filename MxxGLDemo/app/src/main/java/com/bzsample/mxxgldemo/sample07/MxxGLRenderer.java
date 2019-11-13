package com.bzsample.mxxgldemo.sample07;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MxxGLRenderer implements GLSurfaceView.Renderer {
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private MxxShape mShape1;
    private MxxShape mShape2;
    private MxxContext mContext;

    int mSFactor = GLES20.GL_SRC_ALPHA;
    int mDFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;

    public MxxGLRenderer(MxxContext context) {
        mContext = context;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mShape1 = new MxxShape(mContext, "image08.jpg");
        mShape2 = new MxxShape(mContext, "image05.png");

        GLES20.glEnable(GLES20.GL_BLEND); // 打开混合
        GLES20.glDisable(GLES20.GL_DEPTH_TEST); // 关闭深度测试
    }

    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //https://blog.csdn.net/dkqiang/article/details/37729759
        //

        //GLES20.glBlendFunc(GLES20.GL_ZERO, GLES20.GL_ONE); // 基于源象素alpha通道值的半透明混合函数
        //GLES20.glBlendFunc( GLES20.GL_ONE , GLES20.GL_ZERO );        // 源色将覆盖目标色
        //GLES20.glBlendFunc( GLES20.GL_ZERO , GLES20.GL_ONE );        // 目标色将覆盖源色

        //表示把渲染的图像叠加到目标区域，也就是说源的每一个像素的alpha都等于自己的alpha，目标的每一个像素的alpha等于1。这样叠加次数越多，叠加的图元的alpha越高，得到的结果就越亮。因此这种融合用于表达光亮效果。
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE); // 基于源象素alpha通道值的半透明混合函数

        //表示把渲染的图像融合到目标区域。也就是说源的每一个像素的alpha都等于自己的alpha，目标的每一个像素的alpha等于1减去该位置源像素的alpha。 因此不论叠加多少次，亮度是不变的。
        // GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glBlendColor(1, 1, 1, 0.2f);
        GLES20.glBlendFunc(mSFactor, mDFactor);

        //GLES20.glEnable(GLES20.GL_BLEND); // 打开混合
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST); // 关闭深度测试
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mShape1.draw(mMVPMatrix);
        mShape2.draw(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // sets the viewport
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void setBlending(String id) {
        switch (id) {
            case "GL_SRC_ALPHA/GL_ONE_MINUS_SRC_ALPHA":
                mSFactor = GLES20.GL_SRC_ALPHA;
                mDFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
                break;
            case "GL_SRC_ALPHA/GL_ONE":
                Log.e("BZDB", "GL_SRC_ALPHA/GL_ONE");
                mSFactor = GLES20.GL_SRC_ALPHA;
                mDFactor = GLES20.GL_ONE;
                break;
            case "GL_ZERO/GL_ONE":
                Log.e("BZDB", "GL_ZERO/GL_ONE");
                mSFactor = GLES20.GL_ZERO;
                mDFactor = GLES20.GL_ONE;
                break;
            case "GL_ONE/GL_ZERO":
                Log.e("BZDB", "GL_ONE/GL_ZERO");
                mSFactor = GLES20.GL_ONE;
                mDFactor = GLES20.GL_ZERO;
                break;
            default:
                mSFactor = GLES20.GL_SRC_ALPHA;
                mDFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
                break;
        }
    }
}