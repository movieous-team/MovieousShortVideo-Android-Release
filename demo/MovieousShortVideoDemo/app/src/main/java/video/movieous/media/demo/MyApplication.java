package video.movieous.media.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import video.movieous.droid.player.MovieousPlayerEnv;
import video.movieous.engine.base.utils.ULog;
import video.movieous.shortvideo.UShortVideoEnv;

/**
 * MyApplication
 */
public class MyApplication extends Application {
    public static MyApplication gContext = null;
    public static final String MOVIEOUS_SHORTVIDEO_SIGN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBpZCI6InZpZGVvLm1vdmllb3VzLm1lZGlhLmRlbW8ifQ.AZGkx11Hojggmn03DXGmFPor6SfSLfOUroDyyycZl5o";
    public static final String MOVIEOUS_PLAYER_SIGN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBpZCI6InZpZGVvLm1vdmllb3VzLm1lZGlhLmRlbW8ifQ.AZGkx11Hojggmn03DXGmFPor6SfSLfOUroDyyycZl5o";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        gContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化短视频 SDK
        initShortVideoEnv();
        // 初始化播放器 SDK
        initMovieousPlayerEnv();
    }

    private void initShortVideoEnv() {
        UShortVideoEnv.init(this, MOVIEOUS_SHORTVIDEO_SIGN);
        UShortVideoEnv.setLogLevel(ULog.I);
    }

    // 不使用 MovieousPlayer SDK 可以不调用, 具体用法请参考播放器 demo
    private void initMovieousPlayerEnv() {
        // 初始化 SDK，必须第一个调用，否则会出现异常
        MovieousPlayerEnv.init(gContext, MOVIEOUS_PLAYER_SIGN);
    }
}
