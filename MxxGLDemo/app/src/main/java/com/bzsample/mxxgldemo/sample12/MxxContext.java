package com.bzsample.mxxgldemo.sample12;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public String readShaderCodeFromResource(int resourceId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = mContext.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: "
                    + resourceId, nfe);
        }
        return body.toString();
    }
}
