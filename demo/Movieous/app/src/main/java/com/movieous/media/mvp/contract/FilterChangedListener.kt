package com.movieous.media.mvp.contract

import com.movieous.media.mvp.model.entity.BeautyParamEnum
import com.movieous.media.mvp.model.entity.UFilter

interface FilterChangedListener {
    /**
     * Triggered when the magic filter is changed
     */
    fun onMagicFilterChanged(filter: UFilter)

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
    fun onBeautyFilterChanged(filter: UFilter)

    /**
     * Remove last filter
     */
    fun onRemoveLastFilter()

    /**
     * Clear all filter
     */
    fun onClearFilter()
}
