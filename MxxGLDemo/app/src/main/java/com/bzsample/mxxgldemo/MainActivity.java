package com.bzsample.mxxgldemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addItem(com.bzsample.mxxgldemo.sample01.MxxGLActivity.class, "sample01 base");
        addItem(com.bzsample.mxxgldemo.sample02.MxxGLActivity.class, "sample02 draw GL triangle"); // base sample 01
        addItem(com.bzsample.mxxgldemo.sample03.MxxGLActivity.class, "sample03 projection matrix"); // base sample 02
        addItem(com.bzsample.mxxgldemo.sample04.MxxGLActivity.class, "sample04 rotate matrix");  // base sample03
        addItem(com.bzsample.mxxgldemo.sample05.MxxGLActivity.class, "sample05 draw GL square"); // base sample03
        addItem(com.bzsample.mxxgldemo.sample06.MxxGLActivity.class, "sample06 texture"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample07.MxxGLActivity.class, "sample07 blend function"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample08.MxxGLActivity.class, "sample08 shading pixel blend"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample09.MxxGLActivity.class, "sample09 shading alpha blend transition"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample10.MxxGLActivity.class, "sample10 shading skin smooth"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample11.MxxGLActivity.class, "sample11 shading gray"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample12.MxxGLActivity.class, "sample12 shading sharpen"); // base sample05
        addItem(com.bzsample.mxxgldemo.sample13.MxxGLActivity.class, "sample13 FBO"); // base sample05
        //addItem(com.bzsample.mxxgldemo.sample06.MxxGLActivity.class, "sample12 touch event"); // base sample12
    }

    private void addItem(Class<?> cls, String title) {
        LinearLayout container = (LinearLayout)this.findViewById(R.id.container);
        Button item = new Button(this);
        item.setGravity(Gravity.LEFT);
        item.setText(title);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(item, param);

        item.setOnClickListener(new MxxOnClickListener(cls));
    }

    private class MxxOnClickListener implements View.OnClickListener {
        private Class<?> mCls = null;
        public MxxOnClickListener(Class<?> cls) {
            mCls = cls;
        }

        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, mCls);
            startActivity(intent);
        }
    }
}
