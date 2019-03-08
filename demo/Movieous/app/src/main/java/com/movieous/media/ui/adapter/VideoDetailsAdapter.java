package com.movieous.media.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.VideoListItem;
import com.movieous.media.utils.Utils;
import com.movieous.media.utils.StatusBarUtil;
import com.movieous.media.view.player.VideoPlayerView;
import com.movieous.media.view.player.VideoTouchView;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.ArrayList;

public class VideoDetailsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<VideoListItem> mVideoItemList;

    public VideoDetailsAdapter(Context mContext, ArrayList<VideoListItem> videoItemList) {
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
        VideoListItem videoItemBean = mVideoItemList.get(index);

        if (videoItemBean.getAvatarRes() > 0) {
            Glide.with(mContext).load(videoItemBean.getAvatarRes()).into(holder.iv_avatar);
        } else {
            Glide.with(mContext).load(videoItemBean.getAvatarUrl()).into(holder.iv_avatar);
        }

        holder.tv_content.setText(videoItemBean.getContent());
        holder.tv_name.setText(videoItemBean.getUserName());
        holder.playTextureView.setUp(videoItemBean.getVideoUrl(), false, "");
        holder.playTextureView.setLooping(true);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        holder.playTextureView.loadCoverImage(videoItemBean.getCoverUrl(), R.drawable.bc_background_play);
        //setVideoSize(vh, videoItemBean.getVideoWidth(), videoItemBean.getVideoHeight());
    }

    private void setVideoSize(VideoViewHolder vh, int videoWidth, int videoHeight) {
        float videoRatio = videoWidth * 1f / videoHeight;
        int windowWidth = Utils.getWindowWidth(mContext);
        int windowHeight = Utils.getWindowHeight(mContext) + StatusBarUtil.INSTANCE.getStatusHeight(mContext);
        float windowRatio = Utils.getWindowWidth(mContext) * 1f / Utils.getWindowHeight(mContext);
        ViewGroup.LayoutParams layoutParams = vh.videoTouchView.getLayoutParams();
        if (videoRatio >= windowRatio) {
            layoutParams.width = windowWidth;
            layoutParams.height = (int) (layoutParams.width / videoRatio);
        } else {
            layoutParams.height = windowHeight;
            layoutParams.width = (int) (layoutParams.height * videoRatio);
        }
        vh.videoTouchView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mVideoItemList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoTouchView videoTouchView;
        public VideoPlayerView playTextureView;
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_content;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoTouchView = itemView.findViewById(R.id.videoTouchView);
            playTextureView = itemView.findViewById(R.id.video_view);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
