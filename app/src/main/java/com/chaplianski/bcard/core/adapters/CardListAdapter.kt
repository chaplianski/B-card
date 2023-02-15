package com.chaplianski.bcard.core.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.AD_FREQUENCY
import com.chaplianski.bcard.core.utils.AD_POSITION
import com.chaplianski.bcard.core.utils.AVATAR_FORM_OVAL
import com.chaplianski.bcard.core.utils.RESOURCE_TYPE_DRAWABLE
import com.chaplianski.bcard.databinding.FragmentCardsAdItemBinding
import com.chaplianski.bcard.databinding.FragmentCardsCardItemBinding
import com.chaplianski.bcard.domain.model.Card


class CardListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private val adItems: MutableList<NativeAd>
//
//    init {
//        adItems = ArrayList()
//    }
//
//    private var myResult: MyResult? = null
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    interface ShortOnClickListener {
        //        fun shortClick()
        fun shortPhoneClick(phone: String)
        fun shortEmailClick(email: String)
        fun shortHomePhoneClick(homePhone: String)
    }

    var shortOnClickListener: ShortOnClickListener? = null

    fun isCard(position: Int) = (position + 1) % AD_FREQUENCY != 0

    fun getCardForPosition(position: Int): Card? {
        val offset = position / AD_FREQUENCY
        return if (isCard(position)) differ.currentList[position - offset] else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CARD_VIEWTYPE -> {
                val binding = FragmentCardsCardItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CardViewHolder(binding)
            }
            AD_VIEWTYPE -> {
                val binding = FragmentCardsAdItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AdViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
//
//        binding = FragmentCardsCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val card = getCardForPosition(position)
        when (card) {
            null -> {
//                val adHolder = holder as AdViewHolder
//                var ad: NativeAd? = null
//                if (adItems.size > position / AD_DISPLAY_FREQUENCY) {
//                    ad = adItems[position / AD_DISPLAY_FREQUENCY]
//                } else {
//                    val nativeAdOptions =
//                        NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.LANDSCAPE)
//                            .build()
//                    val builder = AdLoader.Builder(
//                        adHolder.binding.root.context,
//                        "ca-app-pub-3940256099942544/2247696110"
//                    )
//                    val adLoader: AdLoader = builder.forNativeAd { nativeAd ->
//                        ad = nativeAd
//                        adItems.add(nativeAd)
//                    }.withNativeAdOptions(nativeAdOptions)
//                        .withAdListener(object : AdListener() {
//                            override fun onAdFailedToLoad(p0: LoadAdError) {
//                                Log.d(TAG, "onAdFailedToLoad: Failed : ${p0.message}")
//                            }
//                        })
//                        .build()
//                    adLoader.loadAd(AdRequest.Builder().build())
//                }
//                ad?.let { nativeAd ->
//                    adHolder.binding.run {
//                        adHeadline.text = nativeAd.headline
//                        adPrice.text = nativeAd.price
//                        adStore.text = nativeAd.store
//                        adAdvertiser.text = nativeAd.advertiser
//                        adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
//                        nativeAdView.setNativeAd(nativeAd)
//                    }
//                }
            }
//            else {
//                val index = position - position / AD_DISPLAY_FREQUENCY - 1
//                val item = itemList[index]
//                val itemHolder = holder as ItemHolder
//
//            }

            else -> {
                (holder as CardViewHolder).onBind(card)
//                Log.d("MyCard", "card = $card")
                holder.binding.tvCardFragmentItemWorkPhone.setOnClickListener {
                    shortOnClickListener?.shortPhoneClick(card.workPhone)
                }
                holder.binding.tvCardFragmentItemEmail.setOnClickListener {
                    shortOnClickListener?.shortEmailClick(card.email)
                }
                holder.binding.tvCardFragmentItemHomePhone.setOnClickListener {
                    shortOnClickListener?.shortHomePhoneClick(card.homePhone)
                }
            }
        }

    }

//    fun clearResult() {
//        myResult = null
//        notifyDataSetChanged()
//    }
//
//    fun setResult(myResult : MyResult) {
//        this.myResult = myResult
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int {
        return differ.currentList.size + (differ.currentList.size / AD_POSITION)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCard(position)) CARD_VIEWTYPE else AD_VIEWTYPE
    }

    inner class CardViewHolder(val binding: FragmentCardsCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(card: Card) {
//            Log.d("MyLog", "card in holder = $card")
            binding.apply {


                tvCardFragmentId.text = card.id.toString()
                tvCardFragmentItemWorkPhone.text = card.workPhone
                tvCardFragmentItemHomePhone.text = card.homePhone
                tvCardFragmentItemSpecialization.text = card.speciality
                tvCardFragmentItemOrganization.text = card.organization
                tvCardFragmentItemEmail.text = card.email
                tvCardFragmentItemName.text = itemView.context.getString(R.string.two_value_without_comma,
                    card.surname,
                    card.name)



                //** Location
                when {
                    card.town.isEmpty() && card.country.isNotEmpty() -> {
                        tvCardFragmentItemLocation.text = card.country
                    }
                    card.town.isNotEmpty() && card.country.isEmpty() -> {
                        tvCardFragmentItemLocation.text = card.town
                    }
                    card.town.isNotEmpty() && card.country.isNotEmpty() -> {
                        tvCardFragmentItemLocation.text = itemView.context.getString(
                            R.string.two_value_with_comma,
                            card.town,
                            card.country
                        )
                    }
                    else -> {
//                        tvCardFragmentItemLocation.isVisible = false
//                        ivCardFragmentLocation.isVisible = false
                    }
                }

                val textureResource = ivCardFragmentCardBackground.resources.getIdentifier(
                    card.cardTexture,
                    RESOURCE_TYPE_DRAWABLE,
                    ivCardFragmentCardBackground.context.packageName
                )
                ivCardFragmentCardBackground.setImageResource(textureResource)

                val currentCardTextColor = card.cardTextColor
                tvCardFragmentItemName.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemSpecialization.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemOrganization.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemWorkPhone.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemHomePhone.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemEmail.setTextColor(Color.parseColor(currentCardTextColor))
                tvCardFragmentItemLocation.setTextColor(Color.parseColor(currentCardTextColor))
                val currentColor = Color.parseColor(currentCardTextColor)
                ivCardFragmentHomePhone.setColorFilter(currentColor)
                ivCardFragmentWorkPhone.setColorFilter(currentColor)
                ivCardFragmentEmail.setColorFilter(currentColor)
                ivCardFragmentLocation.setColorFilter(currentColor)

//                if (card.homePhone.isNullOrEmpty()) {
//                    tvCardFragmentItemHomePhone.visibility = View.GONE
//                    ivCardFragmentHomePhone.visibility = View.GONE
//                }
//
//                if (card.workPhone.isNullOrEmpty()) {
//                    tvCardFragmentItemWorkPhone.visibility = View.GONE
//                    ivCardFragmentWorkPhone.visibility = View.GONE
//                }
//
//                if (card.email.isNullOrEmpty()) {
//                    tvCardFragmentItemEmail.visibility = View.GONE
//                    ivCardFragmentEmail.visibility = View.GONE
//                }

                if (card.isCardCorner) cardviewCardFragmentCard.radius = 40f

                if (card.cardFormPhoto == AVATAR_FORM_OVAL) {

                    Glide.with(itemView.context).load(card.photo)
                        .circleCrop()
                        .placeholder(R.drawable.ic_portrait)
                        .into(ivCardFragmentItemAvatar)
                } else {
                    Glide.with(itemView.context).load(card.photo)
                        .centerCrop()
                        .placeholder(R.drawable.ic_portrait)
                        .into(ivCardFragmentItemAvatar)
                }
            }
        }
    }

    inner class AdViewHolder(binding: FragmentCardsAdItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


    private val differCallback = object : DiffUtil.ItemCallback<Card>() {

        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean  = oldItem == newItem

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
                     return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

    companion object {
        val CARD_VIEWTYPE = 1
        val AD_VIEWTYPE = 2
    }
}