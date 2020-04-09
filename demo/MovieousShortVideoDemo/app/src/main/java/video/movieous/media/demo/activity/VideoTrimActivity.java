package video.movieous.media.demo.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import video.movieous.engine.UAVOptions;
import video.movieous.engine.core.env.FitViewHelper;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.media.listener.UVideoSaveListener;
import video.movieous.media.model.UMediaTime;
import video.movieous.shortvideo.UMediaUtil;
import video.movieous.shortvideo.UVideoEditManager;

/**
 * VideoTrimActivity
 */
public class VideoTrimActivity extends BaseEditActivity implements UVideoSaveListener {
    private static final String TAG = "VideoTrimActivity";

    private static final int REQUEST_CODE_CHOOSE = 1;

    private UTextureView mRenderView;
    private TextView mTvTip;
    private TextView mFileTip;
    private EditText mStTrimTime;
    private EditText mEtTrimTime;
    private Button mBtnSave;

    private UVideoEditManager mVideoEditManager;
    private long mStartTime;
    private long mDuration;
    private UAVOptions mAVOptions;

    // 剪辑文件保存路径
    private String mOutFile = "/sdcard/movieous/shortvideo/clip_test.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startFileSelectActivity(this, false, 1);
    }

    @Override
    protected void getFiles(List<String> inFile) {
        mInputFile = inFile.get(0);
        mFileTip.setText(mInputFile);
        mDuration = UMediaUtil.getMetadata(mInputFile).duration;
        //mEtTrimTime.setText(Long.toString(mDuration));
        mVideoEditManager.init(mRenderView, mInputFile)
                .setVideoFrameListener(this)
                .start();
    }

    @Override
    public void onVideoSaveProgress(float progress) {
        runOnUiThread(() -> mTvTip.setText(String.format("%.1f", progress * 100) + "%"));
    }

    @Override
    public void onVideoSaveSuccess(String outFile) {
        long cost = (System.currentTimeMillis() - mStartTime) / 1000;
        Log.i(TAG, "transcode ok, cost: " + cost + "s, file: " + outFile);
        runOnUiThread(() -> {
            mTvTip.setText("cost: " + cost + "s");
            mFileTip.setText(outFile);
        });
    }

    @Override
    public void onVideoSaveFail(int errorCode) {
        Log.i(TAG, "onVideoSaveFail");
        runOnUiThread(() -> {
            mTvTip.setText("video save failed!");
        });
    }

    private void initView() {
        setContentView(R.layout.activity_video_trim);
        mRenderView = $(R.id.render_view);
        mTvTip = $(R.id.tv_tip);
        mFileTip = $(R.id.file_tip);
        mStTrimTime = $(R.id.st_trim_time);
        mEtTrimTime = $(R.id.et_trim_time);
        mBtnSave = $(R.id.trim_video);
        mRenderView.setScaleType(FitViewHelper.ScaleType.CENTER_CROP);
        mBtnSave.setOnClickListener(view -> startTrimVideo());
        mVideoEditManager = new UVideoEditManager();
        $(R.id.priview_trim_video).setOnClickListener(v -> {
            int startTime = Integer.parseInt(mStTrimTime.getText().toString());
            int endTime = Integer.parseInt(mEtTrimTime.getText().toString());
            UMediaTime trimTime = (endTime <= 0 || endTime > mDuration) ? null : new UMediaTime(startTime, endTime);
            mVideoEditManager.setTrimTime(trimTime);
        });
    }

    private void startTrimVideo() {
        int startTime = Integer.parseInt(mStTrimTime.getText().toString());
        int endTime = Integer.parseInt(mEtTrimTime.getText().toString());
        UMediaTime trimTime = (endTime <= 0 || endTime > mDuration) ? null : new UMediaTime(startTime, endTime);
        Log.i(TAG, "video path = " + mInputFile + ", trim time: " + (trimTime == null ? mDuration : trimTime.getDuration()));
        Log.i(TAG, "out file: " + mOutFile);
        mVideoEditManager.pause();
        mVideoEditManager.setVideoSaveListener(this)
                .setVideoFrameListener(this)
                .setTrimTime(trimTime)
                .save(mOutFile);
        mStartTime = System.currentTimeMillis();
    }
}
