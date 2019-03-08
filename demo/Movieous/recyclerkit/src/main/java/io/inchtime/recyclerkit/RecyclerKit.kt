package io.inchtime.recyclerkit

import android.content.Context


object RecyclerKit {

    var defaultEmptyView: Int = R.layout.recyclerkit_view_empty

    fun adapter(context: Context, spanCount: Int = 1): RecyclerAdapterBuilder {
        return RecyclerAdapterBuilder(context, spanCount)
    }

}