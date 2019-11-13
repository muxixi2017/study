package com.bzsample.mxxgldemo.sample04;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class MxxShape {
    private static final String mVertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec4 a_Color;" +
            "varying  vec4 v_Color;" +
            "void main() {" +
            "  gl_Position = u_MVPMatrix * a_Position;" +
            "  v_Color = a_Color;" +
            "}";

    private static final String mFragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 v_Color;" +
            "void main() {" +
            "  gl_FragColor = v_Color;" +
            "}";

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_POSITION = 3;
    private static final float mPosCoords[] = {
               0.0f,   0.5f, 0.0f, // top
            -0.433f, -0.25f, 0.0f, // bottom left
             0.433f, -0.25f, 0.0f  // bottom right
    };
    private FloatBuffer mPosCoodsBuffer;

    private static final int COORDS_PER_COLOR = 4;
    private static final float mColorCoods[] = new float[] {
            1, 0, 0, 1.0f,
            0, 1, 0, 1.0f,
            0, 0, 1, 1.0f
    };
    private FloatBuffer mColorCoodsBuffer;

    private final int mProgram;

    public MxxShape(MxxContext context) {
        mPosCoodsBuffer = MxxUtils.createFloatBuffer(mPosCoords);
        mColorCoodsBuffer = MxxUtils.createFloatBuffer(mColorCoods);

        int vertexShader = MxxUtils.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = MxxUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);


        GLES20.glUseProgram(mProgram);

        int posCoodsHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        int colorCoodsHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");

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
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");
    }

    int mMVPMatrixHandle;

    public void draw(float[] mvpMatrix) {

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawArrays(
                GLES20.GL_TRIANGLES,
                0,
                mPosCoords.length / COORDS_PER_POSITION);

        //GLES20.glDisableVertexAttribArray(posCoodsHandle);
        //GLES20.glDisableVertexAttribArray(colorCoodsHandle);
    }
}