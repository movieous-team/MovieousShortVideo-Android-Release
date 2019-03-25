package com.movieous.media.api.vendor.stsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;
import com.movieous.media.R;
import com.movieous.media.api.vendor.stsdk.glutils.GlUtil;
import com.movieous.media.api.vendor.stsdk.utils.FileUtils;
import com.movieous.media.api.vendor.stsdk.utils.STLicenseUtils;
import com.movieous.media.api.vendor.stsdk.view.FilterItem;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.UFilter;
import com.sensetime.stmobile.*;
import com.sensetime.stmobile.model.STHumanAction;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StSDKManager implements FilterSdkManager {
    private static final String TAG = "StSDKManager";
    private static boolean mIsEnabled = true;

    private Context mContext;
    private Object mImageDataLock = new Object();
    private Object mHumanActionHandleLock = new Object();
    private float[] mBeautifyParams = {0.36f, 0.74f, 0.02f, 0.13f, 0.11f, 0.1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private STBeautifyNative mStBeautifyNative = new STBeautifyNative();
    private STMobileStreamFilterNative mSTMobileStreamFilterNative = new STMobileStreamFilterNative();
    private STMobileStickerNative mStStickerNative = new STMobileStickerNative();
    private STMobileHumanActionNative mSTHumanActionNative = new STMobileHumanActionNative();
    private STHumanAction mHumanActionBeautyOutput = new STHumanAction();
    private String mCurrentSticker;
    private String mCurrentFilterStyle;
    private float mCurrentFilterStrength = 0.65f;//阈值为[0,1]
    private float mFilterStrength = 0.65f;
    private String mFilterStyle;
    private long mDetectConfig;
    private int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO;
    private int[] mBeautifyTextureId;
    private int[] mFilterTextureOutId;
    private int[] mTextureOutId;
    private byte[] mImageData;
    private boolean mNeedBeautify = true;
    private boolean mNeedSticker = false;
    private boolean mNeedFilter = true;
    private boolean mIsCreateHumanActionHandleSucceeded;

    private ByteBuffer mRGBABuffer;

    public static void initStSDKEnv(Context context) {
        Log.i(TAG, "init sensetime sdk env");
        Context appContext = context.getApplicationContext();
        if (!STLicenseUtils.checkLicense(appContext)) {
            // License 过期
            Log.w(TAG, "SenseTime: 请检查License授权！");
        }
        // sensetime 资源文件
        for (String index : StSDKManager.STICKER_LIST) {
            FileUtils.copyStickerFiles(appContext, index);
        }
    }

    public static final String[] STICKER_LIST = new String[]{
            "new", "newEngine", "deformation", "3D", "particle", "avatar"
    };

    public StSDKManager(Context context) {
        mContext = context.getApplicationContext();
        initHumanAction();
    }

    public String[] getFilterTypeName() {
        return new String[]{
                "最新", "新引擎", "哈哈镜", "3D", "粒子", "avatar"
        };
    }

    public ArrayList<UFilter> getMagicFilterList(int type) {
        return getStickerFiles(mContext, STICKER_LIST[type - 1]);
    }

    public static List<UFilter> getFilterList(Context context) {
        List<UFilter> filterList = new ArrayList<>();
        ArrayList<FilterItem> filters = FileUtils.getFilterFiles(context, "filter_portrait");
        for (FilterItem filterItem : filters) {
            String path = filterItem.model == null ? "" : filterItem.model;
            UFilter filter = new UFilter(filterItem.name, filterItem.icon, path);
            filterList.add(filter);
        }
        return filterList;
    }

    public void enableBeautify(boolean needBeautify) {
        mNeedBeautify = needBeautify;
        setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
    }

    private void enableSticker(boolean needSticker) {
        mNeedSticker = needSticker;
        //reset humanAction config
        setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
    }

    public void enableFilter(boolean needFilter) {
        mNeedFilter = needFilter;
    }

    private void changeSticker(String stickerPath) {
        mCurrentSticker = stickerPath;
        int result = mStStickerNative.changeSticker(mCurrentSticker);
        Log.i(TAG, "change sticker result: " + result + ", path = " + stickerPath);
        setHumanActionDetectConfig(mNeedSticker, mStStickerNative.getTriggerAction());
    }

    public void removeAllStickers() {
        mStStickerNative.removeAllStickers();
        setHumanActionDetectConfig(mNeedSticker, mStStickerNative.getTriggerAction());
    }

    public ByteBuffer getRGBABuffer() {
        return mRGBABuffer;
    }

    public void setRGBABuffer(ByteBuffer buffer) {
        mRGBABuffer = buffer;
    }

    public boolean onPreviewFrame(byte[] data, int width, int height, int rotation, int format, long timestampNs) {
        if (mImageData == null || mImageData.length != width * height * 3 / 2) {
            mImageData = new byte[width * height * 3 / 2];
        }
        synchronized (mImageDataLock) {
            System.arraycopy(data, 0, mImageData, 0, data.length);
        }
        return true;
    }

    public void onPause() {
        Log.i(TAG, "onPause");
        mSTHumanActionNative.reset();
        mStStickerNative.removeAvatarModel();
        mStStickerNative.destroyInstance();
        if (mTextureOutId != null) {
            GLES20.glDeleteTextures(1, mTextureOutId, 0);
            mTextureOutId = null;
        }
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view创建的时候调用
     */
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        //初始化GL相关的句柄，包括美颜，贴纸，滤镜
        initBeauty();
        initSticker();
        initFilter();
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view尺寸改变的时候调用
     *
     * @param width
     * @param height
     */
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * 工作在opengl线程, 具体渲染的工作函数
     */
    public int onDrawFrame(int texId, int width, int height) {
        if (!mIsEnabled) return texId;

        int orientation = STRotateType.ST_CLOCKWISE_ROTATE_180;

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mRGBABuffer == null) {
            int size = width * height * 4;
            Log.i(TAG, "mRGBABuffer: allocate: " + size);
            mRGBABuffer = ByteBuffer.allocate(size);
            return texId;
        }

        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            GlUtil.initEffectTexture(width, height, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mTextureOutId == null) {
            mTextureOutId = new int[1];
            GlUtil.initEffectTexture(width, height, mTextureOutId, GLES20.GL_TEXTURE_2D);
        }

        if (mNeedBeautify || mNeedSticker) {
            STHumanAction humanAction = null;
            if (mIsCreateHumanActionHandleSucceeded) {
                humanAction = mSTHumanActionNative.humanActionDetect(mRGBABuffer.array(), STCommon.ST_PIX_FMT_RGBA8888, mDetectConfig, orientation, width, height);
            }

            //美颜
            if (mNeedBeautify) {// do beautify
                int result = mStBeautifyNative.processTexture(texId, width, height, orientation, humanAction, mBeautifyTextureId[0], mHumanActionBeautyOutput);
                if (result == 0) {
                    texId = mBeautifyTextureId[0];
                    humanAction = mHumanActionBeautyOutput;
                }
            }

            // 贴纸
            if (mNeedSticker) {
                /**
                 * 1.在切换贴纸时，调用STMobileStickerNative的changeSticker函数，传入贴纸路径(参考setShowSticker函数的使用)
                 * 2.切换贴纸后，使用STMobileStickerNative的getTriggerAction函数获取当前贴纸支持的手势和前后背景等信息，返回值为int类型
                 * 3.根据getTriggerAction函数返回值，重新配置humanActionDetect函数的config参数，使detect更高效
                 *
                 * 例：只检测人脸信息和当前贴纸支持的手势等信息时，使用如下配置：
                 * mDetectConfig = mSTMobileStickerNative.getTriggerAction()|STMobileHumanActionNative.ST_MOBILE_FACE_DETECT;
                 */
                int result = mStStickerNative.processTexture(texId, humanAction, orientation, orientation, width, height, true, null, mTextureOutId[0]);
                if (result == 0) {
                    texId = mTextureOutId[0];
                }
            }

            if (mCurrentFilterStyle != mFilterStyle) {
                mCurrentFilterStyle = mFilterStyle;
                mSTMobileStreamFilterNative.setStyle(mCurrentFilterStyle);
            }
            if (mCurrentFilterStrength != mFilterStrength) {
                mCurrentFilterStrength = mFilterStrength;
                mSTMobileStreamFilterNative.setParam(STFilterParamsType.ST_FILTER_STRENGTH, mCurrentFilterStrength);
            }

            if (mFilterTextureOutId == null) {
                mFilterTextureOutId = new int[1];
                GlUtil.initEffectTexture(width, height, mFilterTextureOutId, GLES20.GL_TEXTURE_2D);
            }

            //滤镜
            if (mNeedFilter) {
                int ret = mSTMobileStreamFilterNative.processTexture(texId, width, height, mFilterTextureOutId[0]);
                if (ret == 0) {
                    texId = mFilterTextureOutId[0];
                }
            }
        }

        return texId;
    }

    private void initBeauty() {
        // 初始化beautify,preview的宽高
        int result = mStBeautifyNative.createInstance();
        Log.i(TAG, "the result is for initBeautify " + result);
        if (result == 0) {
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_REDDEN_STRENGTH, mBeautifyParams[0]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SMOOTH_STRENGTH, mBeautifyParams[1]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_WHITEN_STRENGTH, mBeautifyParams[2]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_ENLARGE_EYE_RATIO, mBeautifyParams[3]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SHRINK_FACE_RATIO, mBeautifyParams[4]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SHRINK_JAW_RATIO, mBeautifyParams[5]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_CONSTRACT_STRENGTH, mBeautifyParams[6]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SATURATION_STRENGTH, mBeautifyParams[7]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_DEHIGHLIGHT_STRENGTH, mBeautifyParams[8]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_NARROW_FACE_STRENGTH, mBeautifyParams[9]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_NARROW_NOSE_RATIO, mBeautifyParams[10]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_NOSE_LENGTH_RATIO, mBeautifyParams[11]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_CHIN_LENGTH_RATIO, mBeautifyParams[12]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_MOUTH_SIZE_RATIO, mBeautifyParams[13]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_PHILTRUM_LENGTH_RATIO, mBeautifyParams[14]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_HAIRLINE_HEIGHT_RATIO, mBeautifyParams[15]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_3D_THIN_FACE_SHAPE_RATIO, mBeautifyParams[16]);
        }
    }

    private void initFilter() {
        mSTMobileStreamFilterNative.createInstance();
        mSTMobileStreamFilterNative.setStyle(mCurrentFilterStyle);
        mCurrentFilterStrength = mFilterStrength;
        mSTMobileStreamFilterNative.setParam(STFilterParamsType.ST_FILTER_STRENGTH, mCurrentFilterStrength);
    }

    private void initHumanAction() {
        new Thread(() -> {
            synchronized (mHumanActionHandleLock) {
                //从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
                int result = mSTHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionCreateConfig, mContext.getAssets());
                Log.i(TAG, "the result for createInstance for human_action is " + result);

                if (result == 0) {
                    mIsCreateHumanActionHandleSucceeded = true;
                    mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);

                    //for test face morph
                    result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_FACE_EXTRA, mContext.getAssets());
                    Log.i(TAG, "add face extra model result: " + result);

                    //for test avatar
                    result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
                    Log.i(TAG, "add eyeball contour model result: " + result);
                }
            }
        }).start();
    }

    private void initSticker() {
        int result = mStStickerNative.createInstance(mContext);
        //从资源文件加载Avatar模型
        mStStickerNative.loadAvatarModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_CORE, mContext.getAssets());
        setHumanActionDetectConfig(mNeedSticker, mStStickerNative.getTriggerAction());
        Log.i(TAG, "the result for createInstance for human_action is " + result);
    }

    /**
     * human action detect的配置选项,根据Sticker的TriggerAction和是否需要美颜配置
     *
     * @param needFaceDetect 是否需要开启face detect
     * @param config         sticker的TriggerAction
     */
    private void setHumanActionDetectConfig(boolean needFaceDetect, long config) {
        if (!mNeedSticker || mCurrentSticker == null) {
            config = 0;
        }

        if (needFaceDetect) {
            mDetectConfig = config | STMobileHumanActionNative.ST_MOBILE_FACE_DETECT;
        } else {
            mDetectConfig = config;
        }
    }

    private ArrayList<UFilter> getStickerFiles(Context context, String index) {
        ArrayList<UFilter> stickerFiles = new ArrayList<>();
        //Bitmap iconClose = BitmapFactory.decodeResource(context.getResources(), R.drawable.close_sticker);
        Bitmap iconNone = BitmapFactory.decodeResource(context.getResources(), R.drawable.none);

        List<String> stickerModels = FileUtils.getStickerZipFilesFromSd(context, index);
        Map<String, Bitmap> stickerIcons = FileUtils.getStickerIconFilesFromSd(context, index);
        List<String> stickerNames = FileUtils.getStickerNames(context, index);

        for (int i = 0; i < stickerModels.size(); i++) {
            Bitmap icon = stickerIcons.get(stickerNames.get(i)) == null ? iconNone : stickerIcons.get(stickerNames.get(i));
            UFilter item = new UFilter(stickerNames.get(i), icon, stickerModels.get(i));
            item.setVendor(FilterVendor.SENSETIME);
            stickerFiles.add(item);
        }

        return stickerFiles;
    }

    @Override
    public void init(@NotNull Context context, boolean isPreview) {
        //copy model file to sdcard
        FileUtils.copyModelFiles(context.getApplicationContext());
    }

    @Override
    public void destroy() {
        Log.i(TAG, "onDestroy");
        synchronized (mHumanActionHandleLock) {
            mSTHumanActionNative.destroyInstance();
        }
    }

    @Override
    public void changeFilter(@NotNull UFilter filter) {
        if (filter == null) {
            clearAllFilters();
            return;
        }
        enableSticker(true);
        changeSticker(filter.getPath());
    }

    @Override
    public void changeMusicFilterTime(long time) {

    }

    @Override
    public void changeBeautyValue(float value, @NotNull BeautyParamEnum beautyType) {
        int index = ST_BEAUTIFY_SMOOTH_STRENGTH;
        switch (beautyType) {
            case FACE_BLUR: // 磨皮
                index = ST_BEAUTIFY_SMOOTH_STRENGTH;
                break;
            case EYE_ENLARGE: // 大眼
                index = ST_BEAUTIFY_ENLARGE_EYE_RATIO;
                break;
            case CHEEK_THINNING: // 瘦脸
                index = ST_BEAUTIFY_SHRINK_FACE_RATIO;
                break;
        }
        if (mBeautifyParams[index] != value) {
            mStBeautifyNative.setParam(beautyTypes[index], value);
            Log.i("tanhx", "beaty index = " + index + ", value = " + value);
            mBeautifyParams[index] = value;
        }
    }

    @Override
    public void changeBeautyFilter(@NotNull UFilter filter) {
        mFilterStyle = filter.getPath();
    }

    @Override
    public void clearAllFilters() {
        enableSticker(false);
        removeAllStickers();
    }

    public static void setEnabled(boolean enable) {
        mIsEnabled = enable;
    }

    @Override
    public boolean needReInit() {
        return false;
    }

    // 美颜参数定义
    public static final int[] beautyTypes = {
            STBeautyParamsType.ST_BEAUTIFY_REDDEN_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_SMOOTH_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_WHITEN_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_ENLARGE_EYE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_SHRINK_FACE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_SHRINK_JAW_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_CONSTRACT_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_SATURATION_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_DEHIGHLIGHT_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_NARROW_FACE_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_3D_NARROW_NOSE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_NOSE_LENGTH_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_CHIN_LENGTH_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_MOUTH_SIZE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_PHILTRUM_LENGTH_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_HAIRLINE_HEIGHT_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_3D_THIN_FACE_SHAPE_RATIO,

    };

    public static final int ST_BEAUTIFY_REDDEN_STRENGTH = 0;
    public static final int ST_BEAUTIFY_SMOOTH_STRENGTH = 1;
    public static final int ST_BEAUTIFY_WHITEN_STRENGTH = 2;
    public static final int ST_BEAUTIFY_ENLARGE_EYE_RATIO = 3;
    public static final int ST_BEAUTIFY_SHRINK_FACE_RATIO = 4;
    public static final int ST_BEAUTIFY_SHRINK_JAW_RATIO = 5;
    public static final int ST_BEAUTIFY_CONSTRACT_STRENGTH = 6;
    public static final int ST_BEAUTIFY_SATURATION_STRENGTH = 7;
    public static final int ST_BEAUTIFY_DEHIGHLIGHT_STRENGTH = 8;
    public static final int ST_BEAUTIFY_NARROW_FACE_STRENGTH = 9;
    public static final int ST_BEAUTIFY_3D_NARROW_NOSE_RATIO = 10;
    public static final int ST_BEAUTIFY_3D_NOSE_LENGTH_RATIO = 11;
    public static final int ST_BEAUTIFY_3D_CHIN_LENGTH_RATIO = 12;
    public static final int ST_BEAUTIFY_3D_MOUTH_SIZE_RATIO = 13;
    public static final int ST_BEAUTIFY_3D_PHILTRUM_LENGTH_RATIO = 14;
    public static final int ST_BEAUTIFY_3D_HAIRLINE_HEIGHT_RATIO = 15;
    public static final int ST_BEAUTIFY_3D_THIN_FACE_SHAPE_RATIO = 16;
}
