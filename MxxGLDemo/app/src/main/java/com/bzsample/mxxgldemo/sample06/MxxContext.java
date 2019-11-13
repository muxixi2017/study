package com.bzsample.mxxgldemo.sample06;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class MxxContext {
    private Context mContext;
    public MxxContext(Context context) {
        mContext = context;
    }

    public Bitmap loadImage(String fileName) {
        InputStream ins = null;
        Bitmap bitmap = null;
        try {
            ins = mContext.getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(ins);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
