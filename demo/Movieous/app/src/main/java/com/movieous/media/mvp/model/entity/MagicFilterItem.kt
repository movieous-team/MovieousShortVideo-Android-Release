package com.movieous.media.mvp.model.entity

import android.graphics.Bitmap

class MagicFilterItem {
    var name: String
    var path: String? = null
    var thumb: String? = null
    var maxFace: Int = 1
    var type: Int = 0
    var resId: Int = 0
    var icon: Bitmap? = null
    var description: Int = 0
    var enabled: Boolean = true
    var start: Float = 0.toFloat()
    var end: Float = 0.toFloat()
    var color: Int = 0
    var vendor: FilterVendor = FilterVendor.FU

    constructor(name: String, path: String) {
        this.name = name
        this.path = path
    }

    constructor(name: String, icon: Bitmap, path: String) {
        this.name = name
        this.icon = icon
        this.path = path
    }

    fun getDuration(): Float {
        return end - start
    }

}
