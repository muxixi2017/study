package com.bzsample.mxxgldemo.sample12;

import android.opengl.GLES20;

import com.bzsample.mxxgldemo.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxShape {
    private static final String mVertexShaderCode =
            "uniform mat4 u_MVPMatrix;\n" +
            "attribute vec4 a_PositionCoord;\n" +
                    "attribute vec2 a_TextureCoord;\n" +
                    "\n" +
                    "uniform float imageWidthFactor; \n" +
                    "uniform float imageHeightFactor; \n" +
                    "uniform float sharpness;\n" +
                    "\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying vec2 leftTextureCoordinate;\n" +
                    "varying vec2 rightTextureCoordinate; \n" +
                    "varying vec2 topTextureCoordinate;\n" +
                    "varying vec2 bottomTextureCoordinate;\n" +
                    "\n" +
                    "varying float centerMultiplier;\n" +
                    "varying float edgeMultiplier;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_Position = u_MVPMatrix * a_PositionCoord;\n" +
                    "    \n" +
                    "    mediump vec2 widthStep = vec2(imageWidthFactor, 0.0);\n" +
                    "    mediump vec2 heightStep = vec2(0.0, imageHeightFactor);\n" +
                    "    \n" +
                    "    textureCoordinate = a_TextureCoord;\n" +
                    "    leftTextureCoordinate = a_TextureCoord - widthStep;\n" +
                    "    rightTextureCoordinate = a_TextureCoord + widthStep;\n" +
                    "    topTextureCoordinate = a_TextureCoord + heightStep;     \n" +
                    "    bottomTextureCoordinate = a_TextureCoord - heightStep;\n" +
                    "    \n" +
                    "    centerMultiplier = 1.0 + 4.0 * sharpness;\n" +
                    "    edgeMultiplier = sharpness;\n" +
                    "}";

    private static final String mFragmentShaderCode =
            "precision highp float;\n" +
                    "\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 leftTextureCoordinate;\n" +
                    "varying highp vec2 rightTextureCoordinate; \n" +
                    "varying highp vec2 topTextureCoordinate;\n" +
                    "varying highp vec2 bottomTextureCoordinate;\n" +
                    "\n" +
                    "varying highp float centerMultiplier;\n" +
                    "varying highp float edgeMultiplier;\n" +
                    "\n" +
                    "uniform sampler2D u_Texture;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    mediump vec3 textureColor = texture2D(u_Texture, textureCoordinate).rgb;\n" +
                    "    mediump vec3 leftTextureColor = texture2D(u_Texture, leftTextureCoordinate).rgb;\n" +
                    "    mediump vec3 rightTextureColor = texture2D(u_Texture, rightTextureCoordinate).rgb;\n" +
                    "    mediump vec3 topTextureColor = texture2D(u_Texture, topTextureCoordinate).rgb;\n" +
                    "    mediump vec3 bottomTextureColor = texture2D(u_Texture, bottomTextureCoordinate).rgb;\n" +
                    "\n" +
                    "    gl_FragColor = vec4((textureColor * centerMultiplier - (leftTextureColor * edgeMultiplier + rightTextureColor * edgeMultiplier + topTextureColor * edgeMultiplier + bottomTextureColor * edgeMultiplier)), texture2D(u_Texture, bottomTextureCoordinate).w);\n" +
                    "}";


    private int mSingleStepOffsetLocation;
    private int mParamsLocation;

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

    private static final int COORDS_PER_TEXTURE = 2;
    private final float[] mTxtCoords = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f,
            1.0f,0.0f,
    };
    private FloatBuffer mTexCoodsBuffer;

    private final int mProgram;
    private int mTextureId;
    private MxxUtils.ImageSize imageSize = new MxxUtils.ImageSize();

    public MxxShape(MxxContext context) {
        mIndicesBuffer = MxxUtils.createShortBuffer(mIndices);

        mPosCoodsBuffer = MxxUtils.createFloatBuffer(mPosCoords);
        mTexCoodsBuffer = MxxUtils.createFloatBuffer(mTxtCoords);

        mTextureId = MxxUtils.loadTexture(context, "image08.jpg", imageSize);

        int vertexShader = MxxUtils.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = MxxUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId); // bind texture

        int posCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_PositionCoord");
        GLES20.glEnableVertexAttribArray(posCoordHandle);
        GLES20.glVertexAttribPointer(
                posCoordHandle,
                COORDS_PER_POSITION,
                GLES20.GL_FLOAT,
                false,
                COORDS_PER_POSITION * 4,
                mPosCoodsBuffer);

        int texCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TextureCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(
                texCoordHandle,
                COORDS_PER_TEXTURE,
                GLES20.GL_FLOAT,
                false,
                0,
                mTexCoodsBuffer);

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        int texSamplerHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1i(texSamplerHandle, 0);

        int mSharpnessLocation = GLES20.glGetUniformLocation(mProgram, "sharpness");
        int mImageWidthFactorLocation = GLES20.glGetUniformLocation(mProgram, "imageWidthFactor");
        int mImageHeightFactorLocation = GLES20.glGetUniformLocation(mProgram, "imageHeightFactor");
        setFloat(mSharpnessLocation, 3.5f);
        setFloat(mImageWidthFactorLocation, 1.0f / imageSize.width);
        setFloat(mImageHeightFactorLocation, 1.0f / imageSize.height);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, mIndices.length,
                GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES20.glDisableVertexAttribArray(posCoordHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    protected void setFloat(final int location, final float floatValue) {
        GLES20.glUniform1f(location, floatValue);
    }
}