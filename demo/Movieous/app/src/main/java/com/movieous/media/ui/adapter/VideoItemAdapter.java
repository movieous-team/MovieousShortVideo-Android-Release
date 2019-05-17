package com.movieous.media.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.VideoListItem;
import video.movieous.droid.player.ui.widget.VideoView;

import java.util.ArrayList;

public class VideoItemAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<VideoListItem> mVideoItemList;

    public VideoItemAdapter(Context mContext, ArrayList<VideoListItem> videoItemList) {
        this.mContext = mContext;
        this.mVideoItemList = videoItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(View.inflate(mContext, R.layout.item_video_details, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VideoViewHolder holder = (VideoViewHolder) viewHolder;
        int index = mVideoItemList.size() - 1 - position;
        VideoListItem videoItem = mVideoItemList.get(index);

        if (videoItem.getAvatarRes() > 0) {
            Glide.with(mContext).load(videoItem.getAvatarRes()).into(holder.iv_avatar);
        } else {
            Glide.with(mContext).load(videoItem.getAvatarUrl()).into(holder.iv_avatar);
        }

        // 视频封面
        if (!TextUtils.isEmpty(videoItem.getCoverUrl())) {
            Glide.with(mContext.getApplicationContext())
                    .load(videoItem.getCoverUrl())
                    .into(holder.videoView.getPreviewImageView());
        }

        holder.tv_content.setText(videoItem.getContent());
        holder.tv_name.setText(videoItem.getUserName());
        holder.videoView.setVideoPath(videoItem.getVideoUrl());
        holder.videoView.setRepeatMode(Player.REPEAT_MODE_ALL);
        holder.videoView.setReleaseOnDetachFromWindow(false);
    }

    @Override
    public int getItemCount() {
        return mVideoItemList != null ? mVideoItemList.size() : 0;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_content;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.video_view);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
