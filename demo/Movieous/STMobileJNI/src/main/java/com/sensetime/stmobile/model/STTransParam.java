package com.sensetime.stmobile.model;

/**
 * Created by sensetime on 18-5-8.
 */

public class STTransParam {
    private int fadeFrame;                // 需要多少帧渐变，当target是playing，first frame和invisible时有用；
    private int delayFrame;               // 延迟多少帧进行转换，都有用
    private int lastingFrame;             // 在当前状态下持续多少帧自动切换到下一状态，对first frame，last frame和pause有用
    private int playloop;                 // 播放多少个循环，对play有用

    public int getFadeFrame() {
        return fadeFrame;
    }

    public void setFadeFrame(int fadeFrame) {
        this.fadeFrame = fadeFrame;
    }

    public int getDelayFrame() {
        return delayFrame;
    }

    public void setDelayFrame(int delayFrame) {
        this.delayFrame = delayFrame;
    }

    public int getLastingFrame() {
        return lastingFrame;
    }

    public void setLastingFrame(int lastingFrame) {
        this.lastingFrame = lastingFrame;
    }

    public int getPlayloop() {
        return playloop;
    }

    public void setPlayloop(int playloop) {
        this.playloop = playloop;
    }

}
