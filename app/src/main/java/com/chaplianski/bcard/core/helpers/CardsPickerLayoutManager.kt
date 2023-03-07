package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardsPickerLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {
    interface CardScrollStopListener {
        fun selectedView(view: View?)
    }

    private var onScrollStopListener: CardScrollStopListener? = null

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
            scaleCentralView()
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val orientation = orientation
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleCentralView()
            scrolled
        } else 0
    }

    private fun scaleCentralView() {

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            var minGapValue = 0
            var rightBoardCentralView = 0
            var scale = 0f
            val childWidth = child?.width
            val screenWidth = getScreenWidth()
            if (childWidth != null) {
                minGapValue = (screenWidth - childWidth) / 2
                rightBoardCentralView = minGapValue + childWidth
            }
            if (childCount == 1) {
                scale = NORMAL_SCALE
                child?.alpha = NORMAL_SCALE
            } else {
                when (i) {
                    0 -> {
                        if (getChildAt(1)?.left in rightBoardCentralView..screenWidth) {
                            scale = NORMAL_SCALE
                            child?.alpha = NORMAL_SCALE
                        } else {
                            scale = DECREASED_SCALE
                            child?.alpha = DECREASED_ALPHA
                        }
                    }
                    1 -> {
                        if (getChildAt(1)?.left in rightBoardCentralView..screenWidth) {
                            scale = DECREASED_SCALE
                            child?.alpha = DECREASED_ALPHA
                        } else {
                            scale = NORMAL_SCALE
                            child?.alpha = NORMAL_SCALE
                        }
                    }
                    2 -> {
                        scale = DECREASED_SCALE
                        child?.alpha = DECREASED_ALPHA
                    }
                }
            }
            child?.scaleX = scale
            child?.scaleY = scale
        }
    }
    fun getScreenWidth(): Int {
        return Resources.getSystem().getDisplayMetrics().widthPixels
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == 0) {
            if (onScrollStopListener != null) {
                var selected = 0
                var lastHeight = 0f
                for (i in 0 until childCount) {
                    if (lastHeight < getChildAt(i)!!.scaleY) {
                        lastHeight = getChildAt(i)!!.scaleY
                        selected = i
                    }
                }
                onScrollStopListener?.selectedView(getChildAt(selected))
            }
        }
    }
    fun setOnScrollStopListener(onScrollStopListener: CardsPickerLayoutManager.CardScrollStopListener?) {
        this.onScrollStopListener = onScrollStopListener
    }

    companion object{
        val NORMAL_SCALE = 1f
        val DECREASED_SCALE = 0.9f
        val DECREASED_ALPHA = 0.5f
    }
}

