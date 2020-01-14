package video.movieous.media.demo.activity.base;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

/**
 * Activity基类
 */
public class BaseActivity extends AppCompatActivity {

    public <T extends View> T $(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    public <T extends View> T $(View layoutView, @IdRes int resId) {
        return (T) layoutView.findViewById(resId);
    }
}
