package com.movieous.media.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.movieous.media.R;
import com.movieous.media.mvp.model.VideoDataUtil;
import com.movieous.media.mvp.model.entity.MediaParam;
import com.movieous.media.mvp.model.entity.VideoListItem;
import com.movieous.media.ui.adapter.VideoItemAdapter;
import com.movieous.media.utils.SharePrefUtils;
import com.movieous.media.view.CustomSettingDialog;

import java.util.ArrayList;

/**
 * 仿抖音上下滑动播放
 */
public class VideoPlayActivity extends AppCompatActivity {
    private static final String TAG = "VideoPlayActivity";

    private Context mContext;
    private RecyclerView mVideoListRecyclerView;
    private VideoItemAdapter mVideoItemAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private PagerSnapHelper mPagerSnapHelper;
    private static ArrayList<VideoListItem> mVideoList;
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

        if (mPlayView != null) {
            final VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
            vh.videoView.setTag(vh.videoView.getVideoUri().toString());
            vh.videoView.stopPlayback();
        }

        mPlayView = snapView;
        mPlayPosition = position;
        final VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
        Log.i(TAG, "start play, url: " + vh.videoView.getVideoUri().toString());

        if (vh.videoView.getTag() == null || !vh.videoView.getTag().equals(vh.videoView.getVideoUri().toString())) {
            vh.videoView.start();
        } else {
            vh.videoView.restart();
        }
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
        if (mVideoListRecyclerView != null) {
            VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
            vh.videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoItemAdapter != null) {
            mVideoItemAdapter.release();
        }
        if (mVideoListRecyclerView != null) {
            mVideoListRecyclerView.setAdapter(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mVideoList = VideoDataUtil.INSTANCE.getVideoList();
        initView();
    }

    private void initView() {
        mContext = VideoPlayActivity.this;
        setContentView(R.layout.activity_video_detail);
        mVideoListRecyclerView = findViewById(R.id.rv_video_detail);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mVideoListRecyclerView.setLayoutManager(mLinearLayoutManager);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(mVideoListRecyclerView);
        mVideoItemAdapter = new VideoItemAdapter(mContext, mVideoList);
        mVideoListRecyclerView.setAdapter(mVideoItemAdapter);
        mVideoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPagerSnapHelper.findSnapView(mLinearLayoutManager) != mPlayView) {
                    playVideo();
                }
            }
        });

        if (mVideoList != null && !mVideoList.isEmpty()) {
            mVideoListRecyclerView.post(() -> {
                mVideoListRecyclerView.scrollToPosition(mPlayPosition);
                mVideoListRecyclerView.post(() -> playVideo());
            });
        }
    }

    // 显示参数设置窗口
    public void onShowSettingDialog(View view) {
        MediaParam param = SharePrefUtils.getParam(this);
        CustomSettingDialog dialog = new CustomSettingDialog(this, param);
        dialog.showDialog();
    }

    // 进入短视频录制
    public void onCreateShortVideo(View view) {
        startActivity(new Intent(this, VideoRecordActivity.class));
    }

}
