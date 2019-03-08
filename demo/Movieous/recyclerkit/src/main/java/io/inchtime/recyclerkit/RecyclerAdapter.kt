package io.inchtime.recyclerkit

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

typealias OnModelViewClick = (index: Int, viewModel: RecyclerAdapter.ViewModel, view: View) -> Unit
typealias OnModelViewLongClick = (index: Int, viewModel: RecyclerAdapter.ViewModel) -> Unit
typealias OnModelViewBind = (index: Int, viewModel: RecyclerAdapter.ViewModel, viewHolder: RecyclerAdapter.ViewHolder) -> Unit
typealias OnEmptyViewBind = (viewHolder: RecyclerAdapter.EmptyViewHolder) -> Unit

class RecyclerAdapter(private val context: Context, private val spanCount: Int = 1) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    companion object {
        const val VIEW_TYPE_EMPTY = Int.MAX_VALUE
    }

    data class ViewModel(
        val layout: Int,
        val spanSize: Int,
        val type: ModelType,
        val value: Any,
        var selected: Boolean = false
    )

    /**
     * identify the position of item in the items
     */
    enum class ModelType(val value: Int) {

        LEADING(0x01),
        MIDDLE(0x02),
        TRAILING(0x04),
        LEADING_TRAILING(0x05);

        companion object {
            fun valueOf(index: Int, @NonNull list: Collection<*>): ModelType {
                if (index < 0) throw IndexOutOfBoundsException()
                if (list.size <= 1) return LEADING_TRAILING
                if (index == 0) return LEADING
                if (index == list.size - 1) return TRAILING
                return MIDDLE
            }
        }
    }

    enum class SelectionType {
        SINGLE,
        MULTI
    }

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    lateinit var recyclerView: RecyclerView

    val viewModels: MutableList<ViewModel> = ArrayList()

    var onModelViewClick: OnModelViewClick? = null

    var onModelViewLongClick: OnModelViewLongClick? = null

    var onModelViewBind: OnModelViewBind? = null

    var onEmptyViewBind: OnEmptyViewBind? = null

    var emptyViewVisibility: Boolean = true

    var emptyView: Int = RecyclerKit.defaultEmptyView

    var selectable: Boolean = false

    var selectionType: SelectionType = SelectionType.SINGLE

    /**
     * set the viewModels of recycler adapter
     * @param models models to display
     */
    fun setModels(models: List<ViewModel>) {
        this.viewModels.clear()
        this.viewModels.addAll(models)
        this.notifyDataSetChanged()
    }

    /**
     * add the models of recycler adapter
     * @param models models to add
     */
    fun addModels(models: List<ViewModel>) {
        this.viewModels.addAll(models)
        this.notifyDataSetChanged()
    }

    /**
     * remove model and notify
     * @param model model to be removed
     */
    fun removeModel(model: ViewModel) {
        val index = this.viewModels.indexOf(model)
        if (index >= 0) {
            this.viewModels.removeAt(index)
            this.notifyItemRemoved(index)
        }
    }

    /**
     * remove model and notify
     * @param index model index to be removed
     */
    fun removeModelAt(index: Int) {
        if (index >= 0 && index < this.viewModels.size) {
            this.viewModels.removeAt(index)
            this.notifyItemRemoved(index)
        }
    }

    /**
     * replace the model and notify
     * @param index model index to be replaced
     * @param viewModel the new model
     */
    fun replaceModel(index: Int, viewModel: ViewModel) {
        if (index >= 0 && index < this.viewModels.size) {
            this.viewModels[index] = viewModel
            this.notifyItemChanged(index)
        }
    }

    /**
     * clear all models
     */
    fun clearModels() {
        this.viewModels.clear()
        this.notifyDataSetChanged()
    }

    val selectedViewModels: List<ViewModel>
        get() {
            return viewModels.filter { viewModel ->
                viewModel.selected
            }
        }

    inline fun <reified T> selectedViewModels() = viewModels.filter { viewModel ->
        viewModel.selected && viewModel.value is T
    }

    inline fun <reified T> selectedModels() = viewModels.filter { viewModel ->
        viewModel.selected && viewModel.value is T
    }.map { viewModel ->
        viewModel.value as T
    }

    fun selectAll() {
        for (viewModel in viewModels) {
            viewModel.selected = true
        }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        for (viewModel in viewModels) {
            viewModel.selected = false
        }
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewModels.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else viewModels[position].layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {

        return if (VIEW_TYPE_EMPTY == type) {
            val view = inflater.inflate(emptyView, parent, false)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            view.visibility = if (emptyViewVisibility) View.VISIBLE else View.INVISIBLE
            val viewHolder = EmptyViewHolder(context, view)
            viewHolder
        } else {
            // type is layout
            // see fun getItemViewType
            val view = inflater.inflate(type, parent, false)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            val viewHolder = ViewHolder(context, this, view)
            viewHolder
        }
    }

    override fun getItemCount(): Int {
        return if (viewModels.isNotEmpty()) viewModels.size else 1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {
            val viewModel = viewModels[position]
            onModelViewBind?.invoke(position, viewModel, viewHolder)
        }

        if (viewHolder is EmptyViewHolder) {
            onEmptyViewBind?.invoke(viewHolder)
        }
    }

    override fun onClick(view: View) {
        val position = recyclerView.getChildAdapterPosition(view)

        if (!viewModels.isEmpty() && position >= 0) {

            val viewModel = viewModels[position]

            if (selectable) {
                // rebind view model
                if (selectionType == SelectionType.SINGLE) {
                    for ((index, model) in viewModels.withIndex()) {
                        if (model == viewModel) continue
                        if (model.selected) {
                            model.selected = false
//                            notifyItemChanged(index)
                            val viewHolder = recyclerView.findViewHolderForAdapterPosition(index)
                            viewHolder?.let {
                                onBindViewHolder(it, index)
                            }
                        }
                    }
                }

//                notifyItemChanged(position)
                viewModel.selected = !viewModel.selected
                val viewHolder = recyclerView.getChildViewHolder(view)
                onBindViewHolder(viewHolder, position)
            }

            onModelViewClick?.invoke(position, viewModel, view)
        }
    }

    override fun onLongClick(view: View): Boolean {
        val position = recyclerView.getChildAdapterPosition(view)

        if (!viewModels.isEmpty() && position >= 0) {
            val model = viewModels[position]
            onModelViewLongClick?.invoke(position, model)
        }
        return true
    }

    fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // empty spanCount must equal to GridLayoutManager's spanCount
                return if (viewModels.isEmpty()) spanCount else viewModels[position].spanSize
            }
        }
    }

    class ViewHolder(val context: Context, val adapter: RecyclerAdapter, val view: View) : RecyclerView.ViewHolder(view) {

        private val views: SparseArray<View> = SparseArray()

        fun <T : View> haveView(key: Int): Boolean {
            var v = views[key]
            if (v == null) {
                v = view.findViewById<T>(key)
                if (v == null) {
                    return false
                }
                views.put(key, v)
            }
            return true
        }

        fun <T : View> findView(key: Int): T {
            var v = views[key]
            if (v == null) {
                v = view.findViewById<T>(key)
                views.put(key, v)
            }
            @Suppress("UNCHECKED_CAST")
            return v as T
        }

    }

    class EmptyViewHolder(val context: Context, val view: View) : RecyclerView.ViewHolder(view)

}