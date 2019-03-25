package com.movieous.media.mvp.model.entity;

public class MediaParam {
    // vendor
    public FilterVendor vendor = FilterVendor.FACEUNITY;

    // video
    public int width = 360;
    public int height = 640;
    public int videoBitrate = 400;
    public int videoFrameRate = 15;
    public int videoGop = 30;

    // audio
    public int audioSampleRate = 44100;
    public int audioBitrate = 48;
    public int audioChannels = 1;

    @Override
    public String toString() {
        return "rtc setting: video: width=" + width + ", height=" + height + ", bitrate=" + videoBitrate
                + ", fps=" + videoFrameRate + ", gop=" + videoGop
                + ", audio: samplerate=" + audioSampleRate + ", bitrate=" + audioBitrate + ", channel=" + audioChannels;
    }
}
