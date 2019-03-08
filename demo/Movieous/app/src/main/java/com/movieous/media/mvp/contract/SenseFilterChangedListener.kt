package com.movieous.media.mvp.contract

interface SenseFilterChangedListener {
    /**
     * Triggered when the effect filter is changed
     */
    fun onChangeSticker(path: String)

    fun onRemoveAllStickers()
}
