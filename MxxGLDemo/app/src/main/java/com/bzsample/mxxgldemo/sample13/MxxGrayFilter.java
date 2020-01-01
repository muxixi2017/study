package com.bzsample.mxxgldemo.sample13;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.bzsample.mxxgldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxGrayFilter extends MxxBaseFilter {
    private static final String TAG = "MxxGrayFilter";

    private FloatBuffer vertexBuffer, mTexVertexBuffer;

    private ShortBuffer mVertexIndexBuffer;

    protected int mProgram;

    private int textureId;

    /**
     * 顶点坐标
     * (x,y,z)
     */
    private float[] POSITION_VERTEX = new float[]{
            0f, 0f, 0f,     //顶点坐标V0
            1f, 1f, 0f,     //顶点坐标V1
            -1f, 1f, 0f,    //顶点坐标V2
            -1f, -1f, 0f,   //顶点坐标V3
            1f, -1f, 0f     //顶点坐标V4
    };

    /**
     * 纹理坐标
     * (s,t)
     */
    private static final float[] TEX_VERTEX = {
            0.5f, 0.5f, //纹理坐标V0
            1f, 0f,     //纹理坐标V1
            0f, 0f,     //纹理坐标V2
            0f, 1.0f,   //纹理坐标V3
            1f, 1.0f    //纹理坐标V4
    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
            0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
            0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
            0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    };

    private int uMatrixLocation;

    /**
     * 矩阵
     */
    private float[] mMatrix = new float[16];

    /**
     * 顶点着色器
     */
    private String mVertexShader;

    /**
     * 片段着色器
     */
    private String mFragmentShader;

    private int aFilterLocation;

    private float[] filterValue = new float[]{0.299f, 0.587f, 0.114f};


    public MxxGrayFilter(MxxContext context) {
        super(context);
        mVertexShader = context.readShaderCodeFromResource(R.raw.gray_filter_vertex_shader);
        mFragmentShader = context.readShaderCodeFromResource(R.raw.gray_filter_fragment_shader);
        setupBuffer();
    }

    private void setupBuffer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(POSITION_VERTEX);
        vertexBuffer.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }

    public void onSurfaceCreated() {
        MxxLogUtils.d(TAG, "[onSurfaceCreated]");
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //初始化程序对象
        setupProgram();
    }

    public void setupProgram() {
        //编译着色器
        final int vertexShaderId = MxxUtils.compileVertexShader(mVertexShader);
        final int fragmentShaderId = MxxUtils.compileFragmentShader(mFragmentShader);
        //链接程序片段
        mProgram = MxxUtils.linkProgram(vertexShaderId, fragmentShaderId);
        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        //加载纹理
        textureId = MxxUtils.loadTexture(mContext, "image01.jpg", null);

        aFilterLocation = GLES30.glGetUniformLocation(mProgram, "a_Filter");

        MxxLogUtils.d(TAG, "program=%d matrixLocation=%d textureId=%d", mProgram, uMatrixLocation, textureId);
    }

    public void onSurfaceChanged(int width, int height) {
        MxxLogUtils.d(TAG, "[onSurfaceChanged]");
        GLES30.glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    public void onDrawFrame() {
        MxxLogUtils.d(TAG, "[onDrawFrame]");
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //使用程序片段
        GLES30.glUseProgram(mProgram);

        //更新参数
        GLES30.glUniform3fv(aFilterLocation, 1, filterValue, 0);

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTexVertexBuffer);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        // 绘制
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, VERTEX_INDEX.length, GLES30.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
    }


}
