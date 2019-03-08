package video.movieous.media.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import video.movieous.engine.UAVOptions;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.shortvideo.UMultiVideoRecordManager;

/**
 * MultiVideoRecordActivity
 */
public class MultiVideoRecordActivity extends BaseEditActivity implements UVideoSaveListener {

    private String mOutFile = "/sdcard/movieous/shortvideo/multirecord.mp4";
    private UTextureView mRenderView;
    private Button mRecordButton;

    private UMultiVideoRecordManager mRecordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startVideoSelectActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecordManager != null) {
            mRecordManager.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecordManager != null) {
            mRecordManager.stopPreview();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_multi_video_record);
        mRenderView = $(R.id.render_view);
        mRecordButton = $(R.id.record);
        $(R.id.switch_camera).setOnClickListener(view -> mRecordManager.switchCamera());
        $(R.id.save_record).setOnClickListener(view -> mRecordManager.combineClip(mOutFile, this));
        mRecordButton.setOnClickListener(v -> {
            if (mRecordButton.getTag() == null) {
                mRecordButton.setTag(0);
                mRecordButton.setText("停止");
                mRecordManager.startRecord();
            } else {
                mRecordButton.setTag(null);
                mRecordButton.setText("录制");
                mRecordManager.stopRecord();
            }
        });
    }

    private void initRecordManager(String videoFile) {
        mRecordManager = new UMultiVideoRecordManager();
        UAVOptions options = new UAVOptions()
                .setInteger(UAVOptions.KEY_VIDEO_WIDTH, 720)
                .setInteger(UAVOptions.KEY_VIDEO_HEIGHT, 640);
        mRecordManager.setAVOptions(options);
        mRecordManager.setVideoFile(videoFile);
        mRecordManager.init(mRenderView);
    }

    @Override
    protected void getVideoFile(String file) {
        initRecordManager(file);
    }
}
