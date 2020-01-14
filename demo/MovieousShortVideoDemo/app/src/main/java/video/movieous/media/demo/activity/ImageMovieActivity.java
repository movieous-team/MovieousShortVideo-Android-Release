package video.movieous.media.demo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import video.movieous.engine.UVideoSaveListener;
import video.movieous.engine.image.UPhotoMovieType;
import video.movieous.engine.view.UImageRenderView;
import video.movieous.media.demo.R;
import video.movieous.media.demo.activity.base.BaseActivity;
import video.movieous.media.demo.model.TransferItem;
import video.movieous.media.demo.utils.UriUtil;
import video.movieous.shortvideo.UImageCombineManager;

import java.util.LinkedList;
import java.util.List;

/**
 * ImageMovieActivity
 */
public class ImageMovieActivity extends BaseActivity {
    private static final int REQUEST_CODE_CHOOSE = 1;
    private static final int REQUEST_MUSIC = 2;

    private UImageCombineManager mImageEditManager;
    private UImageRenderView mRenderView;
    private List<TransferItem> mTransfers;
    private int mTransferIndex;
    private boolean mIsRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initTransfers();
        initImageEditManager();
        requestPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageEditManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageEditManager.pause();
    }

    private void requestPhotos() {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP), false)
                .showSingleMediaType(true)
                .maxSelectable(9)
                .countable(true)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (!mIsRunning) {
                finish();
            }
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            final List<String> paths = Matisse.obtainPathResult(data);
            onPhotoPick(paths);
        } else if (requestCode == REQUEST_MUSIC) {
            Uri uri = data.getData();
            setMusic(UriUtil.getPath(this, uri));
        }
    }

    private void initView() {
        setContentView(R.layout.activity_image_combine);
        mRenderView = $(R.id.preview);

        $(R.id.change_image).setOnClickListener(v -> requestPhotos());

        $(R.id.record).setOnClickListener(v -> saveVideo());

        $(R.id.transfer_change).setOnClickListener(v -> changeTransfer());

        $(R.id.add_music).setOnClickListener(v -> onMusicClick());
    }

    private void initImageEditManager() {
        mImageEditManager = new UImageCombineManager();
        mImageEditManager.init(this, mRenderView);
    }

    private void setMusic(String musicFile) {
        mImageEditManager.setMusic(musicFile);
    }

    private void onPhotoPick(List<String> photos) {
        if (photos.isEmpty()) return;
        mIsRunning = true;
        mImageEditManager.setImageList(photos);
    }

    private void onMusicClick() {
        Intent i = new Intent();
        i.setType("audio/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, REQUEST_MUSIC);
    }

    private void initTransfers() {
        if (mTransfers != null) return;
        mTransfers = new LinkedList<>();
        mTransfers.add(new TransferItem(R.drawable.texture, "LeftRight", UPhotoMovieType.HORIZONTAL_TRANS));
        mTransfers.add(new TransferItem(R.drawable.texture, "UpDown", UPhotoMovieType.VERTICAL_TRANS));
        mTransfers.add(new TransferItem(R.drawable.texture, "Window", UPhotoMovieType.WINDOW));
        mTransfers.add(new TransferItem(R.drawable.texture, "Gradient", UPhotoMovieType.GRADIENT));
        mTransfers.add(new TransferItem(R.drawable.texture, "Tranlation", UPhotoMovieType.SCALE_TRANS));
        mTransfers.add(new TransferItem(R.drawable.texture, "Thaw", UPhotoMovieType.THAW));
        mTransfers.add(new TransferItem(R.drawable.texture, "Scale", UPhotoMovieType.SCALE));
        mTransferIndex = 0;
    }

    private void onTransferSelect(TransferItem item) {
        mImageEditManager.changeTransfer(item.type);
    }

    private void changeTransfer() {
        mTransferIndex++;
        if (mTransferIndex >= mTransfers.size()) mTransferIndex = 0;
        onTransferSelect(mTransfers.get(mTransferIndex));
    }

    private void saveVideo() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("saving video...");
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
        final long startRecodTime = System.currentTimeMillis();
        mImageEditManager.saveVideo(new UVideoSaveListener() {
            @Override
            public void onVideoSaveProgress(float progress) {
                dialog.setProgress((int) (progress * 100));
            }

            @Override
            public void onVideoSaveSuccess(String path) {
                long recordEndTime = System.currentTimeMillis();
                Log.i("Record", "record:" + (recordEndTime - startRecodTime));
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Video save to path:" + path, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ImageMovieActivity.this, VideoEditActivity.class);
                intent.putExtra(VideoEditActivity.VIDEO_PATH, path);
                startActivity(intent);
            }
        });
    }

}
