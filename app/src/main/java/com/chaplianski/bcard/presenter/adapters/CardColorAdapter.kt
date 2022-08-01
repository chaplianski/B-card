package com.chaplianski.bcard.presenter.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.CardColor

class CardColorAdapter () : RecyclerView.Adapter<CardColorAdapter.ViewHolder>() {

    private val cardColorList: List<CardColor> = getCardColorsList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_color_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(cardColorList[position])
    }

    override fun getItemCount(): Int {
        return cardColorList.size
    }

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        val colorImage: ImageView = itemView.findViewById(R.id.iv_card_color_item)

        fun onBind(cardColor: CardColor){

            val colorGradient = GradientDrawable()
            colorGradient.setColor(cardColor.secondColor.toColorInt())
            colorImage.background = colorGradient

        }
    }
}

fun getCardColorsList(): List<CardColor>{
    val colorsList = mutableListOf<CardColor>()
    colorsList.add(CardColor(0,"#E64A19","#FFCDD2","#616161" ))
    colorsList.add(CardColor(1,"#D32F2F","#E1BEE7", "#455A64" ))
    colorsList.add(CardColor(2,"#7B1FA2", "#B2DFDB","#F57C00" ))
    colorsList.add(CardColor(3,"#303F9F","#F0F4C3","#D32F2F" ))
    colorsList.add(CardColor(4,"#1976D2", "#FFE0B2","#F57C00" ))
    colorsList.add(CardColor(5,"#0097A7","#D7CCC8","#303F9F" ))
    colorsList.add(CardColor(6,"#00796B", "#FFF9C4","#E64A19" ))
    colorsList.add(CardColor(7,"#388E3C","#B2DFDB","#7B1FA2" ))
    colorsList.add(CardColor(8,"#AFB42B","#FFCCBC","#D32F2F" ))
    colorsList.add(CardColor(9,"#FBC02D","#E1BEE7","#AFB42B" ))
    return colorsList.toList()









}