package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.databinding.ItemLanguageBinding
import com.chaplianski.bcard.domain.model.LanguageItem

class LanguageSettingsAdapter(private val languageList: List<LanguageItem>): RecyclerView.Adapter<LanguageSettingsAdapter.ViewHolder>() {

    var checkedPosition = -1

    interface LanguageCheckListener{
        fun onClickLanguage(language: LanguageItem)
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
        holder.checkBoxLanguage.isChecked = (checkedPosition == position)
        holder.checkBoxLanguage.setOnClickListener {
            languageCheckListener?.onClickLanguage(languageList[position])
            this.checkedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    class ViewHolder(binding: ItemLanguageBinding): RecyclerView.ViewHolder(binding.root) {
        val checkBoxLanguage = binding.checkBoxItemLanguage

        fun onBind(language: LanguageItem){
            checkBoxLanguage.text = language.longName
        }
    }
}