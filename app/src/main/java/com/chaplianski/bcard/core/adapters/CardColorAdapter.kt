package com.chaplianski.bcard.core.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R

class CardColorAdapter (private val beginCardColor: String) : RecyclerView.Adapter<CardColorAdapter.ViewHolder>() {

    private val cardColorList: List<String> = getCardColorsList()
    var selectedPosition = getSelectedPosition(beginCardColor)

    private fun getSelectedPosition(beginCardColor: String): Int {
        var position = 0
        for (color in cardColorList){
            if (color == beginCardColor) return position
            else position++
        }
        return position
    }

    interface ColorCardClickListener{
        fun onShortClick(color: String)
    }

    var colorCardClickListener: ColorCardClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_color_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(cardColorList[position])

        if (selectedPosition == position){
            val colorGradient = GradientDrawable()
            colorGradient.setStroke(20,holder.itemView.context.resources.getColor(R.color.blue_main))
            colorGradient.setColor(cardColorList[position].toColorInt())
            holder.colorImage.background = colorGradient
        }

        holder.itemView.setOnClickListener {

            if (selectedPosition == position){
                selectedPosition = -1
                notifyDataSetChanged()
                return@setOnClickListener
            }
            selectedPosition = position
            notifyDataSetChanged()
            colorCardClickListener?.onShortClick(cardColorList[position])
        }


    }

    override fun getItemCount(): Int {
        return cardColorList.size
    }

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        val colorImage: ImageView = itemView.findViewById(R.id.iv_card_color_item)

        fun onBind(cardColor: String){

            val colorGradient = GradientDrawable()
            colorGradient.setColor(cardColor.toColorInt())
            colorImage.background = colorGradient

        }
    }
}

fun getCardColorsList(): List<String>{

    val colorList = listOf<String>(
        "#DCEDC8", "#E1BEE7", "#B2DFDB", "#F0F4C3", "#FFE0B2", "#D7CCC8", "#FFF9C4", "#B3E5FC",
        "#D7CCC8", "#F5F5F5", "#C8E6C9", "#BDBDBD"
    )
    return colorList

}