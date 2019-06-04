package video.movieous.media.demo.kiwi;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;
import com.kiwi.tracker.KwFaceTracker;
import com.kiwi.tracker.KwFilterType;
import com.kiwi.tracker.KwTrackerManager;
import com.kiwi.tracker.KwTrackerSettings;
import com.kiwi.tracker.bean.KwFilter;
import com.kiwi.tracker.bean.conf.StickerConfig;
import com.kiwi.tracker.common.Config;
import com.kiwi.ui.OnViewEventListener;
import com.kiwi.ui.helper.ResourceHelper;
import com.kiwi.ui.model.SharePreferenceMgr;

import static com.kiwi.ui.KwControlView.*;

/**
 * All calls to the Face Effects library are encapsulated in this class
 * Created by shijian on 2016/9/28.
 */

public class KwTrackerWrapper {
    private static final String TAG = KwTrackerWrapper.class.getName();

    private KwTrackerSettings mTrackerSetting;
    private KwTrackerManager mTrackerManager;

    public KwTrackerWrapper(final Context context) {
        SharePreferenceMgr instance = SharePreferenceMgr.getInstance();

        KwTrackerSettings.BeautySettings2 beautySettings2 = new KwTrackerSettings.BeautySettings2();
        beautySettings2.setWhiteProgress(instance.getSkinWhite());
        beautySettings2.setDermabrasionProgress(instance.getSkinRemoveBlemishes());
        beautySettings2.setSaturatedProgress(instance.getSkinSaturation());
        beautySettings2.setPinkProgress(instance.getSkinTenderness());

        KwTrackerSettings.BeautySettings beautySettings = new KwTrackerSettings.BeautySettings();
        beautySettings.setBigEyeScaleProgress(instance.getBigEye());
        beautySettings.setThinFaceScaleProgress(instance.getThinFace());

        mTrackerSetting = new KwTrackerSettings().
                setBeauty2Enabled(instance.isBeautyEnabled()).
                setBeautySettings2(beautySettings2).
                setBeautyFaceEnabled(instance.isLocalBeautyEnabled()).
                setBeautySettings(beautySettings).
                setDefaultDirection(KwFaceTracker.CV_CLOCKWISE_ROTATE_180);

        mTrackerManager = new KwTrackerManager(context).
                setTrackerSetting(mTrackerSetting)
                .build();

        //copy assets config/sticker/filter to sdcard
        ResourceHelper.copyResource2SD(context);

        initKiwiConfig();
    }

    private void initKiwiConfig() {
        //推荐配置，以下情况，选择性能优先模式
        //1.oppo vivo你懂的
        //2.小于5.0的机型可能配置比较差
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        Log.i(TAG, String.format("manufacturer:%s,model:%s,sdk:%s", manufacturer, Build.MODEL, Build.VERSION.SDK_INT));
        boolean isOppoVivo = manufacturer.contains("oppo") || manufacturer.contains("vivo");
        Log.i(TAG, "initKiwiConfig buildVersion" + Build.VERSION.RELEASE);
        if (isOppoVivo || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Config.TRACK_MODE = Config.TRACK_PRIORITY_PERFORMANCE;
        }

        //关闭日志打印,release版本请务必关闭日志打印
        Config.isDebug = false;
    }

    public void onCreate(Activity activity) {
        Log.i(TAG, "onCreate");
        mTrackerManager.onCreate(activity);
    }

    public void onResume(Activity activity) {
        mTrackerManager.onResume(activity);
    }

    public void onPause(Activity activity) {
        mTrackerManager.onPause(activity);
    }

    public void onDestroy(Activity activity) {
        Log.i(TAG, "onDestroy");
        mTrackerManager.onDestory(activity);
    }

    public void onSurfaceCreated(Context context) {
        Log.i(TAG, "onSurfaceCreated");
        mTrackerManager.onSurfaceCreated(context);
    }

    public void onSurfaceChanged(int width, int height, int previewWidth, int previewHeight) {
        Log.i(TAG, "onSurfaceChanged");
        mTrackerManager.onSurfaceChanged(width, height, previewWidth, previewHeight);
    }

    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mTrackerManager.onSurfaceDestroyed();
    }

    public void switchCamera(int ordinal) {
        mTrackerManager.switchCamera(ordinal);
    }

    /**
     * 对纹理进行特效处理（美颜、大眼瘦脸、人脸贴纸、哈哈镜、滤镜）
     *
     * @param texId     YUV格式纹理
     * @param texWidth  纹理宽度
     * @param texHeight 纹理高度
     * @return 特效处理后的纹理
     */
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        int newTexId = texId;
        int filterTexId = mTrackerManager.onDrawTexture2D(texId, texWidth, texHeight, 1);
        if (filterTexId != -1) {
            newTexId = filterTexId;
        }
        int error = GLES20.glGetError(); //请勿删除当前行获取opengl错误代码
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "glError:" + error);
        }
        return newTexId;
    }

    /**
     * UI事件处理类
     *
     * @return
     */
    public OnViewEventListener initUIEventListener() {
        OnViewEventListener eventListener = new OnViewEventListener() {

            @Override
            public void onSwitchBeauty2(boolean enable) {
                mTrackerManager.setBeauty2Enabled(enable);
            }

            @Override
            public void onTakeShutter() {
            }

            @Override
            public void onSwitchCamera() {
            }

            @Override
            public void onSwitchFilter(KwFilter filter) {
                mTrackerManager.switchFilter(filter);
            }

            @Override
            public void onStickerChanged(StickerConfig item) {
                mTrackerManager.switchSticker(item);
            }

            @Override
            public void onSwitchBeauty(boolean enable) {
                mTrackerManager.setBeautyEnabled(enable);
            }

            @Override
            public void onSwitchBeautyFace(boolean enable) {
                mTrackerManager.setBeautyFaceEnabled(enable);
            }

            @Override
            public void onDistortionChanged(KwFilterType filterType) {
                mTrackerManager.switchDistortion(filterType);
            }

            @Override
            public void onAdjustFaceBeauty(int type, float param) {
                switch (type) {
                    case BEAUTY_BIG_EYE_TYPE:
                        mTrackerManager.setEyeMagnifying((int) param);
                        break;
                    case BEAUTY_THIN_FACE_TYPE:
                        mTrackerManager.setChinSliming((int) param);
                        break;
                    case SKIN_SHINNING_TENDERNESS:
                        //粉嫩
                        mTrackerManager.setSkinTenderness((int) param);
                        break;
                    case SKIN_TONE_SATURATION:
                        //饱和
                        mTrackerManager.setSkinSaturation((int) param);
                        break;
                    case REMOVE_BLEMISHES:
                        //磨皮
                        mTrackerManager.setSkinBlemishRemoval((int) param);
                        break;
                    case SKIN_TONE_PERFECTION:
                        //美白
                        mTrackerManager.setSkinWhitening((int) param);
                        break;
                }

            }

            @Override
            public void onFaceBeautyLevel(float level) {
                mTrackerManager.adjustBeauty(level);
            }

        };

        return eventListener;
    }

}
