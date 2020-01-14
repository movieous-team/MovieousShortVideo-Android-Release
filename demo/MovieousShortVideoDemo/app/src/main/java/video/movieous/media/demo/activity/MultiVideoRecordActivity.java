package video.movieous.media.demo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;
import video.movieous.engine.UAVOptions;
import video.movieous.engine.UConstants;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.shortvideo.UMultiVideoRecordManager;

import java.util.List;

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
        startFileSelectActivity(this, false, 1);
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
        UAVOptions options = new UAVOptions()
                .setInteger(UAVOptions.KEY_VIDEO_WIDTH, 720)    // 设置输出文件宽度
                .setInteger(UAVOptions.KEY_VIDEO_HEIGHT, 1280); // 设置输出文件高度
        mRecordManager = new UMultiVideoRecordManager()
                .setVideoFile(videoFile)  // 设置合拍文件本地路径
                .setScaleType(UConstants.SCALE_TYPE_CENTER_CROP) // 设置缩放模式
                .setVideoLayout(UMultiVideoRecordManager.VideoLayout.TOP, 0.5f); // 设置合拍视频布局和大小（占宽或者高的百分比）
        mRecordManager.setAVOptions(options)
                .setVideoFrameListener(this) // 设置摄像头数据回调，可以通过第三方进行美颜、贴纸处理
                .init(mRenderView);
    }

    @Override
    protected void getFiles(List<String> fileList) {
        initRecordManager(fileList.get(0));
    }
}
