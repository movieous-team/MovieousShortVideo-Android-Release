package com.movieous.media

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.faceunity.FURenderer
import com.movieous.media.mvp.model.VideoDataUtil
import com.movieous.media.utils.DisplayManager
import iknow.android.utils.BaseUtils
import video.movieous.engine.base.utils.ULog
import video.movieous.shortvideo.UShortVideoEnv
import kotlin.properties.Delegates

class MyApplication : Application() {

    companion object {
        private val TAG = "MyApplication"
        var context: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        BaseUtils.init(this)
        context = applicationContext
        // 获取播放列表
        VideoDataUtil.doGetVideoList()
        initShortVideoEnv()
        initFaceunity()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    private fun initShortVideoEnv() {
        UShortVideoEnv.setLogLevel(ULog.I)
        UShortVideoEnv.init(context, Constants.MOVIEOUS_SIGN)
    }

    private fun initFaceunity() {
        FURenderer.initFURenderer(context)
    }

    private val mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.i(TAG, "onCreated: " + activity.componentName.className)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.i(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
        }
    }
}
