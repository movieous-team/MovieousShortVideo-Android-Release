package com.movieous.media.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.movieous.media.R;
import com.movieous.media.base.BaseActivity;
import com.movieous.media.mvp.model.VideoDataUtil;
import com.movieous.media.mvp.model.entity.MediaParam;
import com.movieous.media.mvp.model.entity.VideoListItem;
import com.movieous.media.ui.adapter.VideoDetailsAdapter;
import com.movieous.media.utils.SharePrefUtils;
import com.movieous.media.view.CustomSettingDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import org.jetbrains.annotations.Nullable;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import java.util.ArrayList;

/**
 * 仿抖音上下滑动播放
 */
public class VideoPlayActivity extends BaseActivity {
    private Context mContext;
    private RecyclerView mVideoListRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PagerSnapHelper mPagerSnapHelper;
    private ArrayList<VideoListItem> mVideoList;
    private boolean mIsPause;
    private int mPlayPosition;

    private View mPlayView;

    private void playVideo() {
        View snapView = mPagerSnapHelper.findSnapView(mLinearLayoutManager);
        if (snapView == null) {
            return;
        }
        final int position = mLinearLayoutManager.getPosition(snapView);
        if (position < 0) {
            return;
        }

        mPlayView = snapView;
        mPlayPosition = position;
        final VideoDetailsAdapter.VideoViewHolder vh = (VideoDetailsAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
        vh.playTextureView.startPlayLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsPause) {
            mIsPause = false;
            playVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
        VideoDetailsAdapter.VideoViewHolder vh = (VideoDetailsAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
        vh.playTextureView.onVideoPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void initData() {
        mVideoList = VideoDataUtil.INSTANCE.getVideoList();
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        mContext = VideoPlayActivity.this;
        mVideoListRecyclerView = findViewById(R.id.rv_video_detail);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mVideoListRecyclerView.setLayoutManager(mLinearLayoutManager);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(mVideoListRecyclerView);
        mVideoListRecyclerView.setAdapter(new VideoDetailsAdapter(mContext, mVideoList));
        mVideoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPagerSnapHelper.findSnapView(mLinearLayoutManager) != mPlayView) {
                    playVideo();
                }
            }
        });

        mVideoListRecyclerView.post(() -> {
            mVideoListRecyclerView.scrollToPosition(mPlayPosition);
            mVideoListRecyclerView.post(() -> playVideo());
        });
    }

    @Override
    public void start() {
    }

    public void onShowSettingDialog(View view) {
        MediaParam setting = SharePrefUtils.getParam(this);
        CustomSettingDialog dialog = new CustomSettingDialog(this, setting);
        dialog.showDialog();
    }

    public void onCreateShortVideo(View view) {
        startActivity(new Intent(this, VideoRecordActivity.class));
    }
}
