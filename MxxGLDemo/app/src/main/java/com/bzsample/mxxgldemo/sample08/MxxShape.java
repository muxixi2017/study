package com.bzsample.mxxgldemo.sample08;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//多纹理参考: https://blog.csdn.net/huawenguang/article/details/41245871

public class MxxShape {
    private static final String mVertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_PositionCoord;" +
                    "attribute vec2 a_TextureCoord;" +
                    "varying  vec2 v_TextureCoord;" +
                    "void main() {" +
                    "  v_TextureCoord = a_TextureCoord;" +
                    "  gl_Position = u_MVPMatrix * a_PositionCoord;" +
                    "}";

    private static final String mFragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D u_Texture;" +
                    "uniform sampler2D u_Texture2;" +
                    "varying vec2 v_TextureCoord;" +
                    "void main() {" +
                    "  lowp vec4 l_Color1 = texture2D(u_Texture, v_TextureCoord);" +
                    "  lowp vec4 l_Color2 = texture2D(u_Texture2, v_TextureCoord);" +
                  // (1) YUV to RGB

                  // (2) 2image alpha
                    "    gl_FragColor = (1.0 - l_Color2.a) * l_Color1 + l_Color2 * l_Color2.a;" +
                  // (3) gray
                 //   "  float gray = dot(v_Color1.rgb, vec3(0.3, 0.2, 0.2));" +
                 //   "  gl_FragColor = vec4(gray, gray, gray, v_Color1.a);" +
                 //
                 // (4) special area display. according color2 alpha value display color1
                 //   "  if (l_Color2.a == 1.0f)" +
                 //   "    gl_FragColor = vec4(l_Color1.r, l_Color1.g, l_Color1.b, 0.5);" +
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
    private int mTextureId2;

    public MxxShape(MxxContext context, String fileName, String fileName2) {
        mIndicesBuffer = MxxUtils.createShortBuffer(mIndices);

        mPosCoodsBuffer = MxxUtils.createFloatBuffer(mPosCoords);
        mTexCoodsBuffer = MxxUtils.createFloatBuffer(mTxtCoords);

        mTextureId = MxxUtils.loadTexture(context, fileName);
        mTextureId2 = MxxUtils.loadTexture1(context, fileName2);

        int vertexShader = MxxUtils.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = MxxUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

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

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId); // bind texture
        int texSamplerHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1i(texSamplerHandle, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId2); // bind texture
        int texSamplerHandle2 = GLES20.glGetUniformLocation(mProgram, "u_Texture2");
        GLES20.glUniform1i(texSamplerHandle2, 1);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, mIndices.length,
                GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES20.glDisableVertexAttribArray(posCoordHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }
}