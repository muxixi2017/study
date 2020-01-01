package com.bzsample.mxxgldemo.sample13;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxBaseFilter {
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
    private ShortBuffer mIndicesBuffer;

    private static final int COORDS_PER_TEXTURE = 2;
    private final float[] mTxtCoords = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f,
            1.0f,0.0f,
    };
    private FloatBuffer mTexCoodsBuffer;

    private int mProgram;
    private int mTextureId;
    private MxxUtils.ImageSize imageSize = new MxxUtils.ImageSize();


    //纹理位置
    private int afPosition;
    //需要渲染的纹理id
    private int imageTextureId;
    //fbo纹理id
    private int fboTextureId;
    //fbo Id
    private int fboId;

    private void createFBO() {
        Log.e("DBG", "[createFBO] START");

        //1. 创建FBO
        int[] fbos = new int[1];
        GLES30.glGenFramebuffers(1, fbos, 0);
        fboId = fbos[0];

        //2. 绑定FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);

        //3. 创建FBO纹理
        int[] textureIds = new int[1];
        //创建纹理
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e("DBG", "[glGenTextures] ERROR");
            return;
        }
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        fboTextureId = textureIds[0];

        //4. 设置纹理大小
        //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null); // OK
        //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_FLOAT, null); // FAIL
        //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA32F, 1024, 1024, 0, GLES30.GL_RGBA32F, GLES30.GL_FLOAT, null); // FAIL
        //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA32F, GLES30.GL_FLOAT, null); // FAIL
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA32F, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_FLOAT, null); // OK
        //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA32F, 1024, 1024, 0, GLES30.GL_RGBA32F, GLES30.GL_UNSIGNED_BYTE, null); // FAIL

        //5. 把纹理绑定到FBO
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fboTextureId, 0);

        //6. 检测是否绑定从成功
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("DBG", "[glFamebufferTexture2D] ERROR-2");
        }
        //7. 解绑纹理和FBO
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        Log.e("DBG", "[createFBO] END");
    }

    protected MxxContext mContext = null;

    public MxxBaseFilter(MxxContext context) {
        this.mContext = context;

//        mIndicesBuffer = MxxUtils.createShortBuffer(mIndices);
//
//        mPosCoodsBuffer = MxxUtils.createFloatBuffer(mPosCoords);
//        mTexCoodsBuffer = MxxUtils.createFloatBuffer(mTxtCoords);
//
//        mTextureId = MxxUtils.loadTexture(context, "image08.jpg", imageSize);
//
//        int vertexShader = MxxUtils.loadShader(GLES30.GL_VERTEX_SHADER, mVertexShaderCode);
//        int fragmentShader = MxxUtils.loadShader(GLES30.GL_FRAGMENT_SHADER, mFragmentShaderCode);
//
//        mProgram = GLES30.glCreateProgram();
//
//        GLES30.glAttachShader(mProgram, vertexShader);
//        GLES30.glAttachShader(mProgram, fragmentShader);
//
//        GLES30.glLinkProgram(mProgram);
//
//        createFBO();
    }

    public void draw(float[] mvpMatrix) {
        GLES30.glUseProgram(mProgram);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId); // bind texture

        int posCoordHandle = GLES30.glGetAttribLocation(mProgram, "a_PositionCoord");
        GLES30.glEnableVertexAttribArray(posCoordHandle);
        GLES30.glVertexAttribPointer(
                posCoordHandle,
                COORDS_PER_POSITION,
                GLES30.GL_FLOAT,
                false,
                COORDS_PER_POSITION * 4,
                mPosCoodsBuffer);

        int texCoordHandle = GLES30.glGetAttribLocation(mProgram, "a_TextureCoord");
        GLES30.glEnableVertexAttribArray(texCoordHandle);
        GLES30.glVertexAttribPointer(
                texCoordHandle,
                COORDS_PER_TEXTURE,
                GLES30.GL_FLOAT,
                false,
                0,
                mTexCoodsBuffer);

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        int texSamplerHandle = GLES30.glGetUniformLocation(mProgram, "u_Texture");
        GLES30.glUniform1i(texSamplerHandle, 0);

        int mSharpnessLocation = GLES30.glGetUniformLocation(mProgram, "sharpness");
        int mImageWidthFactorLocation = GLES30.glGetUniformLocation(mProgram, "imageWidthFactor");
        int mImageHeightFactorLocation = GLES30.glGetUniformLocation(mProgram, "imageHeightFactor");
        setFloat(mSharpnessLocation, 3.5f);
        setFloat(mImageWidthFactorLocation, 1.0f / imageSize.width);
        setFloat(mImageHeightFactorLocation, 1.0f / imageSize.height);

        GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, mIndices.length,
                GLES30.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES30.glDisableVertexAttribArray(posCoordHandle);
        GLES30.glDisableVertexAttribArray(texCoordHandle);
    }

    protected void setFloat(final int location, final float floatValue) {
        GLES30.glUniform1f(location, floatValue);
    }


    public void onSurfaceCreated() {

    }

    public void onDrawFrame() {


    }

    public void onSurfaceChanged(int width, int height) {

    }
}