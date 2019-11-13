package com.bzsample.mxxgldemo.sample02;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxUtils {
    public static int loadShader(int type, String shaderCode){
        // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // 添加上面编写的着色器代码并编译它
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
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
}
