package com.movieous.media.mvp.contract

import android.content.Context
import com.movieous.media.mvp.model.entity.BeautyParamEnum
import com.movieous.media.mvp.model.entity.UFilter
import java.nio.ByteBuffer
import java.util.ArrayList

interface FilterSdkManager {

    /**
     * Triggered when filter engine init
     */
    fun init(context: Context, isPreview: Boolean)

    /**
     * Triggered when filter engine destroy
     */
    fun destroy()

    /**
     * Triggered when surface created
     */
    fun onSurfaceCreated()

    /**
     * Triggered when surface changed
     */
    fun onSurfaceChanged(width: Int, height: Int)

    /**
     * Triggered when draw frame
     */
    fun onDrawFrame(texId: Int, width: Int, height: Int): Int

    /**
     * Change filter
     */
    fun changeFilter(filter: UFilter)

    /**
     * Change music filter time
     */
    fun changeMusicFilterTime(time: Long)

    /**
     * Change face beauty param value
     */
    fun changeBeautyValue(value: Float, beautyType: BeautyParamEnum)

    /**
     * Change beauty filter
     */
    fun changeBeautyFilter(filter: UFilter)

    /**
     * Clear all filters
     */
    fun clearAllFilters()

    fun needReInit(): Boolean

    fun getFilterTypeName(): Array<String>

    fun getMagicFilterList(type: Int): ArrayList<UFilter>

    fun getRGBABuffer(): ByteBuffer

    fun setRGBABuffer(buffer: ByteBuffer)
}
