package com.chaplianski.bcard.core.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card

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
        this.cardList.addAll(list.map { it.copy() })

//        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_list_share_fragment, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder,position, mutableListOf())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        holder.onBind(cardList[position])

        holder.checkBox.setOnClickListener {
            checkBoxListener?.onCheck(cardList[position])
        }

        if (payloads.isEmpty()) {
            return
        } else {
            holder.update(payloads)
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

        fun update(payloads: MutableList<Any>){
            Log.d("MyLog", "payloads = $payloads")
            val isChecked = payloads[0] as Boolean
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
//            Log.d("MyLog", "areItemsTheSame = ${oldList[oldItemPosition].isChecked == newList[newItemPosition].isChecked}")
//            Log.d("MyLog", "areItemsTheSame = ${oldList[oldItemPosition].id == newList[newItemPosition].id}")
            return oldList[oldItemPosition].id == newList[newItemPosition].id
//                    oldList[oldItemPosition].surname == newList[newItemPosition].surname
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCard = oldList[oldItemPosition]
            val newCard = newList[newItemPosition]
//            Log.d("MyLog", "areContentsTheSame = ${oldCard == newCard}")
            return oldCard == newCard
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
//            Log.d("MyLog", "newItem.isChecked = ${newItem.isChecked}")
           return if (oldItem.isChecked == newItem.isChecked) {
                super.getChangePayload(oldItemPosition, newItemPosition)
            } else newItem.isChecked

        }
    }




}

