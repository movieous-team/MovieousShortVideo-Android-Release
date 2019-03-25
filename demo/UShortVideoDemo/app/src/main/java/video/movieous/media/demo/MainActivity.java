package video.movieous.media.demo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import cn.ezandroid.ezpermission.EZPermission;
import cn.ezandroid.ezpermission.Permission;
import video.movieous.media.demo.activity.*;
import video.movieous.media.demo.activity.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 申请权限
        EZPermission.permissions(Permission.CAMERA, Permission.STORAGE, Permission.MICROPHONE)
                .apply(this, null);

        $(R.id.image_video).setOnClickListener(view -> {
            Intent intent = new Intent(this, ImageMovieActivity.class);
            startActivity(intent);
        });

        $(R.id.video_edit).setOnClickListener(view -> {
            Intent intent = new Intent(this, VideoEditActivity.class);
            startActivity(intent);
        });

        $(R.id.camera_record).setOnClickListener(view -> {
            Intent intent = new Intent(this, VideoRecordActivity.class);
            startActivity(intent);
        });

        $(R.id.video_trim).setOnClickListener(view -> {
            Intent intent = new Intent(this, VideoTrimActivity.class);
            startActivity(intent);
        });

        $(R.id.camera_movie_record).setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiVideoRecordActivity.class);
            startActivity(intent);
        });

        $(R.id.view_record).setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewRecordActivity.class);
            startActivity(intent);
        });

        $(R.id.screen_record).setOnClickListener(view -> {
            Intent intent = new Intent(this, ScreenRecordActivity.class);
            startActivity(intent);
        });

        String ver = "版本号：" + getVersionDescription() + "，编译时间：" + getBuildTime();
        ((TextView) $(R.id.tv_version)).setText(ver);
    }

    private String getVersionDescription() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    private String getBuildTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(BuildConfig.BUILD_TIME);
    }

}
