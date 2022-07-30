package com.chaplianski.bcard.presenter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card

class CardsFragmentCardAdapter (private val cardList: List<Card>, private val recyclerView: RecyclerView): RecyclerView.Adapter<CardsFragmentCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when(viewType){
            ADD -> R.layout.fragment_cards_card_item
            CARD -> R.layout.fragment_cards_card_item
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(getItemViewType(position)){
            ADD -> holder.onBindAdd()
            CARD -> holder.onBindCard(cardList[position])
        }

        Log.d("Log", "adapter ${holder.itemView.id}")
        holder.itemView.setOnClickListener { recyclerView.smoothScrollToPosition(position) }
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

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        fun onBindCard(card: Card){

            val name: TextView = itemView.findViewById(R.id.tv_card_fragment_name)
            val specialization: TextView = itemView.findViewById(R.id.tv_card_fragment_specialization)
            val phone: TextView = itemView.findViewById(R.id.tv_card_fragment_phone)
            val addPhone: TextView = itemView.findViewById(R.id.tv_card_fragment_addition_phone)
            val email: TextView = itemView.findViewById(R.id.tv_card_fragment_email)
            val location: TextView = itemView.findViewById(R.id.tv_card_fragment_location)
            val avatar: ImageView = itemView.findViewById(R.id.iv_card_fragment_avatar)

            name.text = card.name
            specialization.text = card.speciality
            phone.text = card.phone
            addPhone.text = card.additionPhone
            email.text = card.email
            location.text = card.location

            Glide.with(itemView.context).load(R.drawable.avatar)
                .override(150, 150)
                .centerCrop()
                .circleCrop()
                .into(avatar)

        }

        fun onBindAdd(){

        }



    }

    companion object {
        private val CARD = 0
        private val ADD = 1
    }

}

