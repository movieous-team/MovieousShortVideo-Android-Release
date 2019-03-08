package com.movieous.media.ui.fragment;

import android.app.Activity;
import android.util.Log;
import butterknife.BindView;
import com.faceunity.entity.Filter;
import com.movieous.media.R;
import com.movieous.media.api.vendor.fusdk.FuSDKManager;
import com.movieous.media.base.BaseFragment;
import com.movieous.media.mvp.contract.FilterChangedListener;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.MagicFilterItem;
import com.movieous.media.view.SaveProgressDialog;
import org.jetbrains.annotations.NotNull;
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
    protected FilterSdkManager mVendorSdkManager;
    protected MagicFilterItem mCurrentFilter;

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
    }

    // UVideoFrameListener
    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        initVendorSDKManager();
        mVendorSdkManager.onSurfaceCreated();
        onMagicFilterChanged(mCurrentFilter);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged, width = " + width + ", height = " + height);
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        if (mVendorSdkManager != null) {
            mVendorSdkManager.destroy();
        }
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        int outTexId = texId;
        synchronized (mActivity) {
            if (mVendorSdkManager == null) {
                initVendorSDKManager();
            }
            if (mVendorSdkManager.needReInit()) {
                onSurfaceCreated();
            }
            outTexId = mVendorSdkManager.onDrawFrame(texId, texWidth, texHeight);
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
        mVendorSdkManager.changeMusicFilterTime(-1);
    }

    @Override
    public void onBeautyValueChanged(float value, @NotNull BeautyParamEnum beautyType) {
        mVendorSdkManager.changeBeautyValue(value, beautyType);
    }

    @Override
    public void onBeautyFilterChanged(@NotNull Filter filterName) {
        mVendorSdkManager.changeBeautyFilter(filterName);
    }

    @Override
    public void onMagicFilterChanged(@NotNull MagicFilterItem filter) {
        if (filter == null || mVendorSdkManager == null) {
            Log.w("", "filter is null!");
            return;
        }
        mCurrentFilter = filter;
        mVendorSdkManager.clearAllFilters();
        if (filter.getVendor() == FilterVendor.FU) {
            if (filter.getEnabled()) {
                mVendorSdkManager.changeFilter(filter);
            }
        }
        showFilterDescription(filter);
    }

    @Override
    public void onRemoveLastFilter() {
    }

    @Override
    public void onClearFilter() {
        if (mVendorSdkManager != null) {
            mVendorSdkManager.clearAllFilters();
        }
    }

    private void initVendorSDKManager() {
        if (mVendorSdkManager == null) {
            mVendorSdkManager = new FuSDKManager(mActivity);
            mVendorSdkManager.init(mActivity, true);
        }
    }

    private void showFilterDescription(MagicFilterItem filterItem) {
        if (filterItem.getDescription() > 0) {
            mActivity.runOnUiThread(() -> showToast(mActivity, getString(filterItem.getDescription())));
        }
    }

}
