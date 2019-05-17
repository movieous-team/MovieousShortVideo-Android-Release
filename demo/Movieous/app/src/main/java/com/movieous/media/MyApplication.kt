package com.movieous.media

import android.app.Application
import android.content.Context
import com.movieous.media.mvp.model.VideoDataUtil
import com.movieous.media.utils.DisplayManager
import iknow.android.utils.BaseUtils
import video.movieous.droid.player.MovieousPlayerEnv
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
        context = applicationContext
        DisplayManager.init(this)

        initShortVideoEnv()
        initMovieousPlayerEnv()
        BaseUtils.init(this)
        // 获取播放列表
        VideoDataUtil.doGetVideoList()
    }

    // 初始化短视频 SDK 运行环境
    private fun initShortVideoEnv() {
        UShortVideoEnv.setLogLevel(ULog.I)
        UShortVideoEnv.init(context, Constants.MOVIEOUS_SIGN)
    }

    // 初始化播放器 SDK 运行环境
    private fun initMovieousPlayerEnv() {
        // 初始化 SDK，必须第一个调用，否则会出现异常
        MovieousPlayerEnv.init(context)
        // 开启本地缓存，可以离线播放, 需要 okhttp 支持
        MovieousPlayerEnv.setCacheInfo(cacheDir, null, (100 * 1024 * 1024).toLong(), "MovieousPlayer20", true)

    }

}
