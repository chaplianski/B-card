package com.chaplianski.bcard.presenter.helpers

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardsPickerLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {


    var scaleDownBy = 0.9f
    var scaleDownDistance = 1.8f
    var isChangeAlpha = true

    interface CardScrollStopListener {
        fun selectedView(view: View?)
    }

    private var onScrollStopListener: CardScrollStopListener? = null

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        //      scaleDownView(0)
        scaleCentralView()
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
//            scaleDownView(dx)
//            Log.d("MyLog", " dx: ${dx}")
            scrolled
        } else 0
    }

    private fun scaleCentralView() {

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val originalPos = IntArray(2)
            val location = getChildAt(i)!!.getLocationOnScreen(originalPos)
            val xLocation = originalPos[0]
            val yLocation = originalPos[1]
            var scale = 0f
            if (xLocation in 51..899) {
                scale = 1f
                child?.alpha = 1f
            } else {

                scale = scaleDownBy
                child?.alpha = 0.4f
            }


            child?.scaleX = scale
            child?.scaleY = scale
        }
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


    fun setOnScrollStopListener(onScrollStopListener: CardScrollStopListener?) {
        this.onScrollStopListener = onScrollStopListener
    }

}