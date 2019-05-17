package video.movieous.media.demo.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import video.movieous.media.demo.R;

import java.util.List;
import java.util.Set;

public abstract class BaseEditActivity extends BasePreviewActivity {
    private static final int REQUEST_CODE_CHOOSE_FILE = 1;

    protected String mInputFile;

    public void startFileSelectActivity(Activity activity, boolean isImage, int maxSelectableCount) {
        Set<MimeType> sets = isImage ?
                MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP) :
                MimeType.of(MimeType.MP4, MimeType.THREEGPP);
        Matisse.from(activity)
                .choose(sets, false)
                .showSingleMediaType(true)
                .maxSelectable(maxSelectableCount)
                .countable(maxSelectableCount > 1)
                .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                final List<String> paths = Matisse.obtainPathResult(data);
                getFiles(paths);
            } else {
                finish();
            }
        }
    }

    protected abstract void getFiles(List<String> fileList);
}
