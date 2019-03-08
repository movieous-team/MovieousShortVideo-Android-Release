package com.movieous.media.mvp.contract

import android.content.Context
import com.faceunity.entity.Filter
import com.movieous.media.mvp.model.entity.BeautyParamEnum
import com.movieous.media.mvp.model.entity.MagicFilterItem

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
    fun changeFilter(filter: MagicFilterItem)

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
    fun changeBeautyFilter(filterName: Filter)

    /**
     * Clear all filters
     */
    fun clearAllFilters()

    fun needReInit(): Boolean
}
