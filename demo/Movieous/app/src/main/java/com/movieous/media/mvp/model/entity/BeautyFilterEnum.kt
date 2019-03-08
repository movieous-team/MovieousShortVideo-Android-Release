package com.movieous.media.mvp.model.entity

import com.faceunity.entity.Filter
import com.movieous.media.R
import java.util.*

/**
 * Created by tujh on 2018/1/30.
 */

enum class BeautyFilterEnum private constructor(private val filterName: String, private val resId: Int, private val description: Int, private val filterType: Int) {

    // filter
    nature("origin", R.drawable.nature, R.string.origin, Filter.FILTER_TYPE_FILTER),
    delta("delta", R.drawable.delta, R.string.delta, Filter.FILTER_TYPE_FILTER),
    electric("electric", R.drawable.electric, R.string.electric, Filter.FILTER_TYPE_FILTER),
    slowlived("slowlived", R.drawable.slowlived, R.string.slowlived, Filter.FILTER_TYPE_FILTER),
    tokyo("tokyo", R.drawable.tokyo, R.string.tokyo, Filter.FILTER_TYPE_FILTER),
    warm("warm", R.drawable.warm, R.string.warm, Filter.FILTER_TYPE_FILTER),

    // beauty filter
    nature_beauty("origin", R.drawable.nature, R.string.origin_beauty, Filter.FILTER_TYPE_BEAUTY_FILTER),
    ziran("ziran", R.drawable.origin, R.string.ziran, Filter.FILTER_TYPE_BEAUTY_FILTER),
    danya("danya", R.drawable.qingxin, R.string.danya, Filter.FILTER_TYPE_BEAUTY_FILTER),
    fennen("fennen", R.drawable.shaonv, R.string.fennen, Filter.FILTER_TYPE_BEAUTY_FILTER),
    qingxin("qingxin", R.drawable.ziran, R.string.qingxin, Filter.FILTER_TYPE_BEAUTY_FILTER),
    hongrun("hongrun", R.drawable.hongrun, R.string.hongrun, Filter.FILTER_TYPE_BEAUTY_FILTER);

    fun filterName(): String {
        return filterName
    }

    fun resId(): Int {
        return resId
    }

    fun description(): Int {
        return description
    }

    fun filter(): Filter {
        return Filter(filterName, resId, description, filterType)
    }

    companion object {

        fun getFiltersByFilterType(filterType: Int): ArrayList<Filter> {
            val filters = ArrayList<Filter>()
            for (f in BeautyFilterEnum.values()) {
                if (f.filterType == filterType) {
                    filters.add(f.filter())
                }
            }
            return filters
        }
    }
}
