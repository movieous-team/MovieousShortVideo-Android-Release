package video.movieous.media.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import video.movieous.engine.UMediaTrimTime;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.core.env.FitViewHelper;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.shortvideo.UMediaUtil;
import video.movieous.shortvideo.UVideoEditManager;
import video.movieous.engine.view.UTextureView;

/**
 * VideoTrimActivity
 */
public class VideoTrimActivity extends BaseEditActivity implements UVideoSaveListener {
    private static final String TAG = "VideoTrimActivity";

    private static final int REQUEST_CODE_CHOOSE = 1;

    private UTextureView mRenderView;
    private TextView mTvTip;
    private TextView mFileTip;
    private EditText mEtTrimTime;
    private Button mBtnSave;

    private UVideoEditManager mVideoEditManager;
    private long mStartTime;
    private long mDuration;

    // 剪辑文件保存路径
    private String mOutFile = "/sdcard/movieous/shortvideo/clip_test.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startVideoSelectActivity(this);
    }

    @Override
    protected void getVideoFile(String inFile) {
        mVideoFile = inFile;
        mFileTip.setText(mVideoFile);
        mDuration = UMediaUtil.getMetadata(mVideoFile).duration;
        mVideoEditManager.init(mRenderView, mVideoFile)
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

    private void initView() {
        setContentView(R.layout.activity_video_trim);
        mRenderView = $(R.id.render_view);
        mTvTip = $(R.id.tv_tip);
        mFileTip = $(R.id.file_tip);
        mEtTrimTime = $(R.id.et_trim_time);
        mBtnSave = $(R.id.trim_video);
        mRenderView.setScaleType(FitViewHelper.ScaleType.CENTER_CROP);
        mBtnSave.setOnClickListener(view -> startTrimVideo());
        mVideoEditManager = new UVideoEditManager();
    }

    private void startTrimVideo() {
        int endTime = Integer.parseInt(mEtTrimTime.getText().toString()) * 1000;
        UMediaTrimTime trimTime = (endTime <= 0 || endTime > mDuration) ? null : new UMediaTrimTime(0, endTime);
        Log.i(TAG, "video path = " + mVideoFile + ", trim time: " + (trimTime == null ? mDuration : trimTime.getDuration()));
        Log.i(TAG, "out file: " + mOutFile);
        mVideoEditManager.stop();
        mVideoEditManager.setVideoSaveListener(this)
                .setVideoFrameListener(this)
                .transcode(mOutFile, trimTime);
        mStartTime = System.currentTimeMillis();
    }
}
