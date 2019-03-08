package com.movieous.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.movieous.media.R;

public class FrameSelectorView extends RelativeLayout {
    private View mHandlerBody;
    private ImageView mHandlerStart;
    private ImageView mHandlerEnd;
    private FrameLayout.LayoutParams mGroupLayoutParam;
    private SelectorChangedListener mSelectorChangedListener;
    private ViewGroup.LayoutParams mOriginParam;

    private int mOriginWidth;
    private int mOriginLeftMargin;
    private float mOriginX;
    private boolean mIsTouching;

    public FrameSelectorView(Context context) {
        this(context, null);
    }

    public FrameSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.frame_selector_view, this);
        mHandlerStart = view.findViewById(R.id.handler_left);
        mHandlerEnd = view.findViewById(R.id.handler_right);
        mHandlerBody = view.findViewById(R.id.handler_body);
        mHandlerStart.setOnTouchListener(new HandlerLeftTouchListener());
        mHandlerEnd.setOnTouchListener(new HandlerRightTouchListener());
        mHandlerBody.setOnTouchListener(new HandlerBodyTouchListener());
        post(() -> mGroupLayoutParam = (FrameLayout.LayoutParams) getLayoutParams());
    }

    public interface SelectorChangedListener {
        void onSelectorChanged();
    }

    public void setSelectorChangedListener(SelectorChangedListener listener) {
        mSelectorChangedListener = listener;
    }

    private void onSelectorChanged() {
        if (mSelectorChangedListener != null) {
            mSelectorChangedListener.onSelectorChanged();
        }
    }

    private class HandlerLeftTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mIsTouching) {
                    return false;
                }
                mOriginX = event.getRawX();
                mOriginWidth = mHandlerBody.getWidth();
                mOriginParam = mHandlerBody.getLayoutParams();
                mOriginLeftMargin = mGroupLayoutParam.leftMargin;
                mIsTouching = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int delta = (int) (event.getRawX() - mOriginX);
                mOriginParam.width = mOriginWidth - delta;
                mHandlerBody.setLayoutParams(mOriginParam);
                mGroupLayoutParam.leftMargin = mOriginLeftMargin + delta;
                setLayoutParams(mGroupLayoutParam);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                onSelectorChanged();
            }
            return true;
        }
    }

    private class HandlerRightTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mIsTouching) {
                    return false;
                }
                mOriginX = event.getRawX();
                mOriginWidth = mHandlerBody.getWidth();
                mOriginParam = mHandlerBody.getLayoutParams();
                mIsTouching = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int delta = (int) (event.getRawX() - mOriginX);
                mOriginParam.width = mOriginWidth + delta;
                mHandlerBody.setLayoutParams(mOriginParam);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                onSelectorChanged();
            }
            return true;
        }
    }

    private class HandlerBodyTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mIsTouching) {
                    return false;
                }
                mOriginX = event.getRawX();
                mOriginLeftMargin = mGroupLayoutParam.leftMargin;
                mIsTouching = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int delta = (int) (event.getRawX() - mOriginX);
                mGroupLayoutParam.leftMargin = mOriginLeftMargin + delta;
                setLayoutParams(mGroupLayoutParam);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                onSelectorChanged();
            }
            return true;
        }
    }

    public int getBodyLeft() {
        return mGroupLayoutParam.leftMargin + mHandlerStart.getWidth();
    }

    public int getLeftHandlerWidth() {
        return mHandlerStart.getWidth();
    }

    public int getBodyWidth() {
        return mHandlerBody.getWidth();
    }

    public int getBodyRight() {
        return getBodyLeft() + mHandlerBody.getWidth();
    }

    public void setBodyLeft(int left) {
        mGroupLayoutParam.leftMargin = left;
        setLayoutParams(mGroupLayoutParam);
    }

    public void setBodyWidth(int width) {
        mOriginParam = mHandlerBody.getLayoutParams();
        mOriginParam.width = width;
        mHandlerBody.setLayoutParams(mOriginParam);
    }
}
