package com.movieous.media.ui.fragment;

import android.app.Activity;
import android.util.Log;
import butterknife.BindView;
import com.movieous.media.R;
import com.movieous.media.api.vendor.fusdk.FuSDKManager;
import com.movieous.media.base.BaseFragment;
import com.movieous.media.mvp.contract.FilterChangedListener;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.MediaParam;
import com.movieous.media.mvp.model.entity.UFilter;
import com.movieous.media.utils.SharePrefUtils;
import com.movieous.media.view.SaveProgressDialog;
import org.jetbrains.annotations.NotNull;
import video.movieous.engine.UAVOptions;
import video.movieous.engine.UVideoFrameListener;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.view.UTextureView;

import static com.movieous.media.ExtensionsKt.showToast;

public class PreviewFragment extends BaseFragment implements UVideoFrameListener, UVideoSaveListener, FilterChangedListener {
    private static final String TAG = "PreviewFragment";

    protected Activity mActivity;
    @BindView(R.id.preview)
    protected UTextureView mPreview;
    protected SaveProgressDialog mProcessingDialog;
    protected FilterSdkManager mFilterSdkManager;
    protected UFilter mCurrentFilter;
    protected UFilter mBeautyFilter;
    protected FilterVendor mFilterVendor;
    protected UAVOptions mAVOptions; // 可以自定义 SDK 参数

    protected void initProcessingDialog() {
        mProcessingDialog = new SaveProgressDialog(mActivity);
    }

    protected void showProcessingDialog() {
        if (mProcessingDialog == null) {
            initProcessingDialog();
        }
        mProcessingDialog.show();
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initView() {
    }

    @Override
    public void lazyLoad() {
        MediaParam mediaParam = SharePrefUtils.getParam(mActivity);
        initAVOptions(mediaParam);
        mFilterVendor = mediaParam.vendor;
    }

    // 自定义音视频输出参数，如果不自定义的话，会采用默认参数，参见 {@link UConstants}
    private void initAVOptions(MediaParam mediaParam) {
        mAVOptions = new UAVOptions()
                .setInteger(UAVOptions.KEY_VIDEO_WIDTH, mediaParam.width)
                .setInteger(UAVOptions.KEY_VIDEO_HEIGHT, mediaParam.height)
                .setInteger(UAVOptions.KEY_VIDEO_BITRATE, mediaParam.videoBitrate * 1000)
                .setInteger(UAVOptions.KEY_VIDEO_FPS, mediaParam.videoFrameRate);
    }

    // UVideoFrameListener
    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        if (isFilterVendorEnabled(false)) {
            initVendorSDKManager();
            mFilterSdkManager.onSurfaceCreated();
            onMagicFilterChanged(mCurrentFilter);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged, width = " + width + ", height = " + height);
        if (isFilterVendorEnabled(false) && mFilterSdkManager != null) {
            mFilterSdkManager.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        if (mFilterSdkManager != null) {
            mFilterSdkManager.destroy();
            mFilterSdkManager = null;
        }
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        if (!isFilterVendorEnabled(false)) return texId;
        int outTexId = texId;
        synchronized (mActivity) {
            if (mFilterSdkManager == null) {
                initVendorSDKManager();
                onSurfaceCreated();
                onSurfaceChanged(texWidth, texHeight);
            }
            if (mFilterSdkManager.needReInit()) {
                onSurfaceCreated();
            }
            outTexId = mFilterSdkManager.onDrawFrame(texId, texWidth, texHeight);
        }
        return outTexId;
    }

    // USaveFileListener
    @Override
    public void onVideoSaveProgress(float progress) {
        mActivity.runOnUiThread(() -> mProcessingDialog.setProgress((int) (100 * progress)));
    }

    @Override
    public void onVideoSaveSuccess(String s) {
    }

    @Override
    public void onVideoSaveCancel() {
        mProcessingDialog.dismiss();
    }

    @Override
    public void onVideoSaveFail(int errorCode) {
        Log.e("", "save edit failed errorCode:" + errorCode);
        mActivity.runOnUiThread(() -> {
            mProcessingDialog.dismiss();
            showToast(mActivity, getString(R.string.save_file_failed_tip) + errorCode);
        });
    }

    //FilterChangedListener
    @Override
    public void onMusicFilterTime(long time) {
        mFilterSdkManager.changeMusicFilterTime(-1);
    }

    @Override
    public void onBeautyValueChanged(float value, @NotNull BeautyParamEnum beautyType) {
        mFilterSdkManager.changeBeautyValue(value, beautyType);
    }

    @Override
    public void onBeautyFilterChanged(@NotNull UFilter filter) {
        mBeautyFilter = filter;
        mFilterSdkManager.changeBeautyFilter(filter);
    }

    @Override
    public void onMagicFilterChanged(@NotNull UFilter filter) {
        if (filter == null || mFilterSdkManager == null) {
            Log.w("", "filter is null!");
            return;
        }
        mCurrentFilter = filter;
        mFilterSdkManager.clearAllFilters();
        mFilterSdkManager.changeFilter(filter);
        showFilterDescription(filter);
    }

    @Override
    public void onRemoveLastFilter() {
    }

    @Override
    public void onClearFilter() {
        if (mFilterSdkManager != null) {
            mFilterSdkManager.clearAllFilters();
        }
    }

    // 初始化三方特效 SDK
    protected void initVendorSDKManager() {
        if (mFilterSdkManager == null) {
            mFilterSdkManager = isFuFilterSDK() ?
                    new FuSDKManager(mActivity) :
                    null;
            mFilterSdkManager.init(mActivity, true);
        }
    }

    private void showFilterDescription(UFilter filterItem) {
        if (filterItem.getDescription() > 0) {
            mActivity.runOnUiThread(() -> showToast(mActivity, getString(filterItem.getDescription())));
        }
    }

    protected boolean isFuFilterSDK() {
        return mFilterVendor == FilterVendor.FACEUNITY;
    }

    protected boolean isFilterVendorEnabled(boolean showToastEnabled) {
        boolean isEnabled = mFilterVendor != FilterVendor.NONE;
        if (!isEnabled && showToastEnabled) {
            showToast(mActivity, getString(R.string.no_vendor_tip));
        }
        return isEnabled;
    }

}
