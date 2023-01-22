package com.chaplianski.bcard.core.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R

class StrokeColorAdapter (private val beginStrokeColor: String) : RecyclerView.Adapter<StrokeColorAdapter.ViewHolder>() {

    private val strokeColorList = getStrokeColorList()
    var selectedPosition = getSelectedPosition(beginStrokeColor)

    private fun getSelectedPosition(beginStrokeColor: String): Int {
        var position = 0
        for (color in strokeColorList){
            if (color == beginStrokeColor) return position
            else position++
        }
        return position
    }

    interface StrokeColorListener{
        fun onShortClick(strokeColor: String)
    }

    var strokeColorListener: StrokeColorListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.text_color_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorGradient = GradientDrawable()
        colorGradient.setColor(strokeColorList[position].toColorInt())
        holder.strokeColor.background = colorGradient

        if (selectedPosition == position){
            colorGradient.setStroke(20,holder.itemView.context.resources.getColor(R.color.blue_main))
            colorGradient.setColor(strokeColorList[position].toColorInt())
            holder.strokeColor.background = colorGradient
        }


        holder.itemView.setOnClickListener {
//            Log.d("MyLog", "position = $position")
            if (selectedPosition == position){
                selectedPosition = -1
                notifyDataSetChanged()
                return@setOnClickListener
            }
            selectedPosition = position
            notifyDataSetChanged()
            strokeColorListener?.onShortClick(strokeColorList[position])
        }


    }

    override fun getItemCount(): Int {
        return strokeColorList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val strokeColor: ImageView = itemView.findViewById(R.id.iv_stroke_color_item)


    }
}

private fun getStrokeColorList(): List<String> {
    return listOf(
        "#303F9F", "#1976D2", "#0097A7", "#00796B", "#388E3C", "#AFB42B",
        "#FBC02D", "#F57C00", "#E64A19", "#5D4037", "#616161", "#455A64"
    )
}