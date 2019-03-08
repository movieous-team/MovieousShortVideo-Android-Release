package com.movieous.media.mvp.model.entity;

import android.graphics.Bitmap;

public class VideoFrameBean {

    private VideoFrameBean mPrev;

    private VideoFrameBean mNext;

    private Bitmap mBitmap;

    private long mFrameTime;

    public void setPrev(VideoFrameBean prev) {
        mPrev = prev;
    }

    public VideoFrameBean prev() {
        return mPrev;
    }

    public void setNext(VideoFrameBean next) {
        mNext = next;
    }

    public VideoFrameBean next() {
        return mNext;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setFrameTime(long frameTime) {
        mFrameTime = frameTime;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public long getFrameTime() {
        return mFrameTime;
    }

    public boolean isValid() {
        return mBitmap != null && !mBitmap.isRecycled();
    }

    @Override
    public String toString() {
        return "VideoFrameBean{" +
                "mFrameTime=" + mFrameTime +
                '}';
    }
}
