package com.bzsample.mxxgldemo.sample04;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MxxGLActivity extends AppCompatActivity {

    private MxxGLSurfaceView mSurfaceView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new MxxGLSurfaceView(this);
        setContentView(mSurfaceView);
    }
}