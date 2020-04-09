package video.movieous.media.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import video.movieous.engine.UAVOptions;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseActivity;
import video.movieous.media.listener.URecordListener;
import video.movieous.shortvideo.UScreenRecordManager;

public class ScreenRecordActivity extends BaseActivity implements URecordListener {
    private static final String TAG = "ScreenRecordActivity";
    private static final String OUTPUT_FILE = "/sdcard/movieous/shortvideo/screen_record.mp4";

    private UScreenRecordManager mScreenRecordManager;
    private boolean mIsRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initScreenRecordManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScreenRecordManager != null) {
            mScreenRecordManager.stopRecord();
            mScreenRecordManager = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isOk = mScreenRecordManager.onActivityResult(requestCode, resultCode, data);
        if (isOk) {
            startScreenRecord();
            moveTaskToBack(true);
            Toast.makeText(this, "正在进行录屏...", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_screen_record);
        Button btnStartRecord = $(R.id.btn_start_record);
        Button btnStopRecord = $(R.id.btn_stop_record);
        btnStopRecord.setEnabled(false);

        btnStartRecord.setOnClickListener(view -> {
            view.setEnabled(false);
            if (!mIsRecording) {
                mScreenRecordManager.requestScreenRecord();
            }
            btnStopRecord.setEnabled(true);
        });

        btnStopRecord.setOnClickListener(view -> {
            view.setEnabled(false);
            if (mIsRecording) {
                stopScreenRecord();
            }
            btnStartRecord.setEnabled(true);
        });
    }

    private void initScreenRecordManager() {
        mScreenRecordManager = new UScreenRecordManager();
        mScreenRecordManager.setRecordStateListener(this);
        mScreenRecordManager.init(this, OUTPUT_FILE, new UAVOptions());
    }

    private void startScreenRecord() {
        mIsRecording = true;
        mScreenRecordManager.startRecord();
    }

    private void stopScreenRecord() {
        mIsRecording = false;
        if (mScreenRecordManager != null) {
            mScreenRecordManager.stopRecord();
        }
    }
}
