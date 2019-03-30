package com.movieous.media.ui.fragment;

import android.util.Log;
import android.widget.ImageButton;
import butterknife.BindView;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.UFilter;
import org.jetbrains.annotations.NotNull;
import video.movieous.shortvideo.UVideoEditManager;

import java.util.ArrayList;

public abstract class VideoEditPreviewFragment extends PreviewFragment {
    private static final String TAG = "PreviewFragment";
    private static final int[] FILTER_COLOR = new int[]{
            0x80BD10E0, 0x80FFFF00, 0x8032CD32, 0x80FCB600, 0x8000FA9A, 0x8000ABFC, 0x80FF3030
    };

    protected UVideoEditManager mVideoEditManager;
    protected VideoEditorState mEditorState = VideoEditorState.Idle;
    protected long mVideoDuration;
    @BindView(R.id.pause_playback)
    protected ImageButton mPlayButton;
    protected boolean mIsLongTouch;
    protected float mLastSeekValue;

    private int mColorIndex;

    // 特效信息集合
    private volatile ArrayList<UFilter> mMagicFilterList = new ArrayList<>();

    protected abstract void setSeekViewStart(UFilter filterItem);

    protected abstract void setSeekViewEnd(long duration);

    protected abstract boolean isTimeRangeFilter();

    // TODO 暂时分段特效与贴纸滤镜不能共存，需要确认是否有共存的需求，需要 供应商 确认
    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        if (isFilterVendorEnabled(false) && mFilterSdkManager != null && isTimeRangeFilter()) {
            UFilter filterItem = getMagicFilterByPosition(mVideoEditManager.getCurrentPosition());
            if (filterItem != null) {
                mFilterSdkManager.changeFilter(filterItem);
            } else if (isTimeRangeFilter()) {
                mFilterSdkManager.clearAllFilters();
            }
        }
        return super.onDrawFrame(texId, texWidth, texHeight);
    }

    @Override
    public synchronized void onMagicFilterChanged(@NotNull UFilter filter) {
        if (filter == null || mFilterSdkManager == null) {
            return;
        }
        Log.i(TAG, "onMagicFilterChanged: filter change: " + filter.getName());
        mCurrentFilter = filter;
        mFilterSdkManager.clearAllFilters();
        boolean isEnabled = filter.getEnabled();
        if (isEnabled) {
            mFilterSdkManager.changeFilter(filter);
        }
        if (isTimeRangeFilter()) {
            if (isEnabled) {
                int position = mVideoEditManager.getCurrentPosition();
                Log.i(TAG, "filter start = " + position);
                filter.setStart(position);
                filter.setEnd(mVideoDuration);
                filter.setColor(getRectColor());
                mMagicFilterList.add(filter);
                startPlayback();
                setSeekViewStart(filter);
            } else {
                pausePlayback();
                if (filter.getDuration() < 100) {
                    Log.i(TAG, "filter duration is too short, remove filter");
                    mMagicFilterList.remove(filter);
                } else {
                    int position = mVideoEditManager.getCurrentPosition();
                    if (position < filter.getStart()) {
                        Log.i(TAG, "filter end = " + position);
                    } else {
                        filter.setEnd(mVideoEditManager.getCurrentPosition());
                    }
                    setSeekViewEnd(mVideoDuration);
                }
            }
            setLongTouchState(isEnabled);
        } else if (mEditorState == VideoEditorState.Paused) {
            startPlayback();
        }
    }

    @Override
    public void onRemoveLastFilter() {
        if (mMagicFilterList.size() > 0) {
            mMagicFilterList.remove(mMagicFilterList.size() - 1);
        }
    }

    protected void startPlayback() {
        if (mEditorState == VideoEditFragment.VideoEditorState.Idle) {
            mVideoEditManager.setVideoFrameListener(this);
            mVideoEditManager.start();
            mEditorState = VideoEditFragment.VideoEditorState.Playing;
            Log.i(TAG, "startPlayback: start");
        } else if (mEditorState == VideoEditFragment.VideoEditorState.Paused) {
            mVideoEditManager.resume();
            Log.i(TAG, "startPlayback: resume");
            mEditorState = VideoEditFragment.VideoEditorState.Playing;
        }
        setPlayButtonState(R.drawable.btn_pause);
    }

    protected void stopPlayback() {
        mVideoEditManager.stop();
        mEditorState = VideoEditFragment.VideoEditorState.Idle;
        setPlayButtonState(R.drawable.btn_play);
    }

    protected void pausePlayback() {
        if (mVideoEditManager != null && mVideoEditManager.isPlaying()) {
            mVideoEditManager.pause();
            mEditorState = VideoEditorState.Paused;
            setPlayButtonState(R.drawable.btn_play);
        }
    }

    protected void onCompletion() {
        if (mIsLongTouch) {
            pausePlayback();
        }
    }

    private void setPlayButtonState(int resId) {
        if (mPlayButton == null) return;
        mActivity.runOnUiThread(() -> mPlayButton.setImageResource(resId));
    }

    private void setLongTouchState(boolean enable) {
        if (isTimeRangeFilter()) {
            mIsLongTouch = enable;
        }
    }

    private synchronized UFilter getMagicFilterByPosition(long position) {
        for (UFilter filterItem : mMagicFilterList) {
            if ((filterItem.getStart() <= position && position <= filterItem.getEnd())) {
                //Log.i(TAG, "getMagicFilterByPosition: " + filterItem.getName());
                return filterItem;
            }
        }
        return null;
    }

    private int getRectColor() {
        int colorIndex = mColorIndex;
        mColorIndex++;
        if (mColorIndex >= FILTER_COLOR.length) mColorIndex = 0;
        return FILTER_COLOR[colorIndex];
    }

    protected enum VideoEditorState {
        Idle,
        Playing,
        Paused,
    }
}
