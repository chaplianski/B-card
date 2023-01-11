package com.chaplianski.bcard.core.adapters

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R

//class CardColorAdapter (private val beginCardTexture: Int) : RecyclerView.Adapter<CardColorAdapter.ViewHolder>() {
//
//    private val cardColorList: List<String> = getCardColorsList()
//    private val cardTextureList: List<Int> = getCardTexturesList()
//    var selectedPosition = getSelectedPosition(beginCardTexture)
//
//    private fun getSelectedPosition(beginCardTexture: Int): Int {
//        var position = 0
//        cardTextureList.forEachIndexed { index, texture ->
//            if (texture == beginCardTexture) position = index
//        }
//        return position
//
////        for (texture in cardTextureList){
////            if (texture == beginCardTexture) return position
////            else position++
////        }
////        return position
//    }
//
//    interface ColorCardClickListener{
//        fun onShortClick(texture: Int)
//    }
//
//    var colorCardClickListener: ColorCardClickListener? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_color_item, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.onBind(cardTextureList[position])
//
//
//        if (selectedPosition == position){
//
//
//
//            val colorGradient = GradientDrawable()
//            colorGradient.setStroke(20,holder.itemView.context.resources.getColor(R.color.blue_main))
//            Log.d("MyLog", "selectPosition = $selectedPosition, position = $position")
//            holder.colorImage.background = colorGradient
////            colorGradient.setColor(cardColorList[position].toColorInt())
////            holder.colorImage.background = AppCompatResources.getDrawable(holder.itemView.context, cardTextureList[position])
//        }
//
//        holder.itemView.setOnClickListener {
//
//            if (selectedPosition == position){
//                selectedPosition = -1
//                notifyDataSetChanged()
//                return@setOnClickListener
//            }
//            selectedPosition = position
//            notifyDataSetChanged()
//            colorCardClickListener?.onShortClick(cardTextureList[position])
//        }
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return cardColorList.size
//    }
//
//    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
//
////        val colorImage: ImageView = itemView.findViewById(R.id.iv_card_color_item)
//        val texture: RadioButton = itemView.findViewById(R.id.iv_card_color_item)
//
//        fun onBind(cardTexture: Int){
//
////            colorImage.background = AppCompatResources.getDrawable(itemView.context, cardTexture)
//
//        }
//    }
//}
//
//fun getCardTexturesList(): List<Int>{
//
//    val colorList = listOf<Int>(
//        R.drawable.paper_01,
//        R.drawable.paper_04,
//        R.drawable.paper_07,
//        R.drawable.paper_015,
//        R.drawable.paper_016,
//        R.drawable.paper_019,
////        R.drawable.paper_025,
//        R.drawable.paper_031,
//        R.drawable.paper_033,
//        R.drawable.paper_034,
//        R.drawable.paper_035,
//        R.drawable.paper_06,
//        R.drawable.paper_09
//    )
//    return colorList
//
//}
//
//
//fun getTextureNumber(texture: Int){
//
//}
//
//fun getCardColorsList(): List<String>{
//
//    val colorList = listOf<String>(
//        "#DCEDC8", "#E1BEE7", "#B2DFDB", "#F0F4C3", "#FFE0B2", "#D7CCC8", "#FFF9C4", "#B3E5FC",
//        "#D7CCC8", "#F5F5F5", "#C8E6C9", "#BDBDBD"
//    )
//    return colorList
//
//}