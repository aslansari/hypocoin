package com.aslansari.hypocoin.currency.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class MarginItemDecorator(private val marginVertical: Int, private val marginHorizontal: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = marginVertical
        }
        outRect.left = marginHorizontal
        outRect.right = marginHorizontal
        outRect.bottom = marginVertical
    }
}