package com.movieous.media.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.movieous.media.Constants;
import com.movieous.media.R;
import com.movieous.media.mvp.contract.MusicSelectedListener;
import com.movieous.media.ui.activity.PlaybackActivity;
import com.movieous.media.ui.activity.VideoEditActivity;
import com.movieous.media.utils.StringUtils;
import com.movieous.media.view.CameraFocusIndicator;
import com.movieous.media.view.RecordTimer;
import com.movieous.media.view.ShutterButton;
import com.movieous.media.view.TimeDownView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import video.movieous.engine.UCameraFocusListener;
import video.movieous.engine.view.UFitViewHelper;
import video.movieous.media.listener.URecordListener;
import video.movieous.shortvideo.UVideoRecordManager;

import static com.movieous.media.ExtensionsKt.showToast;

/**
 * 相机预览页面
 */
public class VideoRecordFragment extends PreviewFragment implements URecordListener, UCameraFocusListener, MusicSelectedListener {
    private static final String TAG = "VideoRecordFragment";

    @BindView(R.id.tv_countdown)
    TextView mTvCountDown;
    @BindView(R.id.btn_shutter)
    ShutterButton mBtnShutter;
    @BindView(R.id.btn_delete_clip)
    Button mBtnDeleteClip;
    @BindView(R.id.btn_merge_clip)
    Button mBtnCombineClip;
    @BindView(R.id.btn_stickers)
    Button mBtnStickers;
    @BindView(R.id.btn_video_edit)
    Button mBtnVideoEdit;
    @BindView(R.id.focus_indicator)
    CameraFocusIndicator mFocusIndicator;
    @BindView(R.id.tab_speed_panel)
    SegmentTabLayout mRecordSpeedPanel;
    @BindView(R.id.tv_time_down_count)
    TimeDownView mTimeDownView;
    @BindView(R.id.camera_light)
    ImageView mBtnCameraLight;

