package com.movieous.media.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import com.movieous.media.mvp.model.entity.MusicFileItem;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public static int getRandomNum(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));

            final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
            StringBuilder ret = new StringBuilder(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
                ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
            }
            return ret.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 分秒
     *
     * @param time ms
     */
    public static String fromMMss(long time) {
        if (time < 0) {
            return "00:00";
        }

        int ss = (int) (time / 1000);
        int mm = ss / 60;
        int s = ss % 60;
        int m = mm % 60;
        String strM = String.valueOf(m);
        String strS = String.valueOf(s);
        if (m < 10) {
            strM = "0" + strM;
        }
        if (s < 10) {
            strS = "0" + strS;
        }
        return strM + ":" + strS;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static ArrayList<MusicFileItem> getMusicList(Context context) {
        ContentResolver ctResolver = context.getContentResolver();
        Cursor cursor = ctResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        ArrayList<MusicFileItem> musicFileList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                MusicFileItem musicFileItem = new MusicFileItem();
                musicFileItem.setTitle(title);
                musicFileItem.setArtist(artist);
                musicFileItem.setPath(path);
                musicFileList.add(musicFileItem);
            } while (cursor.moveToNext());
        }
        return musicFileList;
    }
}
