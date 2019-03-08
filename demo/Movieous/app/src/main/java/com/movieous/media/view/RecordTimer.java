package com.movieous.media.view;

import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 录制计时器
 */
public abstract class RecordTimer {
    private long mStartTime;
    private final long mCountdownIntervalMs;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public RecordTimer(long countDownIntervalMs) {
        mCountdownIntervalMs = countDownIntervalMs;
    }

    /**
     * 取消计时
     */
    public final void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 开始计时
     *
     * @return
     */
    public synchronized final RecordTimer start() {
        mStartTime = SystemClock.elapsedRealtime();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                long now = SystemClock.elapsedRealtime();
                onTick(now - mStartTime);
            }
        };
        if (mTimer != null && mTimerTask != null) {
            mTimer.scheduleAtFixedRate(mTimerTask, mCountdownIntervalMs, mCountdownIntervalMs);
        }
        return this;
    }

    /**
     * 计时间隔回调
     *
     * @param progress
     */
    public abstract void onTick(long progress);

}
