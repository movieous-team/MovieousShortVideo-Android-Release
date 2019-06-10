package video.movieous.media.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import video.movieous.engine.base.utils.ULog;
import video.movieous.shortvideo.UShortVideoEnv;

/**
 * MyApplication
 */
public class MyApplication extends Application {

    public static MyApplication gContext = null;
    public static final String MOVIEOUS_SIGN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBpZCI6InZpZGVvLm1vdmllb3VzLm1lZGlhLmRlbW8ifQ.AZGkx11Hojggmn03DXGmFPor6SfSLfOUroDyyycZl5o";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        gContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // memory leak
        //LeakCanary.install(this);
        UShortVideoEnv.init(this, MOVIEOUS_SIGN);
        UShortVideoEnv.setLogLevel(ULog.I);
    }
}
