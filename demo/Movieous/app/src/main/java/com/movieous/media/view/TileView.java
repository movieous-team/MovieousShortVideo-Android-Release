package com.movieous.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.View;
import iknow.android.utils.thread.UiThreadExecutor;
import video.movieous.media.base.callback.SingleCallback;
import video.movieous.shortvideo.UMediaUtil;

public class TileView extends View {
    private Uri mVideoUri;
    private long mDurationMs;
    private LongSparseArray<Bitmap> mBitmapList = new LongSparseArray<>();
    private int viewWidth = 0;
    private int viewHeight = 0;

    public TileView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TileView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minW, widthMeasureSpec, 1);
        final int minH = getPaddingBottom() + getPaddingTop() + viewHeight;
        int h = resolveSizeAndState(minH, heightMeasureSpec, 1);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        viewWidth = w;
        viewHeight = h;
        if (w != oldW && mVideoUri != null) {
            getThumbList();
        }
    }

    private void getThumbList() {
        final int thumbWidth = viewHeight;
        final int thumbHeight = viewHeight;
        int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
        UMediaUtil.getVideoThumb(mVideoUri, numThumbs, 0, mDurationMs, thumbWidth, thumbHeight,
                (SingleCallback<Bitmap, Long>) (bitmap, pts) -> {
                    if (bitmap != null) {
                        UiThreadExecutor.runTask("", () -> {
                            mBitmapList.put(mBitmapList.size(), bitmap);
                            invalidate();
                        }, 0L);
                    }
                });
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapList != null) {
            canvas.save();
            int x = 0;
            for (int i = 0; i < mBitmapList.size(); i++) {
                Bitmap bitmap = mBitmapList.get(i);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x, 0, null);
                    x = x + bitmap.getWidth();
                }
            }
        }
    }

    public void setVideoPath(String path, long durationMs) {
        mVideoUri = Uri.parse(path);
        mDurationMs = durationMs > 0 ? durationMs : UMediaUtil.getMetadata(path).duration;
    }

}
