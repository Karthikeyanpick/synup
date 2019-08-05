package com.rmkrishna.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Radoslav Yankov on 29.06.2018
 * radoslavyankov@gmail.com
 *
 * Updated by Muthukrishnan for Sticky Header RecyclerView
 */
open class RecycleScrollListener : RecyclerView.OnScrollListener() {
    private var _onScrollStateChanged: (recyclerView: RecyclerView?, newState: Int) -> Unit =
        { _, _ -> }
    private var _onScrolled: (recyclerView: RecyclerView?, dx: Int, dy: Int) -> Unit =
        { _, _, _ -> }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        _onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        _onScrolled(recyclerView, dx, dy)
    }

    fun onScrollStateChanged(function: (recyclerView: RecyclerView?, newState: Int) -> Unit) {
        _onScrollStateChanged = function
    }

    fun onScrolled(function: (recyclerView: RecyclerView?, dx: Int, dy: Int) -> Unit) {
        _onScrolled = function
    }
}

/**
 * Dynamic list bind function. It should be followed by one or multiple .map calls.
 * @param items - Generic list of the items to be displayed in the list
 */
fun <T> RecyclerView.bind(
    items: List<T>,
    color: Int = -1,
    width: Float = 2.0f
): FastListAdapter<T> {
    layoutManager = LinearLayoutManager(context)
    var fastListAdapter = FastListAdapter(items.toMutableList(), this)

    this.addItemDecoration(StickyItemDecoration(fastListAdapter))

    if (color != -1) {
        this.addItemDecoration(SeparatorDecoration(color, width))
    }

    return fastListAdapter
}

/**
 * Simple list bind function.
 * @param items - Generic list of the items to be displayed in the list
 * @param singleLayout - The layout that will be used in the list
 * @param singleBind - The "binding" function between the item and the layout. This is the standard "bind" function in traditional ViewHolder classes. It uses Kotlin Extensions
 * so you can just use the XML names of the views inside your layout to address them.
 */
fun <T> RecyclerView.bind(
    items: List<T>, @LayoutRes singleLayout: Int = 0,
    singleBind: (View.(item: T) -> Unit)
): FastListAdapter<T> {
    layoutManager = LinearLayoutManager(context)

    var fastListAdapter = FastListAdapter(
        items.toMutableList(), this
    ).map(singleLayout, { true }, singleBind)

    this.addItemDecoration(StickyItemDecoration(fastListAdapter))

    return fastListAdapter
}


/**
 * Updates the list using DiffUtils.
 * @param newItems the new list which is to replace the old one.
 *
 * NOTICE: The comparator currently checks if items are literally the same. You can change that if you want,
 * by changing the lambda in the function
 */
fun <T> RecyclerView.update(newItems: List<T>) {
    (adapter as? FastListAdapter<T>)?.update(newItems) { o, n -> o == n }
}


open class FastListAdapter<T>(
    private var items: MutableList<T>, private var list: RecyclerView
) : RecyclerView.Adapter<FastListViewHolder<T>>() {

    var HEADER = -1

    private inner class BindMap(
        val layout: Int,
        var type: Int = 0,
        val bind: View.(item: T) -> Unit,
        val predicate: (item: T) -> Boolean
    )

    private var bindMap = mutableListOf<BindMap>()
    private var typeCounter = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FastListViewHolder<T> {
        return bindMap.first {
            it.type == viewType
        }
            .let {
                FastListViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        it.layout,
                        parent, false
                    ), viewType
                )
            }
    }

    fun getItem(position: Int) = items.get(position)

    override fun onBindViewHolder(holder: FastListViewHolder<T>, position: Int) {
        val item = items.get(position)
        holder.bind(item, bindMap.first { it.type == holder.holderType }.bind)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = try {
        bindMap.first {
            it.predicate(items[position])
        }.type
    } catch (e: Exception) {
        0
    }

    /**
     * The function used for mapping types to layouts
     * @param layout - The ID of the XML layout of the given type
     * @param predicate - Function used to sort the items. For example, a Type field inside your items class with different values for different types.
     * @param bind - The "binding" function between the item and the layout. This is the standard "bind" function in traditional ViewHolder classes. It uses Kotlin Extensions
     * so you can just use the XML names of the views inside your layout to address them.
     */
    fun map(
        @LayoutRes layout: Int, predicate: (item: T) -> Boolean,
        bind: View.(item: T) -> Unit
    ): FastListAdapter<T> {
        bindMap.add(BindMap(layout, typeCounter++, bind, predicate))
        list.adapter = this
        return this
    }

    fun mapHeader(
        @LayoutRes layout: Int, predicate: (item: T) -> Boolean,
        bind: View.(item: T) -> Unit
    ): FastListAdapter<T> {

        HEADER = typeCounter++

        bindMap.add(BindMap(layout, HEADER, bind, predicate))
        list.adapter = this

        return this
    }

    fun getHeaderLayout() = try {
        bindMap.first {
            it.type == HEADER
        }.layout
    } catch (e: Exception) {
        -1
    }

    fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition: Int = -1

        for (i in itemPosition downTo 0) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }

        return headerPosition
    }

    fun isHeader(itemPosition: Int) = getItemViewType(itemPosition) == HEADER

    fun bindHeaderData(header: View, headerPosition: Int) {
        val func = bindMap.first { it.type == HEADER }.bind

        header.apply {
            func(items[headerPosition])
        }
    }

    /**
     * Sets up a layout manager for the recycler view.
     */
    fun layoutManager(manager: RecyclerView.LayoutManager): FastListAdapter<T> {
        list.layoutManager = manager
        return this
    }

    fun update(newList: List<T>, compare: (T, T) -> Boolean) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(items[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = items.size

            override fun getNewListSize() = newList.size
        })
        items = newList.toMutableList()

        diff.dispatchUpdatesTo(this)
    }
}

class FastListViewHolder<T>(override val containerView: View, val holderType: Int) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(entry: T, func: View.(item: T) -> Unit) {
        containerView.apply {
            func(entry)
        }
    }
}