    private UVideoRecordManager mVideoRecordManager;
    // sticker
    private StickerFilterFragment mStickerFilterFragment;
    // music list
    private MusicSelectFragment mMusicSelectFragment;
    private BeautyFilterFragment mBeautyFilterFragment;
    private RecordTimer mRecordTimer;
    private GestureDetector mGestureDetector;
    private int mFocusMarkerX;
    private int mFocusMarkerY;
    private boolean mIsEditVideo = false;
    private boolean mIsStickerFilterShowing = false;
    private boolean mIsMusicSelectShowing = false;
    private boolean mIsBeautyFilterShowing = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoRecordManager.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoRecordManager.stopPreview();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStickerFilterFragment = null;
        mBeautyFilterFragment = null;
        mProcessingDialog = null;
        mActivity = null;
        if (mFilterSdkManager != null) {
            mFilterSdkManager.clearAllFilters();
            mFilterSdkManager.destroy();
            mFilterSdkManager = null;
        }
        mVideoRecordManager.release();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    public void lazyLoad() {
        super.lazyLoad();
        initVideoRecordManager();
        initTimer();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        mRecordSpeedPanel.setVisibility(View.GONE);
        mBtnShutter.setOnShutterListener(mShutterListener);
        mBtnShutter.setIsRecorder(true);
        mBtnShutter.setProgressMax(Constants.DEFAULT_MAX_RECORD_DURATION);
        mGestureDetector = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!hideFragmentView()) {
                    mFocusMarkerX = (int) e.getX() - mFocusIndicator.getWidth() / 2;
                    mFocusMarkerY = (int) e.getY() - mFocusIndicator.getHeight() / 2;
                    mVideoRecordManager.focus(e.getX(), e.getY());
                }
                return false;
            }
        });
        mPreview.setScaleType(UFitViewHelper.ScaleType.CENTER_CROP);
        mPreview.setOnTouchListener((view, motionEvent) -> {
            mGestureDetector.onTouchEvent(motionEvent);
            return true;
        });
    }

    private boolean hideFragmentView() {
        boolean ret = false;
        if (mIsStickerFilterShowing) {
            hideStickerView();
            ret = true;
        } else if (mIsBeautyFilterShowing) {
            ret = true;
            hideFaceBeautyView();
        } else if (mIsMusicSelectShowing) {
            ret = true;
            hideMusicSelectView();
        }
        return ret;
    }

    // 音乐选择
    @OnClick(R.id.btn_select_music)
    public void onClickAddMusic() {
        if (mIsMusicSelectShowing) {
            hideMusicSelectView();
        } else {
            showMusicFragment();
        }
    }

    private void showMusicFragment() {
        mIsMusicSelectShowing = true;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (mMusicSelectFragment == null) {
            mMusicSelectFragment = new MusicSelectFragment();
            mMusicSelectFragment.setMusicSelectedListener(this);
            ft.add(R.id.fragment_container, mMusicSelectFragment);
        } else {
            ft.show(mMusicSelectFragment);
        }
        if (mStickerFilterFragment != null) {
            ft.hide(mStickerFilterFragment);
        }
        ft.commit();
        hideBottomLayout();
    }

    /**
     * 显示动态贴纸页面
     */
    @OnClick(R.id.btn_stickers)
    public void showStickerFilterFragment() {
        if (!isFilterVendorEnabled(true)) return;
        mIsStickerFilterShowing = true;
        mVideoRecordManager.setOutputBuffer(mFilterSdkManager.getRGBABuffer());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (mStickerFilterFragment == null) {
            mStickerFilterFragment = new StickerFilterFragment();
            mStickerFilterFragment.setFilterSdkManager(mFilterSdkManager);
            mStickerFilterFragment.setOnFilterChangedListener(this);
            ft.add(R.id.fragment_container, mStickerFilterFragment);
        } else {
            ft.show(mStickerFilterFragment);
        }
        ft.commit();
        hideBottomLayout();
    }

    @OnClick(R.id.btn_merge_clip)
    public void onCombineClick(View v) {
        showProcessingDialog();
        showChooseDialog();
    }

    @OnClick(R.id.btn_delete_clip)
    public void onDeleteClipClick() {
        onDeleteClip(false);
    }

    @OnClick(R.id.close_window)
    public void onClickClose() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.switch_camera)
    public void onClickSwitchCamera() {
        mVideoRecordManager.switchCamera();
        mFocusIndicator.focusCancel();
    }

    @OnClick(R.id.camera_light)
    public void onClickCameraLight() {
        if (!mVideoRecordManager.isCameraFlashSupported()) {
            showToast(mActivity, getString(R.string.no_camera_flash_support));
            return;
        }
        boolean nowMode = mBtnCameraLight.getTag() == null;
        boolean isOk = mVideoRecordManager.turnCameraLight(nowMode);
        mBtnCameraLight.setTag(!nowMode ? null : 1);
        mBtnCameraLight.setSelected(isOk && nowMode);
    }

    @OnClick(R.id.record_speed)
    public void onClickSpeed() {
        if (mRecordSpeedPanel.getTabCount() == 0) {
            initSpeedPanel();
        }
        mRecordSpeedPanel.setVisibility(mRecordSpeedPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.face_beauty)
    public void onClickFaceBeauty() {
        if (!isFilterVendorEnabled(true)) return;
        if (mIsBeautyFilterShowing) {
            hideFaceBeautyView();
        } else {
            showFaceBeautyView();
        }
    }

    @OnClick(R.id.btn_video_edit)
    public void onVideoEditClick(View v) {
        mActivity.startActivity(new Intent(mActivity, VideoEditActivity.class));
    }

    @OnClick(R.id.count_down)
    public void onTimeDownClick() {
        mTimeDownView.setVisibility(View.VISIBLE);
        mTimeDownView.downSecond(2);
        mTimeDownView.setOnTimeDownListener(new TimeDownView.DownTimeWatcher() {
            @Override
            public void onLastTimeFinish(int num) {
                mTimeDownView.setVisibility(View.GONE);
                mTimeDownView.closeDefaultAnimate();
                mBtnShutter.openButton();
                startRecord();
            }
        });
    }

    // URecordListener
    @Override
    public void onRecordStart() {
        onStartRecord();
    }

    @Override
    public void onRecordStop() {
        onStopRecord();
    }

    @Override
    public void onRecordFinish(String filePath, long duration) {
        Log.d(TAG, "onRecordFinish: file = " + filePath + ", clip duration = " + duration);
    }

    @Override
    public void onClipChanged(long totalDurationMs, int clipCount) {
        Log.i(TAG, "onClipChanged: total duration = " + totalDurationMs + ", clip count = " + clipCount);
        mActivity.runOnUiThread(() -> setShutterTime(totalDurationMs, clipCount));
    }

    // UCameraFocusListener
    @Override
    public void onManualFocusStart(boolean success) {
        if (success) {
            Log.i(TAG, "manual focus start success");
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFocusIndicator.getLayoutParams();
            lp.leftMargin = mFocusMarkerX;
            lp.topMargin = mFocusMarkerY;
            mFocusIndicator.setLayoutParams(lp);
            mFocusIndicator.focus();
        } else {
            mFocusIndicator.focusCancel();
            Log.i(TAG, "manual focus not supported");
        }
    }

    @Override
    public void onManualFocusStop(boolean success) {
        Log.i(TAG, "manual focus stop result: " + success);
        if (success) {
            mFocusIndicator.focusSuccess();
        } else {
            mFocusIndicator.focusFail();
        }
    }

    // USaveListener
    @Override
    public void onVideoSaveSuccess(String destFile) {
        Log.i(TAG, "combine clips success filePath: " + destFile);
        mActivity.runOnUiThread(() -> {
            mProcessingDialog.dismiss();
            if (mIsEditVideo) {
                VideoEditActivity.start(mActivity, destFile);
            } else {
                PlaybackActivity.start(mActivity, destFile);
            }
        });
    }

    private void setShutterTime(long totalDuration, int clipCount) {
        mBtnShutter.setProgress(totalDuration, true);
        if (clipCount > 0) {
            mTvCountDown.setText(StringUtils.INSTANCE.generateTime(totalDuration));
            mBtnDeleteClip.setVisibility(View.VISIBLE);
            mBtnCombineClip.setVisibility(View.VISIBLE);
            mBtnVideoEdit.setVisibility(View.GONE);
        } else {
            mTvCountDown.setText("");
            mBtnDeleteClip.setVisibility(View.GONE);
            mBtnCombineClip.setVisibility(View.GONE);
            mBtnVideoEdit.setVisibility(View.VISIBLE);
        }
    }

    private void hideFilterFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    private void hideStickerView() {
        if (mIsStickerFilterShowing) {
            mIsStickerFilterShowing = false;
            if (mStickerFilterFragment != null) {
                hideFilterFragment(mStickerFilterFragment);
            }
        }
        resetBottomLayout();
    }

    private void hideMusicSelectView() {
        if (mIsMusicSelectShowing) {
            mIsMusicSelectShowing = false;
            if (mMusicSelectFragment != null) {
                hideFilterFragment(mMusicSelectFragment);
            }
        }
        resetBottomLayout();
    }

    private void showFaceBeautyView() {
        if (mIsStickerFilterShowing) {
            hideStickerView();
        } else if (mIsMusicSelectShowing) {
            hideMusicSelectView();
        }
        mIsBeautyFilterShowing = true;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (mBeautyFilterFragment == null) {
            mBeautyFilterFragment = new BeautyFilterFragment();
            ft.add(R.id.fragment_container, mBeautyFilterFragment);
        } else {
            ft.show(mBeautyFilterFragment);
        }
        ft.commit();
        mBeautyFilterFragment.setOnFilterChangedListener(this);
        hideBottomLayout();
    }

    private void hideFaceBeautyView() {
        if (mIsBeautyFilterShowing) {
            mIsBeautyFilterShowing = false;
            if (mBeautyFilterFragment != null) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.hide(mBeautyFilterFragment);
                ft.commit();
            }
        }
        resetBottomLayout();
    }

    private void hideBottomLayout() {
        mBtnVideoEdit.setVisibility(View.GONE);
        mBtnStickers.setVisibility(View.INVISIBLE);
        mBtnShutter.setVisibility(View.INVISIBLE);
        mTvCountDown.setVisibility(View.INVISIBLE);
        mBtnCombineClip.setVisibility(View.GONE);
        mBtnDeleteClip.setVisibility(View.GONE);
    }

    private void resetBottomLayout() {
        ViewGroup.LayoutParams layoutParams = mBtnShutter.getLayoutParams();
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mActivity.getResources().getDisplayMetrics());
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mActivity.getResources().getDisplayMetrics());
        boolean isRecording = mBtnShutter.getProgress() > 0;
        mBtnShutter.setLayoutParams(layoutParams);
        mBtnShutter.setVisibility(View.VISIBLE);
        mBtnStickers.setVisibility(View.VISIBLE);
        mBtnVideoEdit.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        int isVisible = isRecording ? View.VISIBLE : View.INVISIBLE;
        mTvCountDown.setVisibility(isVisible);
        mBtnCombineClip.setVisibility(isVisible);
        mBtnDeleteClip.setVisibility(isVisible);
    }

    private void initTimer() {
        long countDownIntervalMs = 50;
        mRecordTimer = new RecordTimer(countDownIntervalMs) {
            @Override
            public void onTick(long progress) {
                if (mActivity == null) return;
                mActivity.runOnUiThread(() -> {
                    mBtnShutter.setProgress(progress, false);
                    mTvCountDown.setText(StringUtils.INSTANCE.generateTime((long) mBtnShutter.getProgress()));
                });
            }
        };
    }

    // 初始化 UVideoRecordManager
    private void initVideoRecordManager() {
        mVideoRecordManager = new UVideoRecordManager()
                .init(mPreview)
                .setAVOptions(mAVOptions) // 自定义音视频参数
                .setRecordListener(this)
                .setFocusListener(this)
                .setVideoFrameListener(this);
    }

    private void onStartRecord() {
        mActivity.runOnUiThread(() -> {
            mTvCountDown.setEnabled(false);
            mBtnCombineClip.setVisibility(View.GONE);
            mBtnDeleteClip.setVisibility(View.GONE);
            mTvCountDown.setVisibility(View.VISIBLE);
            mBtnShutter.addSplitView();
            mRecordTimer.start();
        });
    }

    private void onStopRecord() {
        mActivity.runOnUiThread(() -> {
            mRecordTimer.cancel();
            mBtnCombineClip.setVisibility(View.VISIBLE);
            mBtnDeleteClip.setVisibility(View.VISIBLE);
            mBtnVideoEdit.setVisibility(View.GONE);
            mTvCountDown.setEnabled(true);
        });
    }

    private void onDeleteClip(boolean clearAll) {
        if (mBtnShutter.isDeleteMode()) {
            if (clearAll) {
                mBtnShutter.cleanSplitView();
                mVideoRecordManager.removeAllClips();
            } else {
                mBtnShutter.deleteSplitView();
                mVideoRecordManager.removeLastClip();
            }
        } else {
            mBtnShutter.setDeleteMode(true);
        }
    }

    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getString(R.string.if_edit_video));
        builder.setPositiveButton(getString(R.string.dlg_yes), (dialog, which) -> {
            mIsEditVideo = true;
            combineClip();
        });
        builder.setNegativeButton(getString(R.string.dlg_no), (dialog, which) -> {
            mIsEditVideo = false;
            combineClip();
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void combineClip() {
        mVideoRecordManager.combineClip(Constants.RECORD_FILE_PATH, this);
    }

    // ShutterButton.OnShutterListener
    private ShutterButton.OnShutterListener mShutterListener = new ShutterButton.OnShutterListener() {

        @Override
        public void onStartRecord() {
            Log.i(TAG, "onStartRecord");
            startRecord();
        }

        @Override
        public void onStopRecord() {
            Log.i(TAG, "onStopRecord");
            stopRecord();
        }

        @Override
        public void onProgressOver() {
            Log.i(TAG, "onProgressOver");
            stopRecord();
        }
    };

    private void startRecord() {
        mVideoRecordManager.startRecord();
    }

    private void stopRecord() {
        mVideoRecordManager.stopRecord();
    }

    private void initSpeedPanel() {
        double[] recordSpeed = new double[]{Constants.VIDEO_SPEED_SUPER_SLOW, Constants.VIDEO_SPEED_SLOW, Constants.VIDEO_SPEED_NORMAL, Constants.VIDEO_SPEED_FAST, Constants.VIDEO_SPEED_SUPER_FAST};
        String[] titles = {getString(R.string.speed_super_slow), getString(R.string.speed_slow), getString(R.string.speed_normal), getString(R.string.speed_fast), getString(R.string.speed_super_fast)};
        mRecordSpeedPanel.setTabData(titles);
        mRecordSpeedPanel.setCurrentTab(2);
        mRecordSpeedPanel.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                // TODO
                //mVideoRecordManager.setVideoSpeed(recordSpeed[position]);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    @Override
    public void onMusicSelected(@NotNull String file) {
        Log.i(TAG, "Select file: " + file);
        if (!TextUtils.isEmpty(file)) {
            mVideoRecordManager.setMusicFile(file);
            mVideoRecordManager.setOriginVolume(0);
        }
    }
}
