package com.bzsample.mxxgldemo.sample13;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.bzsample.mxxgldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MxxFluidFilter extends MxxBaseFilter {
    private static final String TAG = "MxxGrayFilter";

    /**
     * 矩阵
     */
    private float[] mMatrix = new float[16];

    public MxxFluidFilter(MxxContext context) {
        super(context);
    }

    public void onSurfaceCreated() {
        MxxLogUtils.d(TAG, "[onSurfaceCreated]");
        //设置背景颜色
        //GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //初始化程序对象
        //setupProgram();
        doInit();
    }

    int prog_force, prog_advec, prog_div, prog_p, prog_Source, prog_show;
    int sampLoc;
    float timer, delay = 0, it = 10, frames = 0, time, animation;
    int n = 512;
    int  FBO, FBO1, texture, texture1, texture2;

    private void doInit() {
//        try { ext = gl.getExtension("OES_texture_float")
////        } catch(e) {}
////        if ( !ext ) {alert(err + "OES_texture_float extension"); return}

//        prog_force  = gl.createProgram()
//        gl.attachShader(prog_force, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_force, getShader( gl, "force-fs" ))
//        gl.linkProgram(prog_force)
//
//        gl.useProgram(prog_force)
//        gl.uniform1f(gl.getUniformLocation(prog_force, "c"), .001*.5*10 )
//        gl.uniform1i(gl.getUniformLocation(prog_force, "samp"), 1)

        prog_force = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_force_fs);

        GLES30.glUseProgram(prog_force);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(prog_force, "c"), (float)(.001*.5*10) );
        GLES30.glUniform1i(GLES30.glGetUniformLocation(prog_force, "samp"), 1);


//        prog_advec  = gl.createProgram()
//        gl.attachShader(prog_advec, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_advec, getShader( gl, "advec-fs" ))
//        gl.linkProgram(prog_advec)

        prog_advec = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_advec_fs);

//        prog_div  = gl.createProgram()
//        gl.attachShader(prog_div, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_div, getShader( gl, "div-fs" ))
//        gl.linkProgram(prog_div)
//
//        gl.useProgram(prog_div)
//        gl.uniform1i(gl.getUniformLocation(prog_div, "samp"), 1)

        prog_div = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_div_fs);
        GLES30.glUseProgram(prog_div);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(prog_div, "samp"), 1);

//        prog_p  = gl.createProgram()
//        gl.attachShader(prog_p, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_p, getShader( gl, "p-fs" ))
//        gl.linkProgram(prog_p)
//
//        gl.useProgram(prog_p)
//        sampLoc  = gl.getUniformLocation(prog_p, "samp")

        prog_p = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_p_fs);
        GLES30.glUseProgram(prog_p);
        sampLoc  = GLES30.glGetUniformLocation(prog_p, "samp");


//        prog_Source  = gl.createProgram()
//        gl.attachShader(prog_Source, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_Source, getShader( gl, "Source-fs" ))
//        gl.linkProgram(prog_Source)
//        gl.useProgram(prog_Source)
//        gl.uniform1i(gl.getUniformLocation(prog_Source, "samp2"), 2)

        prog_Source = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_source_fs);
        GLES30.glUseProgram(prog_Source);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(prog_Source, "samp2"), 2);

//        prog_show  = gl.createProgram()
//        gl.attachShader(prog_show, getShader( gl, "shader-vs" ))
//        gl.attachShader(prog_show, getShader( gl, "shader-fs-show" ))
//        gl.linkProgram(prog_show)

        prog_show = doCreateProgram(R.raw.fluid_shader_vs, R.raw.fluid_shader_fs_show);

