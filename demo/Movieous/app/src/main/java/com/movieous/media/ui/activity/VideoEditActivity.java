package com.movieous.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import com.movieous.media.R;
import com.movieous.media.base.BaseActivity;
import com.movieous.media.ui.fragment.VideoEditFragment;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.List;

public class VideoEditActivity extends BaseActivity {
    public static final String PATH = "Path";
    public static final int REQUEST_CODE_CHOOSE = 1;

    private VideoEditFragment mVideoEditFragment;
    private String mVideoPath;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra(PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mVideoPath = getIntent().getStringExtra(PATH);
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(mVideoPath)) {
            startVideoSelectActivity(this);
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
        if (TextUtils.isEmpty(mVideoPath)) return;
        mVideoEditFragment = VideoEditFragment.getInstance(mVideoPath);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mVideoEditFragment)
                .commit();
    }

    @Override
    public void start() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void startVideoSelectActivity(Activity activity) {
        Matisse.from(activity)
                .choose(MimeType.of(MimeType.MP4, MimeType.THREEGPP), false)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .countable(false)
                .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            final List<String> paths = Matisse.obtainPathResult(data);
            if (!paths.isEmpty()) {
                mVideoPath = paths.get(0);
                initView();
            }
        } else {
            finish();
        }
    }
}
