package com.movieous.media.mvp.contract

import com.faceunity.entity.Filter
import com.movieous.media.mvp.model.entity.BeautyParamEnum
import com.movieous.media.mvp.model.entity.MagicFilterItem

interface FilterChangedListener {
    /**
     * Triggered when the magic filter is changed
     */
    fun onMagicFilterChanged(filter: MagicFilterItem)

    /**
     * Triggered when music filter time is changed
     */
    fun onMusicFilterTime(time: Long)

    /**
     * Triggered when face beauty param vale is changed
     */
    fun onBeautyValueChanged(value: Float, beautyType: BeautyParamEnum)

    /**
     * Triggered when beauty filter is changed
     */
    fun onBeautyFilterChanged(filterName: Filter)

    /**
     * Remove last filter
     */
    fun onRemoveLastFilter()

    /**
     * Clear all filter
     */
    fun onClearFilter()
}
