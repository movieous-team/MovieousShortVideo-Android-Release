package com.movieous.media.mvp.contract

import android.view.View

interface OnItemClickListener {
    /**
     * OnItemClickListener
     *
     * @param view     view
     * @param position position
     */
    fun onItemClick(view: View, position: Int)

    /**
     * On item long click listener
     */
    fun onItemLongClick(view: View, position: Int) : Boolean
}