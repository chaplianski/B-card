package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.AD_UNIT_ID
import com.chaplianski.bcard.databinding.LayoutAdToCardBinding
import com.chaplianski.bcard.databinding.LayoutAdToFullScreenBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AdsPicker() {

    fun setAdNative(context: Context, adContainerView: FrameLayout){
        val binding = LayoutAdToFullScreenBinding.inflate(LayoutInflater.from(context))
        val adOptions = NativeAdOptions.Builder()
            .setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
            .build()
        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
            .forNativeAd { nativeAd ->
                Log.d("MyLog", "is success")
                val adView = populateNativeAdView(nativeAd, binding)
                adContainerView.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MyLog", "is failure")
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }



    private fun populateNativeAdView(nativeAd: NativeAd, adViews: LayoutAdToFullScreenBinding): NativeAdView {

        val adView = adViews.root
        adView.mediaView = adView.findViewById(R.id.ad_media_full_size)
        adView.headlineView = adView.findViewById<View>(R.id.ad_headline_full_size)
        adView.bodyView = adView.findViewById<View>(R.id.ad_body_full_size)
        adView.callToActionView = adView.findViewById<View>(R.id.ad_call_to_action_full_size)
        adView.iconView = adView.findViewById<View>(R.id.ad_app_icon_full_size)
        adView.priceView = adView.findViewById<View>(R.id.ad_price_full_size)
        adView.starRatingView = adView.findViewById<View>(R.id.ad_stars_full_size)
        adView.storeView = adView.findViewById<View>(R.id.ad_store_full_size)
        adView.advertiserView = adView.findViewById<View>(R.id.ad_advertiser_full_size)

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

}