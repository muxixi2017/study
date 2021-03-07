package com.farchild.mediaplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.s1.R;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mContainer;
    private VideoView mVideoView;
    private Button mPlayerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContainer = new FrameLayout(this);

        mVideoView = new VideoView(this);
        mContainer.addView(mVideoView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mPlayerButton = new Button(this);
        mPlayerButton.setText("play");
        mContainer.addView(mPlayerButton, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        setContentView(mContainer);

        mPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.play();
            }
        });

        mVideoView.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mVideoView.destroy();
        super.onDestroy();
    }
}