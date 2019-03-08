package com.movieous.media.mvp.contract

interface MusicSelectedListener {
    /**
     * Triggered when music is changed
     */
    fun onMusicSelected(file: String)
}
