package com.farchild.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.example.s1.R;

import java.io.InputStream;

/*
    Android-API30 需要添加下边权限
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
 */
public class RemoteResLoader {
    private final static String TAG = "RemoteResLoader";
    private String mPackageName;
    private Resources mResources;

    public RemoteResLoader(String packageName) {
        mPackageName = packageName;
        // mResources = createRemoteResourceContext(mPackageName);
    }

    public byte[] readRaw(String idName) {
        LogUtil.d(TAG, "readRawResource():" + idName);
        if (mResources == null) {
            mResources = createRemoteResourceContext(mPackageName);
            if (mResources == null) {
                return null;
            }
        }
        try {
            InputStream in = mResources.openRawResource(mResources.getIdentifier(idName, "raw", mPackageName));
            int size = in.available();
            byte [] fileBytes = new byte[size];
            int readSize = in.read(fileBytes, 0, size);
            LogUtil.d(TAG, "readRawResource():read size is " + readSize);
            return fileBytes;
        } catch (Exception e) {

        }
        return null;
    }

//    public Drawable readDrawable(String idName) {
//        LogUtil.d(TAG, "readRawResource():" + idName);
//        if (mResources == null) {
//            mResources = createRemoteResourceContext(mPackageName);
//            if (mResources == null) {
//                return null;
//            }
//        }
//        Drawable drawable = mResources.getDrawable(mResources.getIdentifier(idName, "drawable", mPackageName));
//        return drawable;
//    }

    private static Resources createRemoteResourceContext(String packageName) {
        LogUtil.d(TAG, "createRemoteResourceContext():");
        try {
            //获取apk的资源 最终都要通过AssetManager 获取,  getAssets() 获取的AssetManager是获取的本身Apk的
            //获取其他Apk的资源需要实例化一个AssetManager,并把该AssetManager的加载路径修改为被 加载的Apk的路径
            String pkgPath = AppUtil.getAppPath(packageName);
            if (TextUtils.isEmpty(pkgPath)) {
                return null;
            }
            Context context = AppUtil.getAppContext();
            AssetManager assetMAnager = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(assetMAnager, pkgPath);
            return new Resources(assetMAnager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
        } catch (Exception e) {
            LogUtil.d(TAG, "createRemoteResourceContext():ERROR:" + e.getMessage());
        }
        return null;
    }
}
