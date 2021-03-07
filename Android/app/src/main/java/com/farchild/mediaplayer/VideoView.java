package com.farchild.mediaplayer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.example.s1.R;
import com.farchild.util.LogUtil;
import com.farchild.util.AppUtil;
import com.farchild.util.RemoteResLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;

public class VideoView extends SurfaceView {

    private final static String TAG = "VideoView";
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private RemoteResLoader mResLoader;


    public VideoView(Context context) {
        super(context, null);
        mContext = context;
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs , 0);
        mContext = context;
    }

    public void create() {
        LogUtil.d(TAG, "create()");
        mSurfaceHolder = this.getHolder();//获取surfaceHolder
        mMediaPlayer = new MediaPlayer();//创建MediaPlayer对象
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置多媒体类型
        mResLoader = new RemoteResLoader("com.example.s2");
    }

    public void destroy() {
        LogUtil.d(TAG, "destroy()");
        if(mMediaPlayer!=null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
    }

    public void play(){
        LogUtil.d(TAG, "play():HIT1");
        mMediaPlayer.reset(); //重置MediaPlayer
        mMediaPlayer.setDisplay(mSurfaceHolder);//把视频画面输出到SurfaceView中

        try {
            //mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory()+"/a.mp4");//设置要播放的内容在根目录下的位置
            //mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse("android.resource://com.example.s1/" + R.raw.mp4_sample));//设置要播放的内容在根目录下的位置
            //mMediaPlayer.setDataSource("https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4");
            //mMediaPlayer.setDataSource("https://media.w3.org/2010/05/sintel/trailer.mp4");
            //mMediaPlayer.setDataSource(new MyMediaDataSource(mResLoader, "mp4_sample"));

            byte[] mMediaBytes = mResLoader.readRaw("mp4_sample");
            AppUtil.writeFileData("mp4_sample4.mp4", mMediaBytes);
            mMediaPlayer.setDataSource("file:///data/data/" + "com.example.s1" + "/files/mp4_sample4.mp4");

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.d(TAG, "play():HIT2");
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync();//预加载
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class MyMediaDataSource extends MediaDataSource {
        private int mSize;
        byte[]  mMediaBytes;
        private Context mContext;
        private RemoteResLoader mLoader;

        public MyMediaDataSource(RemoteResLoader loader, String filePath) {
            LogUtil.d(TAG, "MyMediaDataSource():START");
            mLoader = loader;
            mMediaBytes = mLoader.readRaw(filePath);
            writeFileData("aaaa", mMediaBytes);
            mSize = mMediaBytes.length;


            LogUtil.d(TAG, "MyMediaDataSource():END:" + mSize);
        }

        public void writeFileData(String filename, byte[]  bytes){
            LogUtil.d(TAG, "writeFileData():");
            try {
                Context context = AppUtil.getAppContext();
                FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);//获得FileOutputStream
                fos.write(bytes);//将byte数组写入文件
                fos.close();//关闭文件输出流
                LogUtil.d(TAG, "writeFileData():END" + bytes.length);
            } catch (Exception e) {
                LogUtil.d(TAG, "writeFileData():ERR:" + e.getMessage());
            }
        }

        @Override
        public long getSize() {
            return mSize;
        }

        @Override
        public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
            if(position + 1 >= mMediaBytes.length){
                return -1;
            }

            int length;
            if(position + size < mMediaBytes.length){
                length = size;
            }else{
                length = (int) (mMediaBytes.length - position);
                if(length > buffer.length)
                    length = buffer.length ;
                length--;
            }

            System.arraycopy(mMediaBytes, (int) position, buffer, offset, length);
            return length;
        }

        @Override
        public void close() throws IOException {
            LogUtil.d(TAG, "close():");
            mMediaBytes = null;
        }
    }
}
