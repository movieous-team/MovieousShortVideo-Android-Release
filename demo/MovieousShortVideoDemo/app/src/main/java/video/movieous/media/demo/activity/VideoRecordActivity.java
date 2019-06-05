package video.movieous.media.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.core.env.FitViewHelper;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BasePreviewActivity;
import video.movieous.media.demo.kiwi.KwTrackerWrapper;
import video.movieous.media.demo.utils.UriUtil;
import video.movieous.shortvideo.UVideoRecordManager;

/**
 * 视频录制
 */
public class VideoRecordActivity extends BasePreviewActivity {
    private static final String TAG = "VideoRecordActivity";
    private static final int REQUEST_CODE_CHOOSE = 1;
    private String mOutFile = "/sdcard/movieous/shortvideo/record.mp4";

    private ImageView mPreviewImage;
    private Button mRecordButton;
    private UVideoRecordManager mRecordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initRecordManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKwTrackWrapper.onResume(this);
        mRecordManager.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mKwTrackWrapper.onPause(this);
        mRecordManager.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKwTrackWrapper.onDestroy(this);
        mRecordManager.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = UriUtil.getPath(this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                mRecordManager.setMusicFile(selectedFilepath);
                mRecordManager.setMusicPositionMs(60 * 1000);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_video_record);
        mRenderView = $(R.id.render_view);
        mPreviewImage = $(R.id.preview_image);
        mRecordButton = $(R.id.record);
        mRenderView.setScaleType(FitViewHelper.ScaleType.CENTER_CROP);

        $(R.id.switch_camera).setOnClickListener(v -> mRecordManager.switchCamera());

        $(R.id.add_music).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
            }
            startActivityForResult(Intent.createChooser(intent, "请选择音乐文件："), REQUEST_CODE_CHOOSE);
        });

        $(R.id.save_record).setOnClickListener(v -> mRecordManager.combineClip(mOutFile, new UVideoSaveListener() {
            @Override
            public void onVideoSaveSuccess(String path) {
                String msg = "onVideoSaveSuccess: " + path;
                Toast.makeText(VideoRecordActivity.this, msg, Toast.LENGTH_SHORT).show();
                mRecordManager.removeAllClips();
            }

            @Override
            public void onVideoSaveFail(int errorCode) {
                String msg = "onVideoSaveFail: error code = " + errorCode;
                Toast.makeText(VideoRecordActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, msg);
            }
        }));

        $(R.id.capture).setOnClickListener(v -> mRecordManager.captureVideoFrame(bitmap -> runOnUiThread(() -> mPreviewImage.setImageBitmap(bitmap)), false));

        mRecordButton.setOnClickListener(v -> {
            if (!mRecordManager.isRecording()) {
                mRecordButton.setText("停止");
                mRecordManager.startRecord();
            } else {
                mRecordButton.setText("录制");
                mRecordManager.stopRecord();
            }
        });

        // 内置美颜
        $(R.id.builtin_beauty).setOnClickListener(v-> {
            if (v.getTag() != null) {
                mKwControlView.setVisibility(View.INVISIBLE);
                v.setTag(null);
            } else {
                mKwControlView.setVisibility(View.VISIBLE);
                v.setTag(1);
            }
        });

        mKwTrackWrapper = new KwTrackerWrapper(this);
        mKwTrackWrapper.onCreate(this);
        mKwControlView = findViewById(R.id.kiwi_control_layout);
        mKwControlView.setOnEventListener(mKwTrackWrapper.initUIEventListener());
    }

    private void initRecordManager() {
        mRecordManager = new UVideoRecordManager();
        mRecordManager.init(mRenderView);
        mRecordManager.setVideoFrameListener(this);
    }

}
