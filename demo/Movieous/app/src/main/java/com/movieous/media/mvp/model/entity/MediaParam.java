package com.movieous.media.mvp.model.entity;

public class MediaParam {
    // vendor
    public FilterVendor vendor = FilterVendor.FACEUNITY;

    // video
    public boolean remainVideoSize;
    public int width;
    public int height;
    public int videoBitrate;
    public int videoFrameRate;

    // audio
    public int audioSampleRate;
    public int audioBitrate;
    public int audioChannels;

    // player
    public boolean isMovieousPlayer;

    @Override
    public String toString() {
        return "rtc setting: video: width=" + width + ", height=" + height + ", bitrate=" + videoBitrate + ", fps=" + videoFrameRate
                + ", audio: samplerate=" + audioSampleRate + ", bitrate=" + audioBitrate + ", channel=" + audioChannels + ", movieousPlayer=" + isMovieousPlayer;
    }
}
