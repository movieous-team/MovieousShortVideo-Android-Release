package com.movieous.media.view

class TextIcon(id: Int, name: String) {
    private var id: Int = id
    private var name: String? = name

    fun getId(): Int {
        return id
    }

    fun getName(): String? {
        return name
    }
}