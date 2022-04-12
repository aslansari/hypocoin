package com.aslansari.hypocoin.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    // endregion
    // region Member Variables
    protected val HEADER = 0
    protected val ITEM = 1
    protected val FOOTER = 2
    @JvmField
    protected var items: MutableList<T>
    protected var onItemClickListener: OnItemClickListener? = null
        set
    protected var onReloadClickListener: OnReloadClickListener? = null
        set
    protected var onLongClickListener: OnLongClickListener? = null
        set
    var isFooterAdded = false
        protected set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            HEADER -> viewHolder = createHeaderViewHolder(parent)
            ITEM -> viewHolder = createItemViewHolder(parent)
            FOOTER -> viewHolder = createFooterViewHolder(parent)
            else -> {}
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> bindHeaderViewHolder(holder)
            ITEM -> bindItemViewHolder(holder, position)
            FOOTER -> bindFooterViewHolder(holder)
            else -> {}
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // endregion
    // region Abstract Methods
    protected abstract fun createHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder?

    // endregion
    protected abstract fun createItemViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder?
    protected abstract fun createFooterViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder?
    protected abstract fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder?)
    protected abstract fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int)
    protected abstract fun bindFooterViewHolder(viewHolder: RecyclerView.ViewHolder?)
    protected abstract fun displayLoadMoreFooter()
    protected abstract fun displayErrorFooter()
    abstract fun addFooter()

    // region Helper Methods
    fun getItem(position: Int): T {
        return items[position]
    }

    fun add(item: T) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun add(position: Int, item: T) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    fun addAll(items: List<T>) {
        for (item in items) {
            add(item)
        }
    }

    // endregion
    fun addAll(position: Int, items: List<T>) {
        for (item in items) {
            add(position, item)
        }
    }

    fun remove(item: T) {
        val position = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        Timber.d("clear recyclerView")
        isFooterAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun isLastPosition(position: Int): Boolean {
        return position == items.size - 1
    }

    fun removeFooter() {
        isFooterAdded = false
        val position = items.size - 1
        val item: T? = getItem(position)
        if (item != null) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateFooter(footerType: FooterType?) {
        when (footerType) {
            FooterType.LOAD_MORE -> displayLoadMoreFooter()
            FooterType.ERROR -> displayErrorFooter()
            else -> {}
        }
    }



    // region Inner Classes
    enum class FooterType {
        LOAD_MORE, ERROR
    }

    // region Interfaces
    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View?)
    }

    interface OnReloadClickListener {
        fun onReloadClick()
    }

    interface OnLongClickListener {
        fun onItemLongClick(position: Int, view: View?)
    }

    // endregion
    interface ISortable {
        fun sort()
    } // endregion

    companion object {
        // region Constants
    }

    // endregion
    // region Constructors
    init {
        items = ArrayList()
    }
}