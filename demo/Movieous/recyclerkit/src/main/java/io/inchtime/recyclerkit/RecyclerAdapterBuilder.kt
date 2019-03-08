package io.inchtime.recyclerkit

import android.app.Activity
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import java.lang.Exception

class RecyclerAdapterBuilder(val context: Context, val spanCount: Int = 1) {

    private var recyclerView: RecyclerView? = null

    private val adapter: RecyclerAdapter = RecyclerAdapter(context, spanCount)

    fun recyclerView(recyclerView: RecyclerView): RecyclerAdapterBuilder {
        this.recyclerView = recyclerView
        this.recyclerView?.adapter = adapter
        return this
    }

    fun recyclerView(recyclerViewResId: Int): RecyclerAdapterBuilder {
        this.recyclerView = (context as Activity).findViewById(recyclerViewResId)
        this.recyclerView?.adapter = adapter
        return this
    }

    /**
     * set a generic or customer layoutManager to recyclerView
     * @param layoutManager Generic LayoutManager
     */
    fun withLayoutManager(layoutManager: RecyclerView.LayoutManager): RecyclerAdapterBuilder {
        if (recyclerView == null) throw Exception("please call recyclerView() function first")
        recyclerView?.layoutManager = layoutManager
        return this
    }

    fun withLinearLayout(orientation: Int = LinearLayoutManager.VERTICAL, reverse: Boolean = false): RecyclerAdapterBuilder {
        if (recyclerView == null) throw Exception("please call recyclerView() function first")
        val layoutManager = LinearLayoutManager(context, orientation, reverse)
        recyclerView?.layoutManager = layoutManager
        return this
    }

    fun withGridLayout(orientation: Int = GridLayoutManager.VERTICAL, reverse: Boolean = false): RecyclerAdapterBuilder {
        if (recyclerView == null) throw Exception("please call recyclerView() function first")
        val layoutManager = GridLayoutManager(context, spanCount, orientation, reverse)
        layoutManager.spanSizeLookup = adapter.getSpanSizeLookup()
        recyclerView?.layoutManager = layoutManager
        return this
    }

    fun withStaggeredGridLayout(orientation: Int = StaggeredGridLayoutManager.VERTICAL): RecyclerAdapterBuilder {
        if (recyclerView == null) throw Exception("please call recyclerView() function first")
        val layoutManager = StaggeredGridLayoutManager(spanCount, orientation)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView?.layoutManager = layoutManager
        return this
    }

    fun selectable(selectable: Boolean, selectionType: RecyclerAdapter.SelectionType = RecyclerAdapter.SelectionType.SINGLE): RecyclerAdapterBuilder {
        adapter.selectable = selectable
        adapter.selectionType = selectionType
        return this
    }

    fun modelViewBind(listener: OnModelViewBind): RecyclerAdapterBuilder {
        adapter.onModelViewBind = listener
        return this
    }

    fun modelViewClick(listener: OnModelViewClick): RecyclerAdapterBuilder {
        adapter.onModelViewClick = listener
        return this
    }

    fun modelViewLongClick(listener: OnModelViewLongClick): RecyclerAdapterBuilder {
        adapter.onModelViewLongClick = listener
        return this
    }

    fun emptyViewBind(listener: OnEmptyViewBind): RecyclerAdapterBuilder {
        adapter.onEmptyViewBind = listener
        return this
    }

    fun build(): RecyclerAdapter {
        return adapter
    }
}