//        gl.useProgram(prog_advec)
//        var aPosLoc = gl.getAttribLocation(prog_advec, "aPos")
//        var aTexLoc = gl.getAttribLocation(prog_advec, "aTexCoord")
//        gl.enableVertexAttribArray( aPosLoc )
//        gl.enableVertexAttribArray( aTexLoc )
        GLES30.glUseProgram(prog_advec);
        int aPosLoc = GLES30.glGetAttribLocation(prog_advec, "aPos");
        int aTexLoc = GLES30.glGetAttribLocation(prog_advec, "aTexCoord");
        GLES30.glEnableVertexAttribArray( aPosLoc );
        GLES30.glEnableVertexAttribArray( aTexLoc );

//        var data = new Float32Array([-1,-1, 0,0,  1,-1, 1,0,  -1,1, 0,1,
//                1,1, 1,1])
//        gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer())
//        gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW)
//        gl.vertexAttribPointer(aPosLoc, 2, gl.FLOAT, gl.FALSE, 16, 0)
//        gl.vertexAttribPointer(aTexLoc, 2, gl.FLOAT, gl.FALSE, 16, 8)

        float[] data = new float[] {-1,-1, 0,0,  1,-1, 1,0,  -1,1, 0,1,
                1,1, 1,1};
        int[] vboIds = new int[1];
        GLES30.glGenBuffers(1, vboIds, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, data.length*4, Float32Array.create(data), GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(aPosLoc, 2, GLES30.GL_FLOAT, false, 16, 0);
        GLES30.glVertexAttribPointer(aTexLoc, 2, GLES30.GL_FLOAT, false, 16, 8);

//        var pixels = [],  h = 2/n, T
//        for(var i = 0; i<n; i++)
//            for(var j = 0; j<n; j++){
//                T = 0
//                if (j>155 && j<356){
//                    if (i>105 && i<155) T=.005
//                    else if (i>356 && i<406) T=.005
//                }
//                pixels.push( 0, 0, T, 0 )
//            }
//        texture2 = gl.createTexture()
//        gl.activeTexture(gl.TEXTURE2)
//        gl.bindTexture(gl.TEXTURE_2D, texture2)
//        gl.pixelStorei(gl.UNPACK_ALIGNMENT, 1)
//        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, n, n, 0,
//                gl.RGBA, gl.FLOAT, new Float32Array(pixels))
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)

        float[] pixels = new float [n*n*4];  int h = 2/n; float T;  int count = 0;
        for(int i = 0; i<n; i++) {
            for (int j = 0; j < n; j++) {
                T = 0f;
                if (j > 155 && j < 356) {
                    if (i > 105 && i < 155)
                        T = .005f;
                    else if (i > 356 && i < 406)
                        T = -.005f;
                }
                pixels[count++] = 0;
                pixels[count++] = 0;
                pixels[count++] = T;
                pixels[count++] = 0;
            }
        }
        texture2 = doCreateTexture(GLES30.GL_TEXTURE2, pixels);

//        var pixels = []
//        for(var i = 0; i<n; i++)
//            for(var j = 0; j<n; j++){
//                T = 0
//                if (i>205 && i<306){
//                    if (j>105 && j<245) T=-1
//                    else if (j>266 && j<406) T=1
//                }
//                pixels.push( 0, 0, T, 0 )
//            }
//        texture = gl.createTexture()
//        gl.activeTexture(gl.TEXTURE0)
//        gl.bindTexture(gl.TEXTURE_2D, texture)
//        gl.pixelStorei(gl.UNPACK_ALIGNMENT, 1)
//        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, n, n, 0,
//                gl.RGBA, gl.FLOAT, new Float32Array(pixels))
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
//
//        texture1 = gl.createTexture()
//        gl.activeTexture(gl.TEXTURE1)
//        gl.bindTexture(gl.TEXTURE_2D, texture1)
//        gl.pixelStorei(gl.UNPACK_ALIGNMENT, 1)
//        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, n, n, 0,
//                gl.RGBA, gl.FLOAT, new Float32Array(pixels))
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
//        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)

        pixels = new float [n*n*4]; count = 0;
        for(int i = 0; i<n; i++) {
            for (int j = 0; j < n; j++) {
                T = 0;
                if (i > 205 && i < 306) {
                    if (j > 105 && j < 245)
                        T = -1;
                    else if (j > 266 && j < 406)
                        T = 1;
                }
                pixels[count++] = 0;
                pixels[count++] = 0;
                pixels[count++] = T;
                pixels[count++] = 0;
            }
        }
        texture = doCreateTexture(GLES30.GL_TEXTURE0,pixels);
        texture1 = doCreateTexture(GLES30.GL_TEXTURE1,pixels);

