package com.movieous.media.ui.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.movieous.media.R;
import com.movieous.media.base.BaseActivity;
import com.movieous.media.ui.fragment.VideoRecordFragment;

public class VideoRecordActivity extends BaseActivity {
    private static final String TAG = "VideoRecordActivity";
    VideoRecordFragment mVideoRecordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            if (mVideoRecordFragment == null) {
                mVideoRecordFragment = new VideoRecordFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mVideoRecordFragment)
                    .commit();
        }
    }

    @Override
    public int layoutId() {
        return R.layout.activity_base;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initView() {
    }

    @Override
    public void start() {
    }
}
