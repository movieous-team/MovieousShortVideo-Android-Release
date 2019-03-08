package com.movieous.media.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.movieous.media.R;
import com.movieous.media.mvp.contract.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.List;

public class RangeSeekBar extends View {
    private int mHeightTimeLine;
    private float mMaxWidth;
    private float mThumbWidth;
    private float mThumbHeight;
    private int mViewWidth;
    private float mPixelRangeMin;
    private float mPixelRangeMax;
    private float mScaleRangeMax;
    private boolean mFirstRun;
    private final Paint mShadow = new Paint();
    private final Paint mLine = new Paint();
    private List<SeekBarHandler> mSeekBarHandlers;
    private List<OnSeekBarChangeListener> mListeners;
    private int currentThumb;

    public RangeSeekBar(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSeekBarHandlers = SeekBarHandler.initThumbs(getResources());
        mThumbWidth = SeekBarHandler.getWidthBitmap(mSeekBarHandlers);
        mThumbHeight = SeekBarHandler.getHeightBitmap(mSeekBarHandlers);
        mScaleRangeMax = 100;
        mHeightTimeLine = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);
        setFocusable(true);
        setFocusableInTouchMode(true);
        mFirstRun = true;
        int shadowColor = ContextCompat.getColor(getContext(), R.color.shadow_color);
        mShadow.setAntiAlias(true);
        mShadow.setColor(shadowColor);
        mShadow.setAlpha(177);
        int lineColor = ContextCompat.getColor(getContext(), R.color.line_color);
        mLine.setAntiAlias(true);
        mLine.setColor(lineColor);
        mLine.setAlpha(200);
    }

    public void initMaxWidth() {
        mMaxWidth = mSeekBarHandlers.get(1).getPos() - mSeekBarHandlers.get(0).getPos();
        onSeekStop(this, 0, mSeekBarHandlers.get(0).getVal());
        onSeekStop(this, 1, mSeekBarHandlers.get(1).getVal());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        mViewWidth = resolveSizeAndState(minW, widthMeasureSpec, 1);
        int minH = getPaddingBottom() + getPaddingTop() + (int) mThumbHeight;
        int viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1);
        setMeasuredDimension(mViewWidth, viewHeight);
        mPixelRangeMin = 0;
        mPixelRangeMax = mViewWidth - mThumbWidth;
        if (mFirstRun) {
            for (int i = 0; i < mSeekBarHandlers.size(); i++) {
                SeekBarHandler th = mSeekBarHandlers.get(i);
                th.setVal(mScaleRangeMax * i);
                th.setPos(mPixelRangeMax * i);
            }
            // Fire listener callback
            onCreate(this, currentThumb, getThumbValue(currentThumb));
            mFirstRun = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawShadow(canvas);
        drawThumbs(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final SeekBarHandler mSeekBarHandler;
        final SeekBarHandler mSeekBarHandler2;
        final float coordinate = ev.getX();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started
                currentThumb = getClosestThumb(coordinate);
                if (currentThumb == -1) {
                    return false;
                }
                mSeekBarHandler = mSeekBarHandlers.get(currentThumb);
                mSeekBarHandler.setLastTouchX(coordinate);
                onSeekStart(this, currentThumb, mSeekBarHandler.getVal());
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (currentThumb == -1) {
                    return false;
                }
                mSeekBarHandler = mSeekBarHandlers.get(currentThumb);
                onSeekStop(this, currentThumb, mSeekBarHandler.getVal());
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                mSeekBarHandler = mSeekBarHandlers.get(currentThumb);
                mSeekBarHandler2 = mSeekBarHandlers.get(currentThumb == 0 ? 1 : 0);
                // Calculate the distance moved
                final float dx = coordinate - mSeekBarHandler.getLastTouchX();
                final float newX = mSeekBarHandler.getPos() + dx;
                if (currentThumb == 0) {
                    if ((newX + mSeekBarHandler.getWidthBitmap()) >= mSeekBarHandler2.getPos()) {
                        mSeekBarHandler.setPos(mSeekBarHandler2.getPos() - mSeekBarHandler.getWidthBitmap());
                    } else if (newX <= mPixelRangeMin) {
                        mSeekBarHandler.setPos(mPixelRangeMin);
                        if ((mSeekBarHandler2.getPos() - (mSeekBarHandler.getPos() + dx)) > mMaxWidth) {
                            mSeekBarHandler2.setPos(mSeekBarHandler.getPos() + dx + mMaxWidth);
                            setThumbPos(1, mSeekBarHandler2.getPos());
                        }
                    } else {
                        if ((mSeekBarHandler2.getPos() - (mSeekBarHandler.getPos() + dx)) > mMaxWidth) {
                            mSeekBarHandler2.setPos(mSeekBarHandler.getPos() + dx + mMaxWidth);
                            setThumbPos(1, mSeekBarHandler2.getPos());
                        }
                        // Move the object
                        mSeekBarHandler.setPos(mSeekBarHandler.getPos() + dx);
                        // Remember this touch position for the next move event
                        mSeekBarHandler.setLastTouchX(coordinate);
                    }
                } else {
                    if (newX <= mSeekBarHandler2.getPos() + mSeekBarHandler2.getWidthBitmap()) {
                        mSeekBarHandler.setPos(mSeekBarHandler2.getPos() + mSeekBarHandler.getWidthBitmap());
                    } else if (newX >= mPixelRangeMax) {
                        mSeekBarHandler.setPos(mPixelRangeMax);
                        if (((mSeekBarHandler.getPos() + dx) - mSeekBarHandler2.getPos()) > mMaxWidth) {
                            mSeekBarHandler2.setPos(mSeekBarHandler.getPos() + dx - mMaxWidth);
                            setThumbPos(0, mSeekBarHandler2.getPos());
                        }
                    } else {
                        if (((mSeekBarHandler.getPos() + dx) - mSeekBarHandler2.getPos()) > mMaxWidth) {
                            mSeekBarHandler2.setPos(mSeekBarHandler.getPos() + dx - mMaxWidth);
                            setThumbPos(0, mSeekBarHandler2.getPos());
                        }
                        // Move the object
                        mSeekBarHandler.setPos(mSeekBarHandler.getPos() + dx);
                        // Remember this touch position for the next move event
                        mSeekBarHandler.setLastTouchX(coordinate);
                    }
                }
                setThumbPos(currentThumb, mSeekBarHandler.getPos());
                // Invalidate to request a redraw
                invalidate();
                return true;
            }
        }
        return false;
    }

    private void checkPositionThumb(@NonNull SeekBarHandler mSeekBarHandlerLeft, @NonNull SeekBarHandler mSeekBarHandlerRight, float dx, boolean isLeftMove, float coordinate) {
        if (isLeftMove && dx < 0) {
            if ((mSeekBarHandlerRight.getPos() - (mSeekBarHandlerLeft.getPos() + dx)) > mMaxWidth) {
                mSeekBarHandlerRight.setPos(mSeekBarHandlerLeft.getPos() + dx + mMaxWidth);
                setThumbPos(1, mSeekBarHandlerRight.getPos());
            }
        } else if (!isLeftMove && dx > 0) {
            if (((mSeekBarHandlerRight.getPos() + dx) - mSeekBarHandlerLeft.getPos()) > mMaxWidth) {
                mSeekBarHandlerLeft.setPos(mSeekBarHandlerRight.getPos() + dx - mMaxWidth);
                setThumbPos(0, mSeekBarHandlerLeft.getPos());
            }
        }
    }

    private float pixelToScale(int index, float pixelValue) {
        float scale = (pixelValue * 100) / mPixelRangeMax;
        if (index == 0) {
            float pxThumb = (scale * mThumbWidth) / 100;
            return scale + (pxThumb * 100) / mPixelRangeMax;
        } else {
            float pxThumb = ((100 - scale) * mThumbWidth) / 100;
            return scale - (pxThumb * 100) / mPixelRangeMax;
        }
    }

    private float scaleToPixel(int index, float scaleValue) {
        float px = (scaleValue * mPixelRangeMax) / 100;
        if (index == 0) {
            float pxThumb = (scaleValue * mThumbWidth) / 100;
            return px - pxThumb;
        } else {
            float pxThumb = ((100 - scaleValue) * mThumbWidth) / 100;
            return px + pxThumb;
        }
    }

    private void calculateThumbValue(int index) {
        if (index < mSeekBarHandlers.size() && !mSeekBarHandlers.isEmpty()) {
            SeekBarHandler th = mSeekBarHandlers.get(index);
            th.setVal(pixelToScale(index, th.getPos()));
            onSeek(this, index, th.getVal());
        }
    }

    private void calculateThumbPos(int index) {
        if (index < mSeekBarHandlers.size() && !mSeekBarHandlers.isEmpty()) {
            SeekBarHandler th = mSeekBarHandlers.get(index);
            th.setPos(scaleToPixel(index, th.getVal()));
        }
    }

    private float getThumbValue(int index) {
        return mSeekBarHandlers.get(index).getVal();
    }

    public void setThumbValue(int index, float value) {
        mSeekBarHandlers.get(index).setVal(value);
        calculateThumbPos(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private void setThumbPos(int index, float pos) {
        mSeekBarHandlers.get(index).setPos(pos);
        calculateThumbValue(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private int getClosestThumb(float coordinate) {
        int closest = -1;
        if (!mSeekBarHandlers.isEmpty()) {
            for (int i = 0; i < mSeekBarHandlers.size(); i++) {
                // Find thumb closest to x coordinate
                final float tcoordinate = mSeekBarHandlers.get(i).getPos() + mThumbWidth;
                if (coordinate >= mSeekBarHandlers.get(i).getPos() && coordinate <= tcoordinate) {
                    closest = mSeekBarHandlers.get(i).getIndex();
                }
            }
        }
        return closest;
    }

    private void drawShadow(@NonNull Canvas canvas) {
        if (!mSeekBarHandlers.isEmpty()) {
            for (SeekBarHandler th : mSeekBarHandlers) {
                if (th.getIndex() == 0) {
                    final float x = th.getPos();
                    if (x > mPixelRangeMin) {
                        Rect mRect = new Rect(0, (int) (mThumbHeight - mHeightTimeLine) / 2,
                                (int) (x + (mThumbWidth / 2)), mHeightTimeLine + (int) (mThumbHeight - mHeightTimeLine) / 2);
                        canvas.drawRect(mRect, mShadow);
                    }
                } else {
                    final float x = th.getPos();
                    if (x < mPixelRangeMax) {
                        Rect mRect = new Rect((int) (x + (mThumbWidth / 2)), (int) (mThumbHeight - mHeightTimeLine) / 2,
                                (mViewWidth), mHeightTimeLine + (int) (mThumbHeight - mHeightTimeLine) / 2);
                        canvas.drawRect(mRect, mShadow);
                    }
                }
            }
        }
    }

    private void drawThumbs(@NonNull Canvas canvas) {
        if (!mSeekBarHandlers.isEmpty()) {
            for (SeekBarHandler th : mSeekBarHandlers) {
                if (th.getIndex() == 0) {
                    canvas.drawBitmap(th.getBitmap(), th.getPos() + getPaddingLeft(), getPaddingTop(), null);
                } else {
                    canvas.drawBitmap(th.getBitmap(), th.getPos() - getPaddingRight(), getPaddingTop(), null);
                }
            }
        }
    }

    public void addOnRangeSeekBarListener(OnSeekBarChangeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    private void onCreate(RangeSeekBar rangeSeekBar, int index, float value) {
        if (mListeners == null) return;
        for (OnSeekBarChangeListener item : mListeners) {
            item.onCreate(rangeSeekBar, index, value);
        }
    }

    private void onSeek(RangeSeekBar rangeSeekBar, int index, float value) {
        if (mListeners == null) return;
        for (OnSeekBarChangeListener item : mListeners) {
            item.onSeek(rangeSeekBar, index, value);
        }
    }

    private void onSeekStart(RangeSeekBar rangeSeekBar, int index, float value) {
        if (mListeners == null) return;
        for (OnSeekBarChangeListener item : mListeners) {
            item.onSeekStart(rangeSeekBar, index, value);
        }
    }

    private void onSeekStop(RangeSeekBar rangeSeekBar, int index, float value) {
        if (mListeners == null) return;
        for (OnSeekBarChangeListener item : mListeners) {
            item.onSeekStop(rangeSeekBar, index, value);
        }
    }

    public List<SeekBarHandler> getThumbs() {
        return mSeekBarHandlers;
    }
}
