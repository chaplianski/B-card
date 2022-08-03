package com.chaplianski.bcard.presenter.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card

class CardsFragmentCardAdapter (private val cardList: List<Card>, private val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ShortOnClickListener {
        fun shortClick()
        fun shortPhoneClick(phone: String)
        fun shortEmailClick(email: String)
        fun shortLinkedinClick(linkedin: String)
    }
    var shortOnClickListener: ShortOnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ADD -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_cards_card_add_item, parent, false)
                AddCardViewHolder(v)
            }
            CARD -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_cards_card_item, parent, false)
                CardViewHolder(v)
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            ADD -> {}
            CARD -> {
                (holder as CardViewHolder).onBindCard(cardList[position])
            }


        }
        holder.itemView.setOnClickListener { recyclerView.smoothScrollToPosition(position) }

        if (getItemViewType(position) == ADD){
            (holder as AddCardViewHolder).itemView.setOnClickListener {
            shortOnClickListener?.shortClick()
            }
        } else {
            (holder as CardViewHolder).phone.setOnClickListener {
                shortOnClickListener?.shortPhoneClick(cardList[position].phone)
            }
            holder.email.setOnClickListener{
                shortOnClickListener?.shortEmailClick(cardList[position].email)
            }
            holder.linkedin.setOnClickListener {
                shortOnClickListener?.shortLinkedinClick(cardList[position].linkedin)
            }
        }


    }

    override fun getItemCount(): Int {
        return cardList.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            cardList.size  -> ADD
            else -> CARD
        }
    }

    class CardViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        val phone: TextView = itemView.findViewById(R.id.tv_card_fragment_item_phone)
        val name: TextView = itemView.findViewById(R.id.tv_card_fragment_item_name)
        val specialization: TextView = itemView.findViewById(R.id.tv_card_fragment_item_specialization)
        val linkedin: TextView = itemView.findViewById(R.id.tv_card_fragment_item_addition_phone)
        val email: TextView = itemView.findViewById(R.id.tv_card_fragment_item_email)
        val location: TextView = itemView.findViewById(R.id.tv_card_fragment_item_location)
        val avatar: ImageView = itemView.findViewById(R.id.iv_card_fragment_item_avatar)
        val cardLayout: ConstraintLayout = itemView.findViewById(R.id.layout_card_fragment_card)

        fun onBindCard(card: Card){

            name.text = card.name
            specialization.text = card.speciality
            phone.text = card.phone
            linkedin.text = card.linkedin
            email.text = card.email
            location.text = card.location


//            val gfgGradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
//                intArrayOf(
//                    0XFFD98880.toInt(),
//                    0XFFF4D03F.toInt(),
//                    0XFF48C9B0.toInt()
//                ))

//           val color =

            val gfgGradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    itemView.context.resources.getColor(R.color.accent_2),
                    0XFFF4D03F.toInt(),
                    itemView.context.resources.getColor(R.color.accent_2)
                ))

            val colorA = ContextCompat.getDrawable(itemView.context, R.color.accent_2)
            val drawStroke = GradientDrawable()

//            drawStroke.setColor(itemView.context.getResources().getColor(R.color.accent_2))
            drawStroke.alpha = 80
            gfgGradient.cornerRadius = 30f
            gfgGradient.setStroke(20, itemView.context.resources.getColor(R.color.blue_main))
//            drawStroke.setTint(R.color.accent_3)
//            drawStroke.setTintMode(null)
            cardLayout.background = gfgGradient


            Glide.with(itemView.context).load(R.drawable.avatar)
                .override(150, 150)
                .centerCrop()
                .circleCrop()
                .into(avatar)

        }

        fun onBindAdd(){
            val addLayout: ConstraintLayout = itemView.findViewById(R.id.cl_card_fragment_card_add)
        }

    }

    class AddCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    companion object {
        private val CARD = 0
        private val ADD = 1
    }

}

