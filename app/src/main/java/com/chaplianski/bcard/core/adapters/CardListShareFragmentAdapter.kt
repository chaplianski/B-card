package com.chaplianski.bcard.core.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card
import java.util.Objects

class CardListShareFragmentAdapter(): RecyclerView.Adapter<CardListShareFragmentAdapter.ViewHolder>() {

    var cardList = mutableListOf<Card>()

    interface CheckBoxListener {
        fun onCheck(card: Card)
    }

    var checkBoxListener: CheckBoxListener? = null

    fun updateList(list: List<Card>) {
        val diffCallback = CardCheckDiffCallback(cardList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        cardList = list as MutableList<Card>
        diffResult.dispatchUpdatesTo(this)
        this.cardList.clear()
        this.cardList.addAll(list)

//        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_list_share_fragment, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(cardList[position])

        holder.checkBox.setOnClickListener {
            checkBoxListener?.onCheck(cardList[position])
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
//            holder.onBind(cardList[position], payloads)
            val bundle = payloads[0] as Bundle
            val checked = bundle.getBoolean("arg")
            holder.checkBox.isChecked = checked
        }

    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox_item_card_list_shared)
        val cardName: TextView = itemView.findViewById(R.id.tv_item_card_list_shared)

        fun onBind(card: Card) {
            checkBox.isChecked = card.isChecked
            cardName.text = "${card.surname} ${card.name}"
        }

        fun onBind(card: Card, payloads: MutableList<Any>){
            val isChecked = payloads.last() as Boolean
            checkBox.isChecked = isChecked
        }

    }

    class CardCheckDiffCallback(
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

            return oldList[oldItemPosition].isChecked == newList[newItemPosition].isChecked && oldList[oldItemPosition].surname == newList[newItemPosition].surname
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCard = oldList[oldItemPosition]
            val newCard = newList[newItemPosition]
            return oldCard == newCard
        }


        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
//            if (oldItem.id == newItem.id) {
//                return false
//            }
//            return getI

            if (oldItem.id == newItem.id) {
                return if (oldItem.isChecked == newItem.isChecked) {
                    super.getChangePayload(oldItemPosition, newItemPosition)
                } else {
                    val diff = Bundle()
                    diff.putBoolean("arg", newItem.isChecked)
                    diff
                }
            }
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }




}

