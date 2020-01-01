package com.bzsample.mxxgldemo.sample13;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxUtils {
    private static final String TAG = "MxxUtils";

    public static class ImageSize {
        int width;
        int height;
    }
    public static int loadShader(int type, String shaderCode){
        // 创造顶点着色器类型(GLES30.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES30.GL_FRAGMENT_SHADER)
        //创建一个着色器
        final int shaderId = GLES30.glCreateShader(type);
        if (shaderId != 0) {
            GLES30.glShaderSource(shaderId, shaderCode);
            GLES30.glCompileShader(shaderId);
            //检测状态
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                String logInfo = GLES30.glGetShaderInfoLog(shaderId);
                System.err.println(logInfo);
                //创建失败
                GLES30.glDeleteShader(shaderId);
                return 0;
            }
            return shaderId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 编译顶点着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileVertexShader(String shaderCode) {
        return loadShader(GLES30.GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片段着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileFragmentShader(String shaderCode) {
        return loadShader(GLES30.GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 链接小程序
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);
            //链接着色器程序
            GLES30.glLinkProgram(programId);
            final int[] linkStatus = new int[1];

            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programId);
                System.err.println(logInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            }
            return programId;
        } else {
            //创建失败
            return 0;
        }
    }

    public static FloatBuffer createFloatBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);  // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        bb.order(ByteOrder.nativeOrder());  //设置字节顺序
        FloatBuffer fb = bb.asFloatBuffer();  //将字节缓冲转为浮点缓冲
        fb.put(arr); // 将坐标添加到FloatBuffer
        fb.position(0); // 设置缓冲区来读取第一个坐标
        return fb;
    }

    public static ShortBuffer createShortBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 2);  // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        bb.order(ByteOrder.nativeOrder());  //设置字节顺序
        ShortBuffer sb = bb.asShortBuffer();  //将字节缓冲转为浮点缓冲
        sb.put(arr); // 将坐标添加到FloatBuffer
        sb.position(0); // 设置缓冲区来读取第一个坐标
        return sb;
    }

    public static int loadTexture(MxxContext context, String fileName, ImageSize size) {
        Bitmap bitmap = context.loadImage(fileName);
        if (size != null) {
            size.width = bitmap.getWidth();
            size.height = bitmap.getHeight();
        }
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);//生成一个纹理

        int textureId = textures[0];
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId); // bind texture
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        //上面是纹理贴图的取样方式，包括拉伸方式，取临近值和线性值
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);//让图片和纹理关联起来，加载到OpenGl空间中
        bitmap.recycle();//不需要，可以释放
        return textureId;
    }

    public static int loadTexture(MxxContext context, int resourceId) {
        Bitmap bitmap = context.loadImage(resourceId);
        if (bitmap == null) {
            return 0;
        }

        final int[] textureIds = new int[1];
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            MxxLogUtils.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }

        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureIds[0];
    }
}
