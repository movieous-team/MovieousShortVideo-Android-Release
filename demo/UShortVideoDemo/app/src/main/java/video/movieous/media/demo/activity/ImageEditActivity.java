package video.movieous.media.demo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import video.movieous.engine.UVideoFrameListener;
import video.movieous.engine.view.UPaintView;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseActivity;
import video.movieous.shortvideo.UImageEditManager;
import video.movieous.shortvideo.USticker;

/**
 * ImageEditActivity
 */
public class ImageEditActivity extends BaseActivity implements UVideoFrameListener {
    private static final String TAG = "ImageEditActivity";

    private UTextureView mRenderView;
    private ImageView mPreviewImage;
    private Bitmap mBitmap;
    private UImageEditManager mImageEditManager;
    private USticker mTextSticker;
    private UPaintView mPaintView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initImageManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageEditManager.release();
    }

    private void initView() {
        setContentView(R.layout.activity_image_edit);
        mRenderView = $(R.id.render_view);
        mRenderView.setRenderMode(UTextureView.RENDERMODE_CONTINUOUSLY);
        mPreviewImage = $(R.id.preview_image);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.preview);

        $(R.id.add_text).setOnClickListener(view -> demoText());

        $(R.id.add_paintview).setOnClickListener(view -> demoGraffitiPaint());

        $(R.id.save_img).setOnClickListener(view -> mImageEditManager.save("/sdcard/Download/" + "test.jpg", bitmap -> runOnUiThread(() -> mPreviewImage.setImageBitmap(bitmap))));

    }

    private void initImageManager() {
        mImageEditManager = new UImageEditManager();
        mImageEditManager.init(mRenderView)
                .setBitmap(mBitmap)
                .setVideoFrameListener(this);
    }

    // 文字特效
    private void demoText() {
        if (mTextSticker == null) {
            addTextSticker();
        } else {
            removeTextSticker();
        }
    }

    private void addTextSticker() {
        mTextSticker = new USticker();
        String stickerText = "美丽的传说";
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int stickerW = width / 2;
        int stickerH = stickerW / stickerText.length();
        mTextSticker.init(USticker.StickerType.TEXT, stickerW, stickerH)
                .setText(stickerText, Color.RED)
                .setDuration(0, 1000)
                .setPosition(width / 2 - stickerW / 2, height - stickerH - 20);     //图像的左上角为坐标原点
        mImageEditManager.addSticker(mTextSticker);
    }

    private void removeTextSticker() {
        mImageEditManager.removeSticker(mTextSticker);
        mTextSticker = null;
    }

    // 涂鸦
    private void demoGraffitiPaint() {
        if (mPaintView == null) {
            addPaintView();
        } else {
            removePaintView();
        }
    }

    private void addPaintView() {
        mPaintView = new UPaintView(this, mRenderView.getWidth(), mRenderView.getHeight());
        mImageEditManager.addPaintView(mPaintView);
    }

    private void removePaintView() {
        mImageEditManager.removePaintView(mPaintView);
        mPaintView = null;
    }

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged: w = " + width + ", h = " + height);
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight) {
        //Log.i(TAG, "onDrawFrame: w = " + texWidth + ", h = " + texHeight);
        // 可以进行三方特效处理
        return texId;
    }
}
