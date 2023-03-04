package com.chaplianski.bcard.core.helpers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.helpers.adapter.FeedItemViewBinder

//class VerticalImagesViewBinder(val block : (data: VeriticalImageModel) -> Unit) : FeedItemViewBinder<VeriticalImageModel, VerticalImagesViewHolder>(
//    VeriticalImageModel::class.java) {
//
//    override fun createViewHolder(parent: ViewGroup): VerticalImagesViewHolder {
//        return VerticalImagesViewHolder(
//            LayoutInflater.from(parent.context).inflate(getFeedItemType(), parent, false),block)
//    }
//
//    override fun bindViewHolder(model: VeriticalImageModel, viewHolder: VerticalImagesViewHolder) {
//        viewHolder.bind(model)
//    }
//
//    override fun getFeedItemType() = R.layout.adapter_vertical_image
//
//    override fun areContentsTheSame(oldItem: VeriticalImageModel, newItem: VeriticalImageModel) = oldItem == newItem
//
//    override fun areItemsTheSame(oldItem: VeriticalImageModel, newItem: VeriticalImageModel) : Boolean {
//        return oldItem.Image == newItem.Image
//    }
//}
//
//
//class VerticalImagesViewHolder(val view : View, val block : (data: VeriticalImageModel) -> Unit)
//    : RecyclerView.ViewHolder(view) {
//
//    fun bind(data: VeriticalImageModel) {
//
//        itemView.setOnClickListener {
//            block(data)
//        }
//
//        itemView.apply {
//            Glide
//                .with(itemView.context)
//                .load(data.Image)
//                .centerCrop()
//                .into(im_vertical)
//        }
//    }
//}