//        FBO = gl.createFramebuffer()
//        gl.bindFramebuffer(gl.FRAMEBUFFER, FBO)
//        gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0,
//                gl.TEXTURE_2D, texture, 0)
//        FBO1 = gl.createFramebuffer()
//        gl.bindFramebuffer(gl.FRAMEBUFFER, FBO1)
//        gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0,
//                gl.TEXTURE_2D, texture1, 0)
//        if( gl.checkFramebufferStatus(gl.FRAMEBUFFER) != gl.FRAMEBUFFER_COMPLETE)
//            alert(err + "FLOAT as the color attachment to an FBO")
        FBO = doCreateFBO(texture);
        FBO1 = doCreateFBO(texture1);

        if( GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            MxxLogUtils.e(TAG, "FLOAT as the color attachment to an FBO");
        }
    }

    private int doCreateProgram(int vertShaderId, int fragShaderId) {
        String vertexShader = mContext.readShaderCodeFromResource(vertShaderId);
        String fragmentShader = mContext.readShaderCodeFromResource(fragShaderId);

        final int vertexShaderId = MxxUtils.compileVertexShader(vertexShader);
        final int fragmentShaderId = MxxUtils.compileFragmentShader(fragmentShader);

        int program = MxxUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return program;
    }

    private int doCreateTexture(int activeTexture, float[] pixels) {
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0); // 生成一个纹理

        int textureId = textures[0];
        GLES30.glActiveTexture(activeTexture);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId); // bind texture
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA32F, n, n, 0,
                GLES30.GL_RGBA, GLES30.GL_FLOAT, Float32Array.create(pixels));
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST);
        return textureId;
    }

    private int doCreateFBO(int fboTextureId) {
        //1. 创建FBO
        int[] fbos = new int[1];
        GLES30.glGenFramebuffers(1, fbos, 0);
        int fboId = fbos[0];

        //2. 绑定FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);

        //5. 把纹理绑定到FBO
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fboTextureId, 0);

        return fboId;
    }

    static class Float32Array {
            public static FloatBuffer create(float[] vertexPoints) {
                FloatBuffer  vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                vertexBuffer.put(vertexPoints);
                vertexBuffer.position(0);
                return vertexBuffer;
            }
    }

    private void doDraw() {
        GLES30.glViewport(0, 0, mWidth, mHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO1);
        GLES30.glUseProgram(prog_Source);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO);
        GLES30.glUseProgram(prog_force);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO1);
        GLES30.glUseProgram(prog_advec);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO);
        GLES30.glUseProgram(prog_advec);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glUseProgram(prog_p);
        for(int i = 0; i < it; i++) {
            GLES30.glUniform1i(sampLoc, 1);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

            GLES30.glUniform1i(sampLoc, 0);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO1);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        }

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FBO);
        GLES30.glUseProgram(prog_div);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glUseProgram(prog_show);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
    }

    int mWidth = 512, mHeight = 512;

    public void onSurfaceChanged(int width, int height) {
        MxxLogUtils.d(TAG, "[onSurfaceChanged]");
        mWidth = width;
        mHeight = height;
        GLES30.glViewport(0, 0, mWidth, mHeight);

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
        //MxxLogUtils.d(TAG, "[onDrawFrame]");
        doDraw();
    }
}
