package com.movieous.media

import android.app.Application
import android.content.Context
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
        context = applicationContext
        DisplayManager.init(this)

        initShortVideoEnv()
        BaseUtils.init(this)
        // 获取播放列表
        VideoDataUtil.doGetVideoList()
    }

    // 初始化 SDK 运行环境
    private fun initShortVideoEnv() {
        UShortVideoEnv.setLogLevel(ULog.D)
        UShortVideoEnv.init(MyApplication.context, Constants.MOVIEOUS_SIGN)
    }

}
