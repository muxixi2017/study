package com.bzsample.mxxgldemo.sample11;

import android.opengl.GLES20;

import com.bzsample.mxxgldemo.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
                    "varying vec2 v_TextureCoord;" +
                    "void main() {" +
                    "  vec4 texture = texture2D(u_Texture, v_TextureCoord);" +
                    "  float luminance = 0.299*texture.r+0.587*texture.g+0.114*texture.b;" +
                    "  gl_FragColor = vec4(luminance,luminance,luminance,1);" +
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

        int vertexShader = MxxUtils.loadShader(GLES20.GL_VERTEX_SHADER, context.readShaderCodeFromResource(R.raw.vertex_shader_11));
        int fragmentShader = MxxUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, context.readShaderCodeFromResource(R.raw.fragment_shader_11));

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

        mParamsLocation = GLES20.glGetUniformLocation(mProgram, "u_type");

        setTexelSize(imageSize.width, imageSize.height);
        setBeautyLevel(beautyLevel);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, mIndices.length,
                GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES20.glDisableVertexAttribArray(posCoordHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    public static int beautyLevel = 5;

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloat(final int location, final float floatValue) {
        GLES20.glUniform1f(location, floatValue);
    }


    public void setBeautyLevel(int level){
        switch (level) {
            case 1:
                setFloat(mParamsLocation, 1.0f);
                //setSmoothOpacity(1.0f);
                break;
            case 2:
                setFloat(mParamsLocation, 0.8f);
                //setSmoothOpacity(0.8f);
                break;
            case 3:
                setFloat(mParamsLocation,0.6f);
                //setSmoothOpacity(0.6f);
                break;
            case 4:
                setFloat(mParamsLocation, 0.4f);
                //setSmoothOpacity(0.4f);
                break;
            case 5:
                setFloat(mParamsLocation,0.2f);
                //setSmoothOpacity(0.2f);
                break;
            default:
                break;
        }
    }

    /**
     * 设置磨皮程度
     * @param percent 百分比
     */
    public void setSmoothOpacity(float percent) {
        float opacity;
        if (percent <= 0) {
            opacity = 0.0f;
        } else {
            opacity = calculateOpacity(percent);
        }
        setFloat(mParamsLocation, opacity);
    }

    /**
     * 根据百分比计算出实际的磨皮程度
     * @param percent
     * @return
     */
    private float calculateOpacity(float percent) {
        float result = 0.0f;

        // TODO 可以加入分段函数，对不同等级的磨皮进行不一样的处理
        result = (float) (1.0f - (1.0f - percent + 0.02) / 2.0f);

        return result;
    }


}