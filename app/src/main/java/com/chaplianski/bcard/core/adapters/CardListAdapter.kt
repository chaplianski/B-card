package com.chaplianski.bcard.core.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.databinding.FragmentCardsAdItemBinding
import com.chaplianski.bcard.databinding.FragmentCardsCardItemBinding
import com.chaplianski.bcard.databinding.LayoutAdToCardBinding
import com.chaplianski.bcard.domain.model.Card
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class CardListAdapter (val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val view: View = View.inflate(context, R.layout.layout_ad_to_card, null)
    var adView: NativeAdView = view.findViewById(R.id.adView_ad_to_card)
    init{
        loadAdv()
    }

    interface ShortOnClickListener {
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
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val card = getCardForPosition(position)
        when (card) {
            null -> {
                (holder as AdViewHolder).onBind()
            }
            else -> {
                (holder as CardViewHolder).onBind(card)
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

    override fun getItemCount(): Int {
        return differ.currentList.size + (differ.currentList.size / AD_POSITION)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCard(position)) CARD_VIEWTYPE else AD_VIEWTYPE
    }

    inner class CardViewHolder(val binding: FragmentCardsCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(card: Card) {
            binding.apply {
                tvCardFragmentId.text = card.id.toString()
                tvCardFragmentItemWorkPhone.text = card.workPhone
                tvCardFragmentItemHomePhone.text = card.homePhone
                tvCardFragmentItemSpecialization.text = card.speciality
                tvCardFragmentItemOrganization.text = card.organization
                tvCardFragmentItemEmail.text = card.email
                tvCardFragmentItemName.text = itemView.context.getString(
                    R.string.two_value_without_comma,
                    card.surname,
                    card.name
                )

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
                        tvCardFragmentItemLocation.text = ""
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

                if (card.email.isNullOrEmpty()) {
                    tvCardFragmentItemEmail.text = ""
                }

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

    inner class AdViewHolder(val binding: FragmentCardsAdItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adContainerView = binding.layoutCardFragmentAdContainer
        fun onBind() {
            if (adView.parent != null) {
                (adView.parent as ViewGroup).removeView(adView)
            }
            adContainerView.isVisible = adView.bodyView != null
            if (adView.bodyView == null) loadAdv()
            adContainerView.addView(adView)
        }
    }

    private fun loadAdv(){
        val binding = LayoutAdToCardBinding.inflate(LayoutInflater.from(context))
        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
            .forNativeAd { nativeAd ->
                adView = populateNativeAdView(nativeAd, binding)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MyLog", "is failure")
                }
            })
            .build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adViews: LayoutAdToCardBinding): NativeAdView {

        val adView = adViews.root
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById<View>(R.id.ad_headline)
        adView.bodyView = adView.findViewById<View>(R.id.ad_body)
        adView.callToActionView = adView.findViewById<View>(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById<View>(R.id.ad_app_icon)
        adView.priceView = adView.findViewById<View>(R.id.ad_price)
        adView.starRatingView = adView.findViewById<View>(R.id.ad_stars)
        adView.storeView = adView.findViewById<View>(R.id.ad_store)
        adView.advertiserView = adView.findViewById<View>(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as? TextView)?.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as? Button)?.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as? ImageView)?.setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as? TextView)?.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as? TextView)?.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as? RatingBar)?.rating =
                nativeAd.starRating!!.toFloat()//.floatValue()
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as? TextView)?.text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
        return adView
    }

    private val differCallback = object : DiffUtil.ItemCallback<Card>() {

        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean = oldItem == newItem

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

