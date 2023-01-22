package com.chaplianski.bcard.core.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentCardsCardItemBinding
import com.chaplianski.bcard.domain.model.Card

class CardsFragmentCardAdapter(
//    private var cardList: List<Card>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cardList = mutableListOf<Card>()

    interface ShortOnClickListener {
//        fun shortClick()
        fun shortPhoneClick(phone: String)
        fun shortEmailClick(email: String)
        fun shortHomePhoneClick(homePhone: String)
    }

    var shortOnClickListener: ShortOnClickListener? = null

    fun updateData(list: List<Card>) {
        Log.d("MyLog", "list = $list")
        val diffCallback = CardsDiffCallback(cardList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        cardList = list as MutableList<Card>
        diffResult.dispatchUpdatesTo(this)
        this.cardList.clear()
        this.cardList.addAll(list.map { it.copy() })

//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADBLOCK -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_cards_card_add_item, parent, false)
                AddCardViewHolder(v)
            }
            CARD -> {
                val binding = FragmentCardsCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


//                val v = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.fragment_cards_card_item, parent, false)
                CardViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ADBLOCK -> {}
            CARD -> {
                (holder as CardViewHolder).onBindCard(cardList[position])

            }
        }
        holder.itemView.setOnClickListener { recyclerView.smoothScrollToPosition(position) }

        if (getItemViewType(position) == ADBLOCK) {
            (holder as AddCardViewHolder).itemView.setOnClickListener {
//                shortOnClickListener?.shortClick()
            }
        } else {
            (holder as CardViewHolder).phone.setOnClickListener {
                shortOnClickListener?.shortPhoneClick(cardList[position].workPhone)
            }
            holder.email.setOnClickListener {
                shortOnClickListener?.shortEmailClick(cardList[position].email)
            }
            holder.homePhone.setOnClickListener {
                shortOnClickListener?.shortHomePhoneClick(cardList[position].homePhone)
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun getItemViewType(position: Int): Int {
//        val adPosition = position % 20
        return when (position) {
             100  -> ADBLOCK
            else -> CARD

        }



//        return when (position) {
//            cardList.size -> ADD
//            else -> CARD
//        }
    }

    class CardViewHolder(binding: FragmentCardsCardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val phone = binding.tvCardFragmentItemAdditionPhone
        val name = binding.tvCardFragmentItemName
        val specialization = binding.tvCardFragmentItemSpecialization
        val organization = binding.tvCardFragmentItemOrganization
        val workPhone = binding.tvCardFragmentItemPhone
        val homePhone = binding.tvCardFragmentItemAdditionPhone
        val imageHomePhone = binding.ivCardFragmentHomePhone
        val email = binding.tvCardFragmentItemEmail
        val location = binding.tvCardFragmentItemLocation
        val avatar = binding.ivCardFragmentItemAvatar
        val cardLayout: ConstraintLayout = itemView.findViewById(R.id.layout_card_fragment_card)
        val cardId = binding.tvCardFragmentId
        val avatarUri = binding.tvCardFragmentUri
        val cardView = binding.cardviewCardFragmentCard
        val backgroundImage = binding.ivCardFragmentCardBackground



        fun onBindCard(card: Card) {

            name.text = "${card.name} ${card.surname}"
            specialization.text = card.speciality
            organization.text = card.organization
            workPhone.text = card.workPhone
            homePhone.text = card.homePhone
            email.text = card.email
            location.text = "${card.town}, ${card.country}"
            cardId.text = card.id.toString()
            avatarUri.text = card.photo.toString()

//            itemView.background = itemView.context.getDrawable(R.drawable.paper_035)
//            cardView.background = itemView.context.getDrawable(R.drawable.paper_035)
//            cardView.setCardBackgroundResource(ContextCompat.getColor(itemView.context, R.drawable.paper_035))
            cardView.cardElevation = 4f

//            cardView.setBackgroundResource(R.drawable.paper_035)
            backgroundImage.background = AppCompatResources.getDrawable(itemView.context, card.cardTexture)


//            val avatarResource = this.backgroundImage.resources.getIdentifier(avatarId, "drawable", this.backgroundImage.context.packageName)
//            backgroundImage.setImageResource(avatarResource)



            if (card.homePhone.isNullOrEmpty()){
                homePhone.visibility = View.GONE
                imageHomePhone.visibility = View.GONE
            }
//            val colorGradient = GradientDrawable(
//                GradientDrawable.Orientation.TOP_BOTTOM,
//                null
//            )


//            val colorGradient = GradientDrawable(
//                GradientDrawable.Orientation.TOP_BOTTOM,
//                intArrayOf(
//                    itemView.context.resources.getColor(R.color.white),
//                    card.cardColor.toColorInt(),
//                    itemView.context.resources.getColor(R.color.white)
//                )
//            )

//            colorGradient.cornerRadius = card.cornerRound
//            colorGradient.setStroke(20, card.strokeColor.toColorInt())
//            cardLayout.background = colorGradient

            // form photo
            if (card.cardFormPhoto == "oval") {
                Glide.with(itemView.context).load(avatarUri.text.toString())
//                    .override(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.ic_portrait)
                    .circleCrop()
                    .into(avatar)
            } else {
                Glide.with(itemView.context).load(avatarUri.text.toString())
//                    .override(150, 150)
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
        private val ADBLOCK = 1
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
        return oldList[oldItemPosition].id == newList[newItemPosition].id && oldList[oldItemPosition].surname == newList[newItemPosition].surname
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCard = oldList[oldItemPosition]
        val newCard = newList[newItemPosition]

        return oldCard == newCard
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

