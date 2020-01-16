package video.movieous.media.demo.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.List;

import video.movieous.engine.UVideoFrameListener;
import video.movieous.engine.core.env.FitViewHelper;
import video.movieous.engine.view.UFitViewHelper;
import video.movieous.engine.view.UPaintView;
import video.movieous.engine.view.UTextView;
import video.movieous.engine.view.UTextureView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseEditActivity;
import video.movieous.media.demo.view.StrokedTextView;
import video.movieous.shortvideo.UImageEditManager;
import video.movieous.shortvideo.USticker;

/**
 * ImageEditActivity
 */
public class ImageEditActivity extends BaseEditActivity implements UVideoFrameListener {
    private static final String TAG = "ImageEditActivity";

    private UTextureView mRenderView;
    private ImageView mPreviewImage;
    private Bitmap mBitmap;
    private UImageEditManager mImageEditManager;
    private USticker mTextSticker;
    private UPaintView mPaintView;
    private StrokedTextView mTextView;
    private TextStyle[] mTextStyleList;
    private int mTextStyleIndex = 10;
    private boolean mEdgeBlurEnabled = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initImageManager();
        startFileSelectActivity(this, true, 1);
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
        if (mImageEditManager != null) {
            mImageEditManager.release();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_image_edit);
        mRenderView = $(R.id.render_view);
        mRenderView.setScaleType(UFitViewHelper.ScaleType.CENTER_CROP);
        mRenderView.setRenderMode(UTextureView.RENDERMODE_CONTINUOUSLY);
        mPreviewImage = $(R.id.preview_image);

        $(R.id.add_text).setOnClickListener(view -> demoTextView() /*demoTextSticker()*/);

        $(R.id.add_paintview).setOnClickListener(view -> demoGraffitiPaint());

