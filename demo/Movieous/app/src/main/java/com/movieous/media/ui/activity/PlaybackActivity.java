package com.movieous.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import cn.ucloud.ufile.api.ApiError;
import cn.ucloud.ufile.bean.UfileErrorBean;
import cn.ucloud.ufile.bean.base.BaseResponseBean;
import cn.ucloud.ufile.http.UfileCallback;
import com.google.android.exoplayer2.Player;
import com.movieous.media.Constants;
import com.movieous.media.R;
import com.movieous.media.api.ufilesdk.UFileUploadManager;
import com.movieous.media.utils.AppUtils;
import com.movieous.media.utils.GetPathFromUri;
import com.movieous.media.utils.StringUtils;
import iknow.android.utils.thread.BackgroundExecutor;
import okhttp3.Request;
import video.movieous.droid.player.ui.widget.VideoView;
import video.movieous.shortvideo.UMediaUtil;

import static com.movieous.media.ExtensionsKt.showToast;

public class PlaybackActivity extends AppCompatActivity {
    private static final String TAG = "PlaybackActivity";
    private static final String MP4_PATH = "MP4_PATH";

    private VideoView mVideoView;
    private static String mVideoPath;
    private Button mUploadBtn;
    private UFileUploadManager mVideoUploadManager;
    private ProgressBar mProgressBarDeterminate;
    private boolean mIsUpload = false;

    public static void start(Activity activity, String mp4Path) {
        mVideoPath = mp4Path;
        Intent intent = new Intent(activity, PlaybackActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String videoPath = getIntent().getStringExtra(MP4_PATH);
        if (StringUtils.INSTANCE.isEmpty(videoPath)) {
            startActivityForResult(Intent.createChooser(AppUtils.Companion.getMediaIntent(true), getString(R.string.select_media_file_tip)), VideoEditActivity.REQUEST_CODE_CHOOSE);
            return;
        }
        setContentView(R.layout.activity_playback);
        mVideoView = findViewById(R.id.video_view);
        setVideo(videoPath);

        // 上传代码演示，参数需要换成您的 bucket 空间参数
        // 如果希望更精细的上传控制，可以直接使用 ufile sdk 地址：https://github.com/ucloud/ufile-sdk-android
        mVideoUploadManager = new UFileUploadManager(Constants.REGION, Constants.PROXY_SUFFIX, Constants.PUBLIC_KEY, Constants.APPLY_AUTH_URL, Constants.APPLY_PRIVATE_AUTH_URL);
        mUploadBtn = findViewById(R.id.btn_upload);
        mUploadBtn.setText(R.string.upload);
        mUploadBtn.setVisibility(View.INVISIBLE);
        mUploadBtn.setOnClickListener(new UploadOnClickListener());
        mProgressBarDeterminate = findViewById(R.id.progressBar);
        mProgressBarDeterminate.setMax(100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VideoEditActivity.REQUEST_CODE_CHOOSE) {
                String selectedFilepath = GetPathFromUri.INSTANCE.getPath(this, data.getData());
                if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                    start(PlaybackActivity.this, selectedFilepath);
                    finish();
                }
            }
        } else {
            finish();
        }
    }

    /**
     * 设置播放视频 URL
     */
    private void setVideo(String url) {
        Log.d(TAG, "play url = " + url);
        mVideoView.setVideoPath(url);
        mVideoView.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

    // 上传到 UCloud，仅做参考
    private class UploadOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mProgressBarDeterminate.setVisibility(View.VISIBLE);
            BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                @Override
                public void execute() {
                    // 检查并提前 moov
                    boolean isOk = UMediaUtil.fastStartMp4(mVideoPath, Constants.UPLOAD_FILE_PATH) == 0;
                    String uploadFile = isOk ? Constants.UPLOAD_FILE_PATH : mVideoPath;

                    if (v != null) return; // demo 这里直接返回

                    // 仅做上传演示之用
                    mVideoUploadManager.startUpload(uploadFile, Constants.BUCKET, new UfileCallback<BaseResponseBean>() {
                        @Override
                        public void onResponse(BaseResponseBean response) {
                            onUploadSuccess(response.getMessage());
                        }

                        @Override
                        public void onError(Request request, ApiError error, UfileErrorBean response) {
                            onUploadFail(response.getErrMsg());
                        }

                        @Override
                        public void onProgress(long bytesWritten, long contentLength) {
                            onUploadProgress((int) (bytesWritten * 100 / contentLength));
                        }
                    });
                    mIsUpload = true;
                }
            });
        }
    }

    public void onUploadProgress(int percent) {
        mProgressBarDeterminate.setProgress(percent);
        if (1.0 == percent) {
            mProgressBarDeterminate.setVisibility(View.INVISIBLE);
        }
    }

    public void onUploadSuccess(String response) {
        runOnUiThread(() -> {
            showToast(PlaybackActivity.this, response);
            mUploadBtn.setVisibility(View.INVISIBLE);
        });
    }

    public void onUploadFail(final String response) {
        runOnUiThread(() -> showToast(PlaybackActivity.this, "Upload failed, msg: " + response));
    }
}