package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.databinding.ItemLanguageBinding

class LanguageSettingsAdapter(private val languageList: List<String>): RecyclerView.Adapter<LanguageSettingsAdapter.ViewHolder>() {

    var checkedPosition = -1

    interface LanguageCheckListener{
        fun onClickLanguage(language: String)
    }
    var languageCheckListener: LanguageCheckListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(languageList[position])

        holder.itemView.setOnClickListener {
            languageCheckListener?.onClickLanguage(languageList[position])
        }

//        if (checkedPosition == -1 && languageList[position].isChecked){
//            checkedPosition = position
//        }
        holder.checkBoxLanguage.isChecked = (checkedPosition == position)
        holder.checkBoxLanguage.setOnClickListener {
            languageCheckListener?.onClickLanguage(languageList[position])
            this.checkedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

    }

    class ViewHolder(binding: ItemLanguageBinding): RecyclerView.ViewHolder(binding.root) {
        val checkBoxLanguage = binding.checkBoxItemLanguage

        fun onBind(language: String){
            checkBoxLanguage.text = language
        }

    }

//    private val differCallback = object : DiffUtil.ItemCallback<Card>() {
//
//        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean = oldItem == newItem
//
//        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
//            return oldItem.hashCode() == newItem.hashCode()
//        }
//
//    }
//    val differ = AsyncListDiffer(this, differCallback)
}