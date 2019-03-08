package com.movieous.media.api.vendor.fusdk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.entity.Filter;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.FuFilterEnum;
import com.movieous.media.mvp.model.entity.MagicFilterItem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class FuSDKManager implements FilterSdkManager {
    private boolean mIsPreviewMode = true;

    private FURenderer mPreviewFilterEngine;
    private FURenderer mSaveFilterEngine;
    private Context mContext;
    private Effect mCurrentEffect;
    private boolean mReInit;

    public FuSDKManager(Context context) {
        mContext = context;
    }

    public static String[] getFilterTypeName() {
        return new String[]{
                "普通", "AR", "换脸", "表情", "背景替换", "手势", "哈哈镜", "3D", "人像", "抖音",
        };
    }

    public static ArrayList<MagicFilterItem> getMagicFilterList(int type) {
        ArrayList<Effect> mEffects = FuFilterEnum.Companion.getEffectsByEffectType(type);
        ArrayList<MagicFilterItem> items = new ArrayList<>();
        for (Effect effect : mEffects) {
            MagicFilterItem item = new MagicFilterItem(effect.bundleName(), effect.path());
            item.setVendor(FilterVendor.FU);
            item.setResId(effect.resId());
            item.setMaxFace(effect.maxFace());
            item.setType(effect.effectType());
            item.setDescription(effect.description());
            items.add(item);
        }
        return items;
    }

    /**
     * 获取预览 filter engine
     */
    public FURenderer getPreviewFilterEngine() {
        if (mPreviewFilterEngine == null) {
            mPreviewFilterEngine = createFilterEngine();
        }
        return mPreviewFilterEngine;
    }

    /**
     * 获取保存 filter engine
     */
    public FURenderer getSaveFilterEngine() {
        if (mSaveFilterEngine == null) {
            mSaveFilterEngine = createFilterEngine();
        }
        return mSaveFilterEngine;
    }

    /**
     * 销毁预览 filter engine
     */
    public void destroyPreviewFilterEngine() {
        if (mPreviewFilterEngine != null) {
            mPreviewFilterEngine.onSurfaceDestroyed();
            mPreviewFilterEngine = null;
        }
        mReInit = true;
    }

    /**
     * 销毁保存 filter engine
     */
    public void destroySaveFilterEngine() {
        if (mSaveFilterEngine != null) {
            mSaveFilterEngine.onSurfaceDestroyed();
            mSaveFilterEngine = null;
        }
    }

    private FURenderer createFilterEngine() {
        FURenderer filterEngine = new FURenderer.Builder(mContext).build();
        return filterEngine;
    }

    @Override
    public void init(@NotNull Context context, boolean isPreview) {
        getPreviewFilterEngine().loadItems();
    }

    @Override
    public synchronized void destroy() {
        if (mIsPreviewMode) {
            destroyPreviewFilterEngine();
        } else {
            destroySaveFilterEngine();
        }
    }

    @Override
    public void onSurfaceCreated() {
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onSurfaceCreated();
        } else {
            getSaveFilterEngine().onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        if (mReInit) {
            onSurfaceCreated();
        }
    }

    @Override
    public synchronized int onDrawFrame(int texId, int width, int height) {
        return mIsPreviewMode ?
                getPreviewFilterEngine().onDrawFrame(texId, width, height) :
                getSaveFilterEngine().onDrawFrame(texId, width, height);
    }

    @Override
    public void changeFilter(@NotNull MagicFilterItem filter) {
        Effect effect = new Effect(filter.getName(), filter.getResId(), filter.getPath(), filter.getMaxFace(), filter.getType(), filter.getDescription());
        if (mCurrentEffect != null && mCurrentEffect.bundleName().equals(effect.bundleName())) {
            //return;
        }
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onEffectSelected(effect);
        } else {
            getSaveFilterEngine().onEffectSelected(effect);
        }
        mCurrentEffect = effect;
        mStartTime = System.currentTimeMillis();
        //playMusic(effect);
    }

    @Override
    public synchronized void changeMusicFilterTime(long time) {
        if (mCurrentEffect != null && mCurrentEffect.effectType() == Effect.EFFECT_TYPE_MUSIC_FILTER) {
            //if (mediaPlayer.isPlaying()) {
            if (time < 0) {
                time = System.currentTimeMillis() - mStartTime + 10000;
                if (time > 20000) {
                    mStartTime = System.currentTimeMillis();
                }
            }

            if (mIsPreviewMode) {
                getPreviewFilterEngine().onMusicFilterTime(time);
            } else {
                getSaveFilterEngine().onMusicFilterTime(time);
            }
            //}
        }
    }

    @Override
    public void changeBeautyValue(float value, @NotNull BeautyParamEnum beautyType) {
        FURenderer filterEngine = mIsPreviewMode ? getPreviewFilterEngine() : getSaveFilterEngine();
        switch (beautyType) {
            case FACE_BLUR:
                filterEngine.onBlurLevelSelected(value);
                break;
            case EYE_ENLARGE:
                filterEngine.onEyeEnlargeSelected(value);
                break;
            case CHEEK_THINNING:
                filterEngine.onCheekThinningSelected(value);
                break;
        }
    }

    @Override
    public void changeBeautyFilter(@NotNull Filter filterName) {
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onFilterNameSelected(filterName);
        } else {
            getSaveFilterEngine().onFilterNameSelected(filterName);
        }
    }

    @Override
    public synchronized void clearAllFilters() {
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onEffectSelected(FuFilterEnum.Companion.getEffectsByEffectType(0).get(0));
        } else {
            getSaveFilterEngine().onEffectSelected(FuFilterEnum.Companion.getEffectsByEffectType(0).get(0));
        }
    }

    public void setPreviewMode(boolean isPreviewMode) {
        mIsPreviewMode = isPreviewMode;
    }

    // for music filter
    private MediaPlayer mediaPlayer;
    private Handler mMusicHandler;
    private static final int MUSIC_TIME = 50;
    private long mStartTime;
    private Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            changeMusicFilterTime(-1);
            mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
        }
    };

    public synchronized void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mMusicHandler.removeCallbacks(mMusicRunnable);
        }
    }

    public void playMusic(Effect effect) {
        if (mCurrentEffect.effectType() != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        stopMusic();

        if (effect.effectType() != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        mediaPlayer = new MediaPlayer();
        mMusicHandler = new Handler(Looper.getMainLooper());

        /**
         * mp3
         */
        try {
            AssetFileDescriptor descriptor = mContext.getAssets().openFd("musicfilter/" + effect.bundleName() + ".mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            });
            mediaPlayer.setOnPreparedListener(mp -> {
                // 装载完毕回调
                //mediaPlayer.setVolume(1f, 1f);
                mediaPlayer.setLooping(false);
                mediaPlayer.seekTo(0);
                mediaPlayer.start();

                mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            });
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean needReInit() {
        return mReInit;
    }
}
