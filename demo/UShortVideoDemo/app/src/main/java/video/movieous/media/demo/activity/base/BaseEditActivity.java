package video.movieous.media.demo.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import video.movieous.media.demo.R;

import java.util.List;

public abstract class BaseEditActivity extends BasePreviewActivity {
    private static final int REQUEST_CODE_CHOOSE_VIDEO = 1;

    protected String mVideoFile;

    public void startVideoSelectActivity(Activity activity) {
        Matisse.from(activity)
                .choose(MimeType.of(MimeType.MP4, MimeType.THREEGPP), false)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .countable(false)
                .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
            if (resultCode == RESULT_OK) {
                final List<String> paths = Matisse.obtainPathResult(data);
                if (!paths.isEmpty()) {
                    getVideoFile(paths.get(0));
                }
            } else {
                finish();
            }
        }
    }

    protected abstract void getVideoFile(String file);
}
