package com.movieous.media.view.recyclerview.adapter

/**
 * Adapter条目的长按事件
 */
interface OnItemLongClickListener {

    fun onItemLongClick(obj: Any?, position: Int): Boolean
}
