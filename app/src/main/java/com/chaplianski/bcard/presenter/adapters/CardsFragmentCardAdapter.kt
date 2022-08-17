package com.chaplianski.bcard.presenter.adapters

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card

class CardsFragmentCardAdapter(
//    private var cardList: List<Card>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cardList = mutableListOf<Card>()

    interface ShortOnClickListener {
        fun shortClick()
        fun shortPhoneClick(phone: String)
        fun shortEmailClick(email: String)
        fun shortLinkedinClick(linkedin: String)
    }

    var shortOnClickListener: ShortOnClickListener? = null

    fun updateData(list: List<Card>) {
        val diffCallback = CardsDiffCallback(cardList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cardList = list as MutableList<Card>
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_cards_card_add_item, parent, false)
                AddCardViewHolder(v)
            }
            CARD -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_cards_card_item, parent, false)
                CardViewHolder(v)
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ADD -> {}
            CARD -> {
                (holder as CardViewHolder).onBindCard(cardList[position])
            }
        }
        holder.itemView.setOnClickListener { recyclerView.smoothScrollToPosition(position) }

        if (getItemViewType(position) == ADD) {
            (holder as AddCardViewHolder).itemView.setOnClickListener {
                shortOnClickListener?.shortClick()
            }
        } else {
            (holder as CardViewHolder).phone.setOnClickListener {
                shortOnClickListener?.shortPhoneClick(cardList[position].phone)
            }
            holder.email.setOnClickListener {
                shortOnClickListener?.shortEmailClick(cardList[position].email)
            }
            holder.linkedin.setOnClickListener {
                shortOnClickListener?.shortLinkedinClick(cardList[position].linkedin)
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            cardList.size -> ADD
            else -> CARD
        }
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val phone: TextView = itemView.findViewById(R.id.tv_card_fragment_item_phone)
        val name: TextView = itemView.findViewById(R.id.tv_card_fragment_item_name)
        val specialization: TextView =
            itemView.findViewById(R.id.tv_card_fragment_item_specialization)
        val organization: TextView = itemView.findViewById(R.id.tv_card_fragment_item_organization)
        val linkedin: TextView = itemView.findViewById(R.id.tv_card_fragment_item_addition_phone)
        val email: TextView = itemView.findViewById(R.id.tv_card_fragment_item_email)
        val location: TextView = itemView.findViewById(R.id.tv_card_fragment_item_location)
        val avatar: ImageView = itemView.findViewById(R.id.iv_card_fragment_item_avatar)
        val cardLayout: ConstraintLayout = itemView.findViewById(R.id.layout_card_fragment_card)
        val cardId: TextView = itemView.findViewById(R.id.tv_card_fragment_id)
        val avatarUri: TextView = itemView.findViewById(R.id.tv_card_fragment_uri)


        fun onBindCard(card: Card) {

            name.text = "${card.name} ${card.surname}"
            specialization.text = card.speciality
            organization.text = card.organization
            phone.text = card.phone
            linkedin.text = card.linkedin
            email.text = card.email
            location.text = "${card.town}, ${card.country}"
            cardId.text = card.id.toString()
            avatarUri.text = card.photo.toString()


            val colorGradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    itemView.context.resources.getColor(R.color.white),
                    card.cardColor.toColorInt(),
                    itemView.context.resources.getColor(R.color.white)
                )
            )
//            colorGradient.setColor(card.cardColor.toColorInt())


            colorGradient.cornerRadius = card.cornerRound
            colorGradient.setStroke(20, card.strokeColor.toColorInt())
            cardLayout.background = colorGradient

            // form photo
            if (card.formPhoto == "oval") {
                Glide.with(itemView.context).load(avatarUri.text.toString())
                    .override(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.ic_portrait)
                    .circleCrop()
                    .into(avatar)
            } else {
                Glide.with(itemView.context).load(avatarUri.text.toString())
                    .override(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.ic_portrait)
                    .into(avatar)
            }
        }
    }

    class AddCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    companion object {
        private val CARD = 0
        private val ADD = 1
    }

}

class CardsDiffCallback(
    val oldList: List<Card>,
    val newList: List<Card>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCard = oldList[oldItemPosition]
        val newCard = newList[newItemPosition]

        return oldCard == newCard
    }
}

