package com.movieous.media.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.movieous.media.Config;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.MediaParam;

import java.util.Map;

public class SharePrefUtils {

    private Context mContext;
    private String mFileName = "movieous";
    private static SharePrefUtils mInstance;

    public synchronized static SharePrefUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharePrefUtils(context.getApplicationContext());
        }
        return mInstance;
    }

    public synchronized static void save(Context context, MediaParam param) {
        SharePrefUtils pref = SharePrefUtils.getInstance(context);
        pref.saveStringValue(Config.FILTER_VENDOR, param.vendor.name());
        pref.saveBooleanValue(Config.VIDEO_SIZE_REMAIN, param.remainVideoSize);
        pref.saveIntValue(Config.VIDEO_WIDTH, param.width);
        pref.saveIntValue(Config.VIDEO_HEIGHT, param.height);
        pref.saveIntValue(Config.VIDEO_BITRATE, param.videoBitrate);
        pref.saveIntValue(Config.VIDEO_FPS, param.videoFrameRate);
        pref.saveIntValue(Config.AUDIO_SAMPLE_RATE, param.audioSampleRate);
        pref.saveIntValue(Config.AUDIO_BITRATE, param.audioBitrate);
        pref.saveIntValue(Config.AUDIO_CHANNEL, param.audioChannels);
        pref.saveBooleanValue(Config.MOVIEOUS_PLAYER, param.isMovieousPlayer);
    }

    public synchronized static MediaParam getParam(Context context) {
        SharePrefUtils pref = SharePrefUtils.getInstance(context);
        MediaParam param = new MediaParam();
        try {
            param.vendor = FilterVendor.valueOf(pref.getStringValueByKey(Config.FILTER_VENDOR, FilterVendor.FACEUNITY.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        param.remainVideoSize = pref.getBooleanValueByKey(Config.VIDEO_SIZE_REMAIN, true);
        param.width = pref.getIntValueByKey(Config.VIDEO_WIDTH, Config.DEFAULT_VIDEO_WIDTH);
        param.height = pref.getIntValueByKey(Config.VIDEO_HEIGHT, Config.DEFAULT_VIDEO_HEIGHT);
        param.videoBitrate = pref.getIntValueByKey(Config.VIDEO_BITRATE, Config.DEFAULT_VIDEO_BITRATE);
        param.videoFrameRate = pref.getIntValueByKey(Config.VIDEO_FPS, Config.DEFAULT_FRAME_RATE);
        param.audioSampleRate = pref.getIntValueByKey(Config.AUDIO_SAMPLE_RATE, Config.DEFAULT_AUDIO_SAMPLE_RATE);
        param.audioBitrate = pref.getIntValueByKey(Config.AUDIO_BITRATE, Config.DEFAULT_AUDIO_BITRATE);
        param.audioChannels = pref.getIntValueByKey(Config.AUDIO_CHANNEL, Config.DEFAULT_AUDIO_CHANNEL);
        param.isMovieousPlayer = pref.getBooleanValueByKey(Config.MOVIEOUS_PLAYER, true);
        return param;
    }

    public SharePrefUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void saveIntValue(String key, int value) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveLongValue(String key, long value) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void removeSharePreferences(String key) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.remove(key);
        editor.apply();
    }

    public boolean contains(String key) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.contains(key);
    }

    public Map<String, Object> getAllMap() {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return (Map<String, Object>) sharePre.getAll();
    }

    public Integer getIntValueByKey(String key) {
        return getIntValueByKey(key, -1);
    }

    public Integer getIntValueByKey(String key, int defaultValue) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.getInt(key, defaultValue);
    }

    public Long getLongValueByKey(String key) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.getLong(key, -1);
    }

    public void saveStringValue(String key, String value) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValueByKey(String key) {
        return getStringValueByKey(key, null);
    }

    public String getStringValueByKey(String key, String defaultValue) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.getString(key, defaultValue);
    }

    public Boolean getBooleanValueByKey(String key) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.getBoolean(key, false);
    }

    public Boolean getBooleanValueByKey(String key, boolean fallbackValue) {
        SharedPreferences sharePre = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        return sharePre.getBoolean(key, fallbackValue);
    }

    public Integer getIntValueAndRemoveByKey(String key) {
        Integer value = getIntValueByKey(key);
        removeSharePreferences(key);
        return value;
    }

}
