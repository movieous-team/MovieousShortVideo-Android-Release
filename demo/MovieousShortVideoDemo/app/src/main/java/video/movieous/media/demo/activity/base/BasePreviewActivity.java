package video.movieous.media.demo.activity.base;

import android.util.Log;
import com.kiwi.ui.KwControlView;
import video.movieous.engine.UVideoFrameListener;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.kiwi.KwTrackerWrapper;

public abstract class BasePreviewActivity extends BaseActivity implements UVideoFrameListener {
    private static final String TAG = "BasePreviewActivity";

    protected UTextureView mRenderView;
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    protected KwTrackerWrapper mKwTrackWrapper;
    protected KwControlView mKwControlView;
    private boolean mIsKwOnSurfaceCreatedInvoked;
    private boolean mIsKwOnSurfaceChangedInvoked;

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        Log.i(TAG, "onSurfaceChanged: w = " + width + ", h = " + height);
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mIsKwOnSurfaceChangedInvoked = false;
        mIsKwOnSurfaceCreatedInvoked = false;
        if (mKwTrackWrapper != null) {
            mKwTrackWrapper.onSurfaceDestroyed();
        }
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        if (mKwTrackWrapper == null) return texId;
        if (!mIsKwOnSurfaceCreatedInvoked) {
            mIsKwOnSurfaceCreatedInvoked = true;
            mKwTrackWrapper.onSurfaceCreated(this);
        }
        if (!mIsKwOnSurfaceChangedInvoked) {
            mIsKwOnSurfaceChangedInvoked = true;
            mSurfaceWidth = mRenderView.getPreviewWidth();
            mSurfaceHeight = mRenderView.getPreviewHeight();
            mKwTrackWrapper.onSurfaceChanged(mSurfaceWidth, mSurfaceHeight, texWidth, texHeight);
        }
        return mKwTrackWrapper.onDrawFrame(texId, texWidth, texHeight);
    }

}
