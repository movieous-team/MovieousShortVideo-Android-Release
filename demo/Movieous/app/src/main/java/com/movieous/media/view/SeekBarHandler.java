package com.movieous.media.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import com.movieous.media.R;

import java.util.List;
import java.util.Vector;

public class SeekBarHandler {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private int mIndex;
    private float mVal;
    private float mPos;
    private int mWidthBitmap;
    private int mHeightBitmap;
    private float mLastTouchX;
    private Bitmap mBitmap;

    private SeekBarHandler() {
        mVal = 0;
        mPos = 0;
    }

    public int getIndex() {
        return mIndex;
    }

    private void setIndex(int index) {
        mIndex = index;
    }

    public float getVal() {
        return mVal;
    }

    public void setVal(float val) {
        mVal = val;
    }

    public float getPos() {
        return mPos;
    }

    public void setPos(float pos) {
        mPos = pos;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private void setBitmap(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;
        mWidthBitmap = bitmap.getWidth();
        mHeightBitmap = bitmap.getHeight();
    }

    @NonNull
    public static List<SeekBarHandler> initThumbs(Resources resources) {
        List<SeekBarHandler> seekBarHandlers = new Vector<>();
        for (int i = 0; i < 2; i++) {
            SeekBarHandler th = new SeekBarHandler();
            th.setIndex(i);
            if (i == 0) {
                int resImageLeft = R.drawable.ic_video_cutline;
                th.setBitmap(BitmapFactory.decodeResource(resources, resImageLeft));
            } else {
                int resImageRight = R.drawable.ic_video_cutline;
                th.setBitmap(BitmapFactory.decodeResource(resources, resImageRight));
            }
            seekBarHandlers.add(th);
        }
        return seekBarHandlers;
    }

    public static int getWidthBitmap(@NonNull List<SeekBarHandler> seekBarHandlers) {
        return seekBarHandlers.get(0).getWidthBitmap();
    }

    public static int getHeightBitmap(@NonNull List<SeekBarHandler> seekBarHandlers) {
        return seekBarHandlers.get(0).getHeightBitmap();
    }

    public float getLastTouchX() {
        return mLastTouchX;
    }

    public void setLastTouchX(float lastTouchX) {
        mLastTouchX = lastTouchX;
    }

    public int getWidthBitmap() {
        return mWidthBitmap;
    }

    private int getHeightBitmap() {
        return mHeightBitmap;
    }
}