        $(R.id.save_img).setOnClickListener(view -> mImageEditManager.save("/sdcard/Download/" + "test.jpg", bitmap -> runOnUiThread(() -> mPreviewImage.setImageBitmap(bitmap))));

    }

    /**
     * 定义 UImageEditManager 图片编辑类对象
     */
    private void initImageManager() {
        mImageEditManager = new UImageEditManager();
        mImageEditManager.init(mRenderView)
                .setEdgeBlurEnabled(mEdgeBlurEnabled)
                .setBackgroundColor(Color.DKGRAY)
                .setVideoFrameListener(this);
    }

    // 文字水印
    private void demoTextSticker() {
        if (mTextSticker == null) {
            addTextSticker();
        } else {
            removeTextSticker();
        }
    }

    // 文字特效
    private void demoTextView() {
        if (mTextView == null) {
            addTextView();
        } else {
            removeTextView();
        }
    }

    private void addTextSticker() {
        mTextSticker = new USticker();
        String stickerText = "美丽新世界";
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

    private void addTextView() {
        if (mTextStyleList == null) {
            mTextStyleList = initTextStyleList();
        }
        // Get the data model based on position
        final TextStyle info = mTextStyleList[mTextStyleIndex];
        mTextView = new StrokedTextView(this);
        // Set item views based on your views and data model
        mTextView.setText(info.text);
        mTextView.setTextColor(getResources().getColor(info.colorID));
        mTextView.setTypeface(info.typeface, info.style);
        mTextView.setStrokeWidth(info.strokeWidth);
        mTextView.setStrokeColor(info.strokeColor);
        mTextView.setTextSize(40);
        if (info.shadowRadius > 0) {
            mTextView.setShadowLayer(info.shadowRadius, info.shadowDx, info.shadowDy, info.shadowColor);
        }

        mTextView.setOnTouchListener(new ViewTouchListener(mTextView));
        mImageEditManager.addTextView(mTextView);
        // 居中显示
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTextView.getLayoutParams();
        layoutParams.leftMargin = mRenderView.getLeft() + mRenderView.getWidth() / 2 - mRenderView.getWidth() / 4;
        layoutParams.topMargin = mRenderView.getTop() + mRenderView.getHeight() / 2 - 80 / 2;
        mTextView.setLayoutParams(layoutParams);
        mTextView.requestLayout();
    }

    private void removeTextView() {
        mImageEditManager.removeTextView(mTextView);
        mTextView = null;
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

    @Override
    protected void getFiles(List<String> fileList) {
        String imgFile = fileList.get(0);
        mImageEditManager.setFilePath(imgFile, 720, 1280);

        // 这里演示自定义显示策略，默认是根据 ScaleType 最大化显示图片
        // 这里的策略是宽度方向铺满画面，高度方向超过显示高度则裁剪，不足显示高度时开启边缘模糊
        mBitmap = mImageEditManager.getBitmap();
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int scrW = mRenderView.getWidth();
        int scrH = mRenderView.getHeight();
        float inputAspect = width * 1f / height;
        float outputAspect = scrW * 1f / scrH;
        mEdgeBlurEnabled = inputAspect > outputAspect;
        mRenderView.setScaleType(mEdgeBlurEnabled ? FitViewHelper.ScaleType.CENTER_INSIDE : FitViewHelper.ScaleType.CENTER_CROP);
        mRenderView.setAspectRatio(width * 1f / height, 0, 0);
        mRenderView.requestLayout();
        mRenderView.post(() -> mImageEditManager.setEdgeBlurEnabled(mEdgeBlurEnabled, 90));
    }

    private class ViewTouchListener implements View.OnTouchListener {
        private float lastTouchRawX;
        private float lastTouchRawY;
        private boolean scale;
        private boolean isViewMoved;
        private View mView;

        public ViewTouchListener(View view) {
            mView = view;
        }

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mView instanceof UTextView) {
                    mImageEditManager.removeTextView((UTextView) mView);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isViewMoved) {
                    return true;
                }
                return true;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(ImageEditActivity.this, simpleOnGestureListener);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }

            int action = event.getAction();
            float touchRawX = event.getRawX();
            float touchRawY = event.getRawY();
            float touchX = event.getX();
            float touchY = event.getY();

            if (action == MotionEvent.ACTION_DOWN) {
                boolean xOK = touchX >= v.getWidth() * 3 / 4 && touchX <= v.getWidth();
                boolean yOK = touchY >= v.getHeight() * 2 / 4 && touchY <= v.getHeight();
                scale = xOK && yOK;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                float deltaRawX = touchRawX - lastTouchRawX;
                float deltaRawY = touchRawY - lastTouchRawY;

                if (scale) {
                    // rotate
                    float centerX = v.getX() + (float) v.getWidth() / 2;
                    float centerY = v.getY() + (float) v.getHeight() / 2;
                    double angle = Math.atan2(touchRawY - centerY, touchRawX - centerX) * 180 / Math.PI;
                    v.setPivotX(0);
                    v.setPivotY((v.getHeight()) * 1.0f);
                    v.setRotation((float) angle - 45);

                    // scale
                    float xx = (touchRawX >= centerX ? deltaRawX : -deltaRawX);
                    float yy = (touchRawY >= centerY ? deltaRawY : -deltaRawY);
                    float sf = (v.getScaleX() + xx / v.getWidth() + v.getScaleY() + yy / v.getHeight()) / 2;
                    v.setScaleX(sf);
                    v.setScaleY(sf);
                } else {
                    // translate
                    v.setTranslationX(v.getTranslationX() + deltaRawX);
                    v.setTranslationY(v.getTranslationY() + deltaRawY);
                }
                isViewMoved = true;
            }

            if (action == MotionEvent.ACTION_UP) {
                isViewMoved = false;
            }

            lastTouchRawX = touchRawX;
            lastTouchRawY = touchRawY;
            return true;
        }
    }

    public static int[] colors = {R.color.text_color1, R.color.text_color2, R.color.text_color3, R.color.text_color4,
            R.color.text_color5, R.color.text_color6, R.color.text_color7, R.color.text_color8,
            R.color.text_color9, R.color.text_color10, R.color.text_color11, R.color.text_color12};


    private TextStyle[] initTextStyleList() {
        TextStyle[] textStyles = new TextStyle[colors.length];
        for (int i = 0; i < textStyles.length; i++) {
            TextStyle textStyle = new TextStyle();
            textStyle.text = getResources().getString(R.string.demo_add_text);
            textStyles[i] = textStyle;
            textStyle.colorID = colors[i];
            textStyle.alpha = 0.8f;

            if (i >= 4 && i < 8) {
                textStyle.strokeColor = Color.WHITE;
                textStyle.strokeWidth = 5.0f;
            }

            if (i >= 8) {
                textStyle.colorID = R.color.white;
                textStyle.shadowRadius = 20;
                textStyle.shadowColor = getResources().getColor(colors[i]);
            }
        }
        return textStyles;
    }

    private class TextStyle {
        String text;
        int colorID;
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/HappyZcool-2016.ttf");
        int style = Typeface.BOLD;
        float alpha = 1;
        int shadowColor = Color.TRANSPARENT;
        int shadowRadius;
        int shadowDx;
        int shadowDy;
        int strokeColor;
        float strokeWidth;
    }

}
