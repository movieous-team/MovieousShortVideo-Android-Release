package com.sensetime.stmobile;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

public class STSoundPlay {
    private static String TAG = "STSoundPlay";

    private final String CACHED_FOLDER = "Audio";
    private String mCachedPath;
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private HashMap<String, SoundMetaData> mSoundMetaDataMap = new HashMap<>();
    private String mCurrentPlaying;

    private WeakReference<STMobileStickerNative> stickerHandleRef;
    private AudioManager mAudioManager;

    private static class SoundMetaData {
        String name;
        String cachePath;
        int loop;
    }

    /**
     * 音频播放监听器
     */
    public interface PlayControlListener {
        /**
         * 加载音频素材callback
         *
         * @param name    音频名称
         * @param content 音频内容
         */
        void onSoundLoaded(String name, byte[] content);

        /**
         * 播放音频callback
         *
         * @param name 音频名称
         * @param loop 循环次数，0表示无限循环，直到onStopPlay回调，大于0表示循环次数
         */
        void onStartPlay(String name, int loop);

        /**
         * 停止播放callback
         *
         * @param name 音频名称
         */
        void onStopPlay(String name);

        /**
         * 暂停播放callback
         *
         * @param name 音频名称
         */
        void onSoundPause(String name);

        /**
         * 重新播放callback
         *
         * @param name 音频名称
         */
        void onSoundResume(String name);
    }

    /**
     * SenseArPlay初始化
     *
     * @param appContext application context
     * @return
     */
    public STSoundPlay(Context appContext) {
        mContext = appContext.getApplicationContext();
        mCachedPath = mContext.getExternalCacheDir() + File.separator + CACHED_FOLDER;
        mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        File file = new File(mCachedPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        initMediaPlayer();
    }

    public void setStickHandle(STMobileStickerNative stickHandle) {
        stickerHandleRef = new WeakReference<STMobileStickerNative>(stickHandle);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(afChangeListener);
        }
    }

    /**
     * 设置播放控制监听器
     *
     * @param listener listener为null，SDK默认处理，若不为null，用户自行处理
     */
    public void setPlayControlListener(PlayControlListener listener) {
        if (listener != null) {
            mPlayControlDefaultListener = listener;
        }
    }

    /**
     * 设置音频播放完成标志
     *
     * @param name 音频名称
     */
    public void setSoundPlayDone(String name) {
        if (stickerHandleRef.get() != null) {
            stickerHandleRef.get().setSoundPlayDone(name);
        }
    }


    //===========================================================================================================
    //JNI调用，不做混淆
    private void onSoundLoaded(String name, byte[] content) {
        if (mPlayControlDefaultListener != null) {
            mPlayControlDefaultListener.onSoundLoaded(name, content);
        }
    }

    //JNI调用，不做混淆
    private void onStartPlay(String name, int loop) {
        if (mPlayControlDefaultListener != null) {
            mPlayControlDefaultListener.onStartPlay(name, loop);
        }
    }

    //JNI调用，不做混淆
    private void onStopPlay(String name) {
        if (mPlayControlDefaultListener != null) {
            mPlayControlDefaultListener.onStopPlay(name);
        }
    }

    //JNI调用，不做混淆
    private void onSoundPause(String name) {
        if (mPlayControlDefaultListener != null) {
            mPlayControlDefaultListener.onSoundPause(name);
        }
    }

