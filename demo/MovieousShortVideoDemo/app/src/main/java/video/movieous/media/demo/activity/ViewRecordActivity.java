package video.movieous.media.demo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import video.movieous.engine.view.ULinearLayout;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseActivity;
import video.movieous.shortvideo.UViewRecordManager;
import video.movieous.engine.view.UTextureView;

/**
 * view 录制
 */
public class ViewRecordActivity extends BaseActivity {
    private static final String OUT_FILE = "/sdcard/movieous/shortvideo/view.mp4";
    private UTextureView mRenderView;
    private ImageView mPreviewImage;
    private ULinearLayout mLinearLayout;
    private WebView mWebView;
    private Button mRecordButton;

    private UViewRecordManager mViewRecordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewRecordManager();
    }

    private void initView() {
        setContentView(R.layout.activity_view_record);
        mRenderView = $(R.id.render_view);
        mRenderView.setRenderMode(UTextureView.RENDERMODE_CONTINUOUSLY);
        mPreviewImage = $(R.id.preview_image);
        mLinearLayout = $(R.id.gl_layout);
        mWebView = $(R.id.web_view);

        mRecordButton = $(R.id.record);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://www.movieous.video");

        $(R.id.capture).setOnClickListener(view -> {
            mViewRecordManager.captureVideoFrame(bitmap -> runOnUiThread(() -> mPreviewImage.setImageBitmap(bitmap)), true);
            mRenderView.requestRender();
        });

        mRecordButton.setOnClickListener(v -> {
            if (mViewRecordManager.isRecording()) {
                stopRecord();
            } else {
                startRecord();
            }
        });
    }

    private void initViewRecordManager() {
        mViewRecordManager = new UViewRecordManager();
        mViewRecordManager.init(mRenderView);
        mViewRecordManager.setRecordFile(OUT_FILE);
        mViewRecordManager.setRecordView(mLinearLayout);
    }

    private void startRecord() {
        mRecordButton.setText("停止");
        if (mViewRecordManager != null) {
            mViewRecordManager.startRecord();
        }
    }

    private void stopRecord() {
        mRecordButton.setText("录制");
        if (mViewRecordManager != null) {
            mViewRecordManager.stopRecord();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
