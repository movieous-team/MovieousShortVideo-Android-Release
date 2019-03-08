package com.movieous.media.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import com.classic.common.MultipleStatusView
import com.movieous.media.R
import com.movieous.media.showToast
import io.reactivex.annotations.NonNull
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * BaseActivity 基类
 */
abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    protected var mLayoutStatusView: MultipleStatusView? = null
    private var exitTime: Long = 0
    private var unbind: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId() == 0) {
            return
        }
        setContentView(layoutId())
        unbind = ButterKnife.bind(this)
        initData()
        initView()
        start()
        initListener()
    }

    override fun onBackPressed() {
        val event = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)
        dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbind?.unbind()
    }

    private fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        start()
    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 开始请求
     */
    abstract fun start()

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showToast("再按一次退出")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("EasyPermissions", "Access to Success：$perms")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        val sb = StringBuffer()
        for (str in perms) {
            sb.append(str)
            sb.append("\n")
        }
        sb.replace(sb.length - 2, sb.length, "")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setRationale(String.format(resources.getString(R.string.permission_tip), sb.toString()))
                .setPositiveButton(R.string.dlg_yes)
                .setNegativeButton(R.string.dlg_no)
                .build()
                .show()
        } else {
            finish()
        }
    }
}