    //JNI调用，不做混淆
    private void onSoundResume(String name) {
        if (mPlayControlDefaultListener != null) {
            mPlayControlDefaultListener.onSoundResume(name);
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //mediaPlayer.setOnPreparedListener(mPreparedListener);
        mediaPlayer.setOnCompletionListener(mCompletionListener);
        mediaPlayer.setOnErrorListener(mErrorListener);
        mediaPlayer.reset();
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer arg0) {
            try {
                SoundMetaData data = mSoundMetaDataMap.get(mCurrentPlaying);
                if (data != null && --data.loop > 0) {
                    Log.e(TAG, "loop " + data.loop);
                    mediaPlayer.start();
                } else {
                    //play end
                    Log.e(TAG, "play done");
                    setSoundPlayDone(data.name);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT reset");
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                //mAudioManager.abandonAudioFocus(afChangeListener);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.e(TAG, "AUDIOFOCUS_GAIN");
                if(mediaPlayer!= null && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.e(TAG, "AUDIOFOCUS_LOSS reset");
////                SoundMetaData data = mSoundMetaDataMap.get(mCurrentPlaying);
////                setSoundPlayDone(data.name);
//                if(mediaPlayer.isPlaying()){
//                    mediaPlayer.stop();
//                }
//                mediaPlayer.reset();
//                mAudioManager.abandonAudioFocus(afChangeListener);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG, "MediaPlayer error: " + what + ";" + extra);
            return true;
        }
    };

    private PlayControlListener mPlayControlDefaultListener = new PlayControlListener() {
        @Override
        public void onSoundLoaded(String name, byte[] content) {
            if (name == null)
                return;
            Log.e(TAG, "onSoundLoaded " + name);
            String soundFilePath = saveSoundToFile(name, content);
            if (soundFilePath != null) {
                SoundMetaData data = mSoundMetaDataMap.get(name);
                if (data == null) {
                    data = new SoundMetaData();
                }
                data.cachePath = soundFilePath;
                data.name = name;
                mSoundMetaDataMap.put(name, data);
            } else {
                Log.e(TAG, "SoundFilePath is null");
                return;
            }
        }

        @Override
        public void onStartPlay(String name, int loop) {
            SoundMetaData data = mSoundMetaDataMap.get(name);
            if (data == null) {
                Log.e(TAG, "No meta-data when start");
                return;
            }
            data.loop = loop;
            Log.e(TAG, "onStartPlay " + name);

            if (mediaPlayer.isPlaying()) {
                Log.e(TAG, "Stop it before play");
                setSoundPlayDone(mCurrentPlaying);
                mediaPlayer.reset();
            }

            try {
                mediaPlayer.setDataSource(mCachedPath + File.separator + name);
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e(TAG, "IOException:" + e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Log.e(TAG, "IllegalStateException:" + e.toString());
                e.printStackTrace();
            }

            mCurrentPlaying = name;
            if(0 == loop) {
                //loop forever
                mediaPlayer.setLooping(true);
            }

            mediaPlayer.start();
//            if (requestFocus()) {
//                if(0 == loop) {
//                    //loop forever
//                    mediaPlayer.setLooping(true);
//                }
//
//                mediaPlayer.start();
//            } else {
//                Log.e(TAG, "Can not get audio focus");
//                mediaPlayer.reset();
//            }
        }

        @Override
        public void onStopPlay(String name) {
            Log.e(TAG, "onStopPlay " + name);
            SoundMetaData data = mSoundMetaDataMap.get(name);
            if (data == null || !name.equals(mCurrentPlaying)) {
                Log.e(TAG, "No meta-data when stop");
                return;
            }

            if (mediaPlayer.isPlaying()) {
                Log.e(TAG, "Playing when onStopPlay callback");
                //todo
                //mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }

        @Override
        public void onSoundPause(String name) {
            Log.e(TAG, "onSoundPause " + name);
            if (!name.equals(mCurrentPlaying)) {
                Log.e(TAG, "No meta-data when stop");
                return;
            }

            if (mediaPlayer.isPlaying()) {
                Log.e(TAG, "Playing when onStopPlay callback");
                //todo
                mediaPlayer.pause();
            }
        }

        @Override
        public void onSoundResume(String name) {
            Log.e(TAG, "onStopPlay " + name);
            if (name.equals(mCurrentPlaying)) {
                Log.e(TAG, "No meta-data when stop");
                mediaPlayer.start();
            }
        }
    };

    private boolean requestFocus() {
        int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private String saveSoundToFile(String name, byte[] content) {
        File dir = new File(mCachedPath);
        boolean dirExist = true;
        if (!dir.exists()) {
            dirExist = dir.mkdirs();
        }
        if (!dirExist) {
            Log.e(TAG, mCachedPath + " is not exist");
            return null;
        }

        File file = null;
        try {
            file = new File(dir.getPath() + File.separator + name);
            FileOutputStream outputStream = new FileOutputStream(file, false);
            outputStream.write(content);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            file = null;
            Log.e(TAG, "write file failed:" + e.toString());
        }

        if (file != null) {
            return file.getAbsolutePath();
        }

        return null;
    }
}
