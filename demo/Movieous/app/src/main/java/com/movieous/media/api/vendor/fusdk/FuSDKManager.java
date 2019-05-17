package com.movieous.media.api.vendor.fusdk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.entity.Filter;
import com.faceunity.utils.FileUtils;
import com.movieous.media.api.vendor.fusdk.entity.EffectEnum;
import com.movieous.media.api.vendor.fusdk.entity.FilterEnum;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.UFilter;
import iknow.android.utils.thread.BackgroundExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FuSDKManager implements FilterSdkManager {
    private static final String TAG = "FuSDKManager";

    private boolean mIsPreviewMode = true;
    private FURenderer mPreviewFilterEngine;
    private FURenderer mSaveFilterEngine;
    private Context mContext;
    private Effect mCurrentEffect;
    private boolean mReInit;

    public static void initFuSDKEnv(Context context) {
        Log.i(TAG, "init faceunity sdk env");
        Context sContext = context.getApplicationContext();
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
            @Override
            public void execute() {
                // 拷贝 assets 资源
                FileUtils.copyAssetsMagicPhoto(sContext);
                FileUtils.copyAssetsTemplate(sContext);
                FURenderer fuRenderer = new FURenderer.Builder(sContext).build();
                fuRenderer.loadItems();
                Log.i(TAG, "initFuSDKEnv is ok");
            }
        });
    }

    public FuSDKManager(Context context) {
        mContext = context;
    }

    public String[] getFilterTypeName() {
        return new String[]{
                "普通", "AR", "换脸", "表情", "背景替换", "手势", "PORTRAIT", "ANIMOJI", "人像", "哈哈镜", "抖音",
        };
    }

    public static List<UFilter> getFilterList() {
        List<UFilter> filterItemList = new ArrayList<>();
        ArrayList<Filter> beautyFilterList = FilterEnum.getFiltersByFilterType();
        for (Filter filter : beautyFilterList) {
            UFilter item = new UFilter(filter.filterName(), filter.description());
            item.setResId(filter.resId());
            filterItemList.add(item);
        }
        return filterItemList;
    }

    public ArrayList<UFilter> getMagicFilterList(int type) {
        ArrayList<Effect> mEffects = EffectEnum.getEffectsByEffectType(type);
        ArrayList<UFilter> items = new ArrayList<>();
        for (Effect effect : mEffects) {
            UFilter item = new UFilter(effect.bundleName(), effect.path());
            item.setVendor(FilterVendor.FACEUNITY);
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
        mSaveFilterEngine.setIsSyncLoadeBeautyBundle(true);
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
        if (isPreview) {
            getPreviewFilterEngine().loadItems();
        } else {
            getSaveFilterEngine().loadItems();
        }
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
        Log.i(TAG, "onSurfaceChanged");
    }

    @Override
    public synchronized int onDrawFrame(int texId, int width, int height) {
        if (mReInit) {
            mReInit = false;
            onSurfaceCreated();
        }
        return mIsPreviewMode ?
                getPreviewFilterEngine().onDrawFrame(texId, width, height) :
                getSaveFilterEngine().onDrawFrame(texId, width, height);
    }

    @Override
    public void changeFilter(@NotNull UFilter filter) {
        Log.i(TAG, "changeFilter: filter = " + filter.getName());
        Effect effect = new Effect(filter.getName(), filter.getResId(), filter.getPath(), filter.getMaxFace(), filter.getType(), filter.getDescription());
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onEffectSelected(effect);
        } else {
            getSaveFilterEngine().onEffectSelected(effect);
        }
        mCurrentEffect = effect;
        mStartTime = System.currentTimeMillis();
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
    public void changeBeautyFilter(@NotNull UFilter filterItem) {
        Filter filter = new Filter(filterItem.getName(), filterItem.getResId(), filterItem.getDescription());
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onFilterNameSelected(filter);
        } else {
            getSaveFilterEngine().onFilterNameSelected(filter);
        }
    }

    @Override
    public synchronized void clearAllFilters() {
        if (mIsPreviewMode) {
            getPreviewFilterEngine().onEffectSelected(EffectEnum.getEffectsByEffectType(0).get(0));
        } else {
            getSaveFilterEngine().onEffectSelected(EffectEnum.getEffectsByEffectType(0).get(0));
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

    @Override
    public ByteBuffer getRGBABuffer() {
        return null;
    }

    @Override
    public void setRGBABuffer(@NotNull ByteBuffer buffer) {
    }

    @Override
    public int getMusicFilterIndex() {
        return Effect.EFFECT_TYPE_MUSIC_FILTER;
    }
}
