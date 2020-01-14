package video.movieous.media.demo.activity;

import android.media.AudioFormat;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import video.movieous.engine.UAVOptions;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.media.demo.view.MediaFileListAdapter;
import video.movieous.shortvideo.UVideoEditManager;

import java.util.List;

/**
 * VideoTrimActivity
 */
public class VideoMergeActivity extends BaseEditActivity {
    private static final String TAG = "VideoTrimActivity";

    private TextView mTvTip;
    private TextView mFileTip;
    private MediaFileListAdapter mMediaFileListAdapter;

    private UVideoEditManager mVideoEditManager;
    private long mStartTime;
    private List<String> mMergeVideoList;

    // 拼接文件保存路径
    private String mOutFile = "/sdcard/movieous/shortvideo/merge_test.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startFileSelectActivity(this, false, 5);
    }

    @Override
    protected void getFiles(List<String> inFile) {
        mMergeVideoList = inFile;
        mMediaFileListAdapter.addFileList(inFile);
        mMediaFileListAdapter.notifyDataSetChanged();
        initVideoEditManager();
    }

    private void initView() {
        setContentView(R.layout.activity_video_merge);
        mTvTip = $(R.id.tv_tip);
        mFileTip = $(R.id.file_tip);
        mMediaFileListAdapter = new MediaFileListAdapter(this);
        ((ListView) findViewById(R.id.video_list_view)).setAdapter(mMediaFileListAdapter);
        $(R.id.trim_video).setOnClickListener(view -> startMergeVideo());
    }

    private void initVideoEditManager() {
        mVideoEditManager = new UVideoEditManager();
        UAVOptions options = new UAVOptions()
                .setInteger(UAVOptions.KEY_VIDEO_WIDTH, 480)
                .setInteger(UAVOptions.KEY_VIDEO_HEIGHT, 848)
                .setInteger(UAVOptions.KEY_AUDIO_SAMPLE_RATE, 44100)
                .setInteger(UAVOptions.KEY_VIDEO_BITRATE, 2000 * 1000)
                .setInteger(UAVOptions.KEY_AUDIO_CHANNEL_CONFIG, AudioFormat.CHANNEL_IN_STEREO);
        mVideoEditManager.setAVOptions(options);
        mVideoEditManager.init(null, mInputFile);
    }

    private void startMergeVideo() {
        mStartTime = System.currentTimeMillis();
        mVideoEditManager.mergeVideo(mMergeVideoList, mOutFile, true, new UVideoSaveListener() {
            @Override
            public void onVideoSaveProgress(float progress) {
                Log.d(TAG, "merge video: progress = " + progress);
                runOnUiThread(() -> mTvTip.setText(String.format("%.1f", progress * 100) + "%"));
            }

            @Override
            public void onVideoSaveSuccess(String path) {
                long cost = (System.currentTimeMillis() - mStartTime) / 1000;
                Log.i(TAG, "merge video success: cost = " + cost + ", file = " + path);
                runOnUiThread(() -> {
                    mTvTip.setText("cost: " + cost + "s");
                    mFileTip.setText(path);
                });
            }

            @Override
            public void onVideoSaveFail(int errorCode) {
                String errMsg = "merge video failed, error code = " + errorCode;
                Log.e(TAG, errMsg);
                runOnUiThread(() -> mTvTip.setText(errMsg));
            }
        });
    }

}
