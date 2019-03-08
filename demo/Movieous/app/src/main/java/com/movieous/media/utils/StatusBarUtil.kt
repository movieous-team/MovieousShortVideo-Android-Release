package com.movieous.media.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.movieous.media.R

object StatusBarUtil {

    //是否沉浸式
    var isImmersiveMode: Boolean = false
        private set
    //是否状态栏白底黑字
    var isBlackext: Boolean = false
        private set

    /**
     * 设置状态栏颜色
     *
     * @param color           状态栏颜色值
     * @param isFontColorDark 深色字体模式
     */
    fun setColor(activity: Activity, color: Int, isFontColorDark: Boolean) {

        if (isFullScreen(activity)) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            isImmersiveMode = true

            setStatusBarColor(activity, color, isFontColorDark)
        }
    }

    /**
     * 覆盖状态栏模式
     *
     * @param isFontColorDark 深色字体模式
     */
    fun setCoverStatus(activity: Activity, isFontColorDark: Boolean) {

        if (isFullScreen(activity)) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            isImmersiveMode = true
            setStatusBarColor(activity, 0, isFontColorDark)
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @return 状态栏矩形条
     */
    private fun createStatusBarView(activity: Activity, color: Int): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = View(activity)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(color)
        return statusBarView
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        rootView.fitsSystemWindows = true
        rootView.clipToPadding = true
    }

    /**
     * 黑色字体
     */
    fun setStatusBarColor(activity: Activity, BgColor: Int, isFontColorDark: Boolean) {
        var BgColor = BgColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (setMIUIStatusBarLightMode(activity, isFontColorDark)) {//MIUI
                //MIUI9以上api废除, 要调用系统的
                setAndroidStatusTextColor(activity, isFontColorDark)
                isBlackext = true
                //miui设置成功
            } else if (setFLYMEStatusBarLightMode(activity, isFontColorDark)) {//Flyme
                isBlackext = true
                //魅族设置成功
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
                //系统6.0设置成功
                isBlackext = true
                setAndroidStatusTextColor(activity, isFontColorDark)
            } else {
                //黑色字体设置失败, 背景颜色默认
                BgColor = activity.resources.getColor(R.color.black)
            }

            activity.window.statusBarColor = BgColor
        }
    }

    //android 6.0以上设置状态栏黑色
    private fun setAndroidStatusTextColor(activity: Activity, isFontColorDark: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            activity.window.decorView.systemUiVisibility = if (isFontColorDark) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUI6以上
     *
     * @param isFontColorDark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private fun setMIUIStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        val window = activity.window
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (isFontColorDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (e: Exception) {
                //not MIUI
            }

        }
        return result
    }

    /**
     * 设置状态栏字体图标为深色，魅族4.4
     *
     * @param isFontColorDark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private fun setFLYMEStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        val window = activity.window
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (isFontColorDark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {
                //not meizu
            }

        }
        return result
    }


    /**
     * 状态栏高度
     */
    fun getStatusHeight(context: Context): Int {

        var statusBarHeight = -1
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    /**
     * @param activity
     * @return 判断当前手机是否是全屏
     */
    fun isFullScreen(activity: Activity): Boolean {
        val flag = activity.window.attributes.flags
        return flag and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    fun setPadding(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setPadding(
                view.paddingLeft, view.paddingTop + getStatusHeight(context),
                view.paddingRight, view.paddingBottom
            )
        }
    }
}
