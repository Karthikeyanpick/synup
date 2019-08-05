package com.rmkrishna.recycler

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Ref https://medium.com/@saber.solooki/sticky-header-for-recyclerview-c0eb551c3f68
 */
class StickyItemDecoration(var mRecyclerAdapter: FastListAdapter<*>) :
    RecyclerView.ItemDecoration() {

    private var mHeaderHeight: Int? = null

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val headerLayout = mRecyclerAdapter.getHeaderLayout()

        if (headerLayout == RecyclerView.NO_POSITION) {
            return
        }

        val topChild = parent.getChildAt(0)
        topChild?.let { top ->
            var position = parent.getChildAdapterPosition(top)

            if (position == RecyclerView.NO_POSITION) {
                return
            }

            val headerPos = mRecyclerAdapter.getHeaderPositionForItem(position)

            if (headerPos == RecyclerView.NO_POSITION) {
                c.save()
                c.translate(0f, 0f)
                c.restore()
                return
            }

            val currentHeader = getHeaderViewForItem(headerPos, parent)

            fixLayoutSize(parent, currentHeader)

            val contactPoint = currentHeader.bottom
            val childInContact = getChildInContact(parent, contactPoint, headerPos)

            childInContact?.let { topView ->
                val childInContactPos = parent.getChildAdapterPosition(topView)

                if (mRecyclerAdapter.isHeader(childInContactPos)) {
                    moveHeader(c, currentHeader, childInContact)
                    return
                }
            }

            drawHeader(c, currentHeader)
        }
    }

    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {

        val header = LayoutInflater.from(parent.context)
            .inflate(mRecyclerAdapter.getHeaderLayout(), parent, false)

        mRecyclerAdapter.bindHeaderData(header, headerPosition)

        return header
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()

        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())

        currentHeader.draw(c)
        c.restore()
    }

    private fun getChildInContact(
        parent: RecyclerView,
        contactPoint: Int,
        currentHeaderPos: Int
    ): View? {
        var childInContact: View? = null

        for (i in 0 until parent.childCount) {
            var heightTolerance = 0

            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                val isChildHeader = mRecyclerAdapter.isHeader(parent.getChildAdapterPosition(child))
                if (isChildHeader) {
                    heightTolerance = mHeaderHeight?.minus(child.height) as Int
                }
            }

            //add heightTolerance if child top be in display area
            val childBottomPosition: Int
            if (child.top > 0) {
                childBottomPosition = child.bottom + heightTolerance
            } else {
                childBottomPosition = child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)

        mHeaderHeight = view.measuredHeight

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}