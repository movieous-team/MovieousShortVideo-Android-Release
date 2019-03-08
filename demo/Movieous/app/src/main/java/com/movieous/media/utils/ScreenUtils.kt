package com.movieous.media.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

object ScreenUtils {

    var creenRealHeight: Int = 0

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private val navBarOverride: String?
        get() {
            var sNavBarOverride: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    val c = Class.forName("android.os.SystemProperties")
                    val m = c.getMethod("get", String::class.java)
                    sNavBarOverride = m.invoke(c, "qemu.hw.mainkeys") as String
                } catch (e: Throwable) {
                }

            }
            return sNavBarOverride
        }

    fun getScreenWidth(context: Context): Int {
        val dm = DisplayMetrics()
        val mWm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val dm = DisplayMetrics()
        val mWm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取系统栏的高度
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res = context.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    /**
     * 设置占比屏幕宽度9/16
     *
     * @param context
     * @param view
     */
    fun setNine_Sixteenth(context: Context, view: View) {
        val width = getWindowWidth(context)
        val para1: ViewGroup.LayoutParams
        para1 = view.layoutParams
        para1.height = width * 9 / 16
        view.layoutParams = para1
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    fun getWindowWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

    /**
     * 设置占比屏幕宽度1/4
     *
     * @param context
     * @param view
     */
    fun setOne_Four_H(context: Context, view: View) {
        val width = getWindowWidth(context)
        val para1: ViewGroup.LayoutParams
        para1 = view.layoutParams
        para1.width = width * 1 / 4
        view.layoutParams = para1
    }

    /**
     * 设置占比屏幕宽度1/4
     *
     * @param context
     * @param view
     */
    fun setOne_Four_V(context: Context, view: View) {
        val width = getWindowWidth(context)
        val para1: ViewGroup.LayoutParams
        para1 = view.layoutParams
        para1.height = width * 1 / 4
        view.layoutParams = para1
    }

    /**
     * 设置占比屏幕宽度16/23
     *
     * @param context
     * @param view
     */
    fun setSixteen_TwentyThree(context: Context, view: View) {
        val width = getWindowWidth(context)
        val para1: ViewGroup.LayoutParams
        para1 = view.layoutParams
        para1.height = width * 23 / 16
        view.layoutParams = para1
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    fun getBottomStatusHeight(context: Context): Int {
        val totalHeight = getDpi(context)
        val contentHeight = getWindowHeight(context)
        return totalHeight - contentHeight
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    fun getDpi(context: Context): Int {
        var dpi = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        val c: Class<*>
        try {
            c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, displayMetrics)
            dpi = displayMetrics.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dpi
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    fun getWindowHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.height
    }

    /**
     * 判断手机是否有虚拟按键功能：
     *
     * @param activity
     * @return
     */
    fun hasNavigationBar(activity: Activity): Boolean {
        val dm = DisplayMetrics()
        val display = activity.windowManager.defaultDisplay
        display.getMetrics(dm)
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        val density = dm.density

        val realDisplayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics)
        } else {
            val c: Class<*>
            try {
                c = Class.forName("android.view.Display")
                val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
                method.invoke(display, realDisplayMetrics)
            } catch (e: Exception) {
                realDisplayMetrics.setToDefaults()
                e.printStackTrace()
            }

        }

        creenRealHeight = realDisplayMetrics.heightPixels
        val creenRealWidth = realDisplayMetrics.widthPixels

        val diagonalPixels = Math.sqrt(Math.pow(creenRealWidth.toDouble(), 2.0) + Math.pow(creenRealHeight.toDouble(), 2.0)).toFloat()
        val screenSize = diagonalPixels / (160f * density) * 1f

        val rs = activity.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        var hasNavBarFun = false
        if (id > 0) {
            hasNavBarFun = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavBarFun = false
            } else if ("0" == navBarOverride) {
                hasNavBarFun = true
            }
        } catch (e: Exception) {
            hasNavBarFun = false
        }

        return hasNavBarFun
    }

    /**
     * 检测虚拟键盘是否开启的
     *
     * @param windowManager
     * @return
     */
    fun checkDeviceHasNavigationBar(windowManager: WindowManager): Boolean {
        val dm = DisplayMetrics()
        val display = windowManager.defaultDisplay
        display.getMetrics(dm)
        val screenWidth = dm.widthPixels
        return creenRealHeight - screenWidth > 0//screenRealHeight上面方法中有计算
    }


    //获取虚拟按键的高度
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (hasNavBar(context)) {
            val res = context.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    /**
     * 检查是否存在虚拟按键栏
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun hasNavBar(context: Context): Boolean {
        val res = context.resources
        val resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android")
        var hasNav = false
        if (resourceId != 0) {
            hasNav = res.getBoolean(resourceId)
        }
        return hasNav
    }

}
