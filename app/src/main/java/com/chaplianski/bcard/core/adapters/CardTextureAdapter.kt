package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.databinding.ItemCardTextureBinding
import com.chaplianski.bcard.core.model.CardTexture

class CardTextureAdapter: RecyclerView.Adapter<CardTextureAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<CardTexture>() {
        override fun areItemsTheSame(oldItem: CardTexture, newItem: CardTexture): Boolean {
            return oldItem.cardTextureName == newItem.cardTextureName
        }

        override fun areContentsTheSame(oldItem: CardTexture, newItem: CardTexture): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    var checkedPosition = -1

    interface CardTextureListener{
        fun onClickItem (cardTexture: CardTexture)
    }
    var cardTextureListener: CardTextureListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardTextureBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (checkedPosition == -1 && differ.currentList[position].isChecked){
               checkedPosition = position
        }
        holder.binding.radioButtonItemCardTexture.isChecked = (checkedPosition == position)
        holder.binding.radioButtonItemCardTexture.setOnClickListener {
            cardTextureListener?.onClickItem(differ.currentList[position])
            this.checkedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    class ViewHolder (val binding: ItemCardTextureBinding): RecyclerView.ViewHolder(binding.root){}
}