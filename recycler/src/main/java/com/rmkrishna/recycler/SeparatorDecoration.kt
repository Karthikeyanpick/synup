package com.rmkrishna.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

class SeparatorDecoration(
    @ColorInt color: Int,
    width: Float = 2.0f,
    val marginLeft: Float = 0f,
    val marginRight: Float = 0f
) : RecyclerView.ItemDecoration() {

    lateinit var mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.color = color
        mPaint.strokeWidth = width
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val params = view.layoutParams as RecyclerView.LayoutParams

        val position = params.viewAdapterPosition

        outRect.set(0, 0, 0, mPaint.getStrokeWidth().toInt())
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // we set the stroke width before, so as to correctly draw the line we have to offset by width / 2
        val offset = (mPaint.strokeWidth / 2).toInt()

        // this will iterate over every visible view
        for (i in 0 until parent.childCount) {
            // get the view
            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams

            // get the position
            val position = params.viewAdapterPosition

            // and finally draw the separator
            if (position < state.itemCount) {
                c.drawLine(
                    view.left + marginLeft, (view.bottom + offset).toFloat(),
                    view.right - marginRight, (view.bottom + offset).toFloat(), mPaint
                )
            }
        }
    }
}