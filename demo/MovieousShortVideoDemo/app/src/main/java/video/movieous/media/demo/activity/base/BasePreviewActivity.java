package video.movieous.media.demo.activity.base;

import android.util.Log;
import video.movieous.engine.UVideoFrameListener;

public abstract class BasePreviewActivity extends BaseActivity implements UVideoFrameListener {
    private static final String TAG = "BasePreviewActivity";
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

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
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        return texId;
    }

}
