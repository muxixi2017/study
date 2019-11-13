package com.bzsample.mxxgldemo.sample05;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxShape {
    private static final String mVertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying  vec4 fColor;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  fColor = vColor;" +
            "}";

    private static final String mFragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 fColor;" +
            "void main() {" +
            "  gl_FragColor = fColor;" +
            "}";

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_POSITION = 3;
    private static final float[] mPosCoords = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f    // top right
    };
    private FloatBuffer mPosCoodsBuffer;

    private final short mIndices[] = { 0, 2, 1, 0, 3, 2 }; // order to draw vertices
    private final ShortBuffer mIndicesBuffer;

    private static final int COORDS_PER_COLOR = 4;
    private static final float mColorCoods[] = new float[] {
            1, 0, 0, 1.0f,
            0, 1, 0, 1.0f,
            0, 0, 1, 1.0f,
            1, 1, 1, 1.0f,
    };
    private FloatBuffer mColorCoodsBuffer;

    private final int mProgram;

    //private int mPositionHandle;
    //private int mColorHandle;

    public MxxShape(MxxContext context) {
        mIndicesBuffer = MxxUtils.createShortBuffer(mIndices);

        mPosCoodsBuffer = MxxUtils.createFloatBuffer(mPosCoords);
        mColorCoodsBuffer = MxxUtils.createFloatBuffer(mColorCoods);

        int vertexShader = MxxUtils.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = MxxUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        int posCoodsHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int colorCoodsHandle = GLES20.glGetAttribLocation(mProgram, "vColor");

        GLES20.glEnableVertexAttribArray(posCoodsHandle);

        GLES20.glVertexAttribPointer(
                posCoodsHandle,
                COORDS_PER_POSITION,
                GLES20.GL_FLOAT,
                false,
                COORDS_PER_POSITION * 4,
                mPosCoodsBuffer);

        GLES20.glEnableVertexAttribArray(colorCoodsHandle);

        GLES20.glVertexAttribPointer(
                colorCoodsHandle,
                COORDS_PER_COLOR,
                GLES20.GL_FLOAT,
                false,
                COORDS_PER_COLOR * 4,
                mColorCoodsBuffer);

        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, mIndices.length,
                GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES20.glDisableVertexAttribArray(posCoodsHandle);
        GLES20.glDisableVertexAttribArray(colorCoodsHandle);
    }
}