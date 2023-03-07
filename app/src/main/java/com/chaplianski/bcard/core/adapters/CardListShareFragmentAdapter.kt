package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.UiText
import com.chaplianski.bcard.databinding.ItemCardListShareContactBinding
import com.chaplianski.bcard.databinding.ItemCardListShareLetterBinding
import com.chaplianski.bcard.domain.model.ContactContent


class CardListShareFragmentAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var cardList = mutableListOf<ContactContent>()
    interface CheckBoxListener {
        fun onCheck(card: ContactContent.Contact)
    }
    var checkBoxListener: CheckBoxListener? = null

    fun updateList(list: List<ContactContent>) {

        val diffCallback = ContactCheckDiffCallback(cardList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.cardList.clear()
        val newList = mutableListOf<ContactContent>()
        list.forEach {
            if (it is ContactContent.Contact) {
                val newContact = ContactContent.Contact(it.card.copy())
                newList.add(newContact)
            } else newList.add(it)
        }
        cardList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            TYPE_LETTER ->  {
                val binding = ItemCardListShareLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LetterViewHolder(binding)
            }
            TYPE_CONTACT -> {
                val binding = ItemCardListShareContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ContactViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder,position, mutableListOf())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when(cardList[position]){
            is ContactContent.Letter -> {
                (holder as LetterViewHolder).onBind(cardList[position] as ContactContent.Letter)
            }
            is ContactContent.Contact -> {
                (holder as ContactViewHolder).onBind(cardList[position] as ContactContent.Contact)
                holder.checkBox.setOnClickListener {
                    checkBoxListener?.onCheck(cardList[position] as ContactContent.Contact)
                }
                if (payloads.isEmpty()) {
                    return
                } else {
                    holder.update(payloads)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(cardList[position]){
            is ContactContent.Letter -> TYPE_LETTER
            else -> TYPE_CONTACT
        }
    }

    inner class LetterViewHolder(binding: ItemCardListShareLetterBinding): RecyclerView.ViewHolder(binding.root){

        val currentLetter = binding.tvItemCardListShareLetter
        fun onBind(letter: ContactContent.Letter){
            currentLetter.text = letter.letter.toString()
        }
    }

    inner class ContactViewHolder (binding: ItemCardListShareContactBinding): RecyclerView.ViewHolder(binding.root){

        val checkBox = binding.checkBoxItemCardListShared
        val contactName = binding.tvItemCardListSharedContact
        fun  onBind(card: ContactContent.Contact){
//            Log.d("MyLog", "onCheck = ${card.card.isChecked}")
            checkBox.isChecked = card.card.isChecked
            contactName.text = UiText.StringResource(
                R.string.two_value_without_comma, card.card.surname, card.card.name).asString(contactName.context)
            checkBox.setOnClickListener {
            checkBoxListener?.onCheck(card)
            }
        }
        fun update(payloads: MutableList<Any>){
            val isChecked = payloads[0] as Boolean
            checkBox.isChecked = isChecked
        }
    }

    class ContactCheckDiffCallback(
        val oldList: List<ContactContent>,
        val newList: List<ContactContent>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
//            Log.d("MyLog", "oldList = $oldList")
            return oldList.size
        }

        override fun getNewListSize(): Int {
//            Log.d("MyLog", "newList = $newList")
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition] is ContactContent.Letter && newList[newItemPosition] is ContactContent.Letter -> {
                    (oldList[oldItemPosition] as ContactContent.Letter).letter == (newList[newItemPosition] as ContactContent.Letter).letter
                }
                oldList[oldItemPosition] is ContactContent.Contact && newList[newItemPosition] is ContactContent.Contact -> {
                    val oldItem = oldList[oldItemPosition] as ContactContent.Contact
                    val newItem = newList[newItemPosition] as ContactContent.Contact
                    oldItem.card.id == newItem.card.id
                }
                oldList[oldItemPosition] is ContactContent.Letter && newList[newItemPosition] is ContactContent.Contact -> {
                        true
                }
                oldList[oldItemPosition] is ContactContent.Contact && newList[newItemPosition] is ContactContent.Letter -> {
                    true
                }
                else -> {
                    true
                }
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition] is ContactContent.Letter && newList[newItemPosition] is ContactContent.Letter -> {
                    (oldList[oldItemPosition] as ContactContent.Letter) == (newList[newItemPosition] as ContactContent.Letter)
                }
                oldList[oldItemPosition] is ContactContent.Contact && newList[newItemPosition] is ContactContent.Contact -> {
                   val oldItem = oldList[oldItemPosition] as ContactContent.Contact
                   val newItem = newList[newItemPosition] as ContactContent.Contact
                    oldItem.card == newItem.card
                }
                else -> {
                    false
                }
            }
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

            if (oldList[oldItemPosition] is ContactContent.Contact && newList[newItemPosition] is ContactContent.Contact){
                val oldItem = (oldList[oldItemPosition] as ContactContent.Contact).card
                val newItem = (newList[newItemPosition] as ContactContent.Contact).card
                return if (oldItem.isChecked == newItem.isChecked) {
                    super.getChangePayload(oldItemPosition, newItemPosition)
                } else newItem.isChecked
            }
            return false
        }
    }

    companion object {
        private val TYPE_LETTER = 0
        private val TYPE_CONTACT = 1
    }
}



