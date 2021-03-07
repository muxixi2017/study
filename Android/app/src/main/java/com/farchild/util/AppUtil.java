package com.farchild.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;

public class AppUtil { 
    private final static String TAG = "AppUtil";

    private static Context mAppContext;

    public static void init(Context appContext) {
        if (mAppContext != null) {
            return;
        }
        mAppContext = appContext;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static void writeFileData(String filename, byte[] bytes){
        LogUtil.d(TAG, "writeFileData():");
        try {
            Context context = mAppContext;
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);//获得FileOutputStream
            fos.write(bytes);//将byte数组写入文件
            fos.close();//关闭文件输出流
            LogUtil.d(TAG, "writeFileData():END" + bytes.length);
        } catch (Exception e) {
            LogUtil.d(TAG, "writeFileData():ERR:" + e.getMessage());
        }
    }

    /*
    Android-API30 需要添加下边权限
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
 */
    public static String getAppPath(String packageName) {
        Context context = mAppContext;
        String dexPath = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            //check package
            if (applicationInfo == null) {
                LogUtil.d(TAG, "loadApkResources():E1");
                return null;
            }
            dexPath = applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //check path
        if (TextUtils.isEmpty(dexPath)) {
            LogUtil.d(TAG, "loadApkResources():E2");
            return null;
        }

        Context pluginContext = null;
        try {
            pluginContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        PackageInfo pluginPackageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
        Log.d(TAG, "loadApk: dexPath " + dexPath);
        Log.d(TAG, "loadApk: pluginPackageArchiveInfo " + pluginPackageArchiveInfo);
        return dexPath;
    }
}
