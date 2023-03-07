package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.databinding.ItemCardListShareContactBinding
import com.chaplianski.bcard.core.model.Contact

class DoubleCardListAdapter: RecyclerView.Adapter<DoubleCardListAdapter.ViewHolder>() {

    val contactList = mutableListOf<Contact>()
    interface CheckItemListener{
        fun onClick(contact: Contact)
    }
    var checkItemListener: CheckItemListener? = null

    fun updateList(list: List<Contact>) {
        val diffCallback = CheckItemsDiffCallback(contactList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.contactList.clear()
        contactList.addAll(list.map { it.copy() })
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardListShareContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.onBind(contactList[position])
        holder.checkBox.setOnClickListener {
            checkItemListener?.onClick(contactList[position])
        }
        if (payloads.isEmpty()) {
            return
        } else {
            holder.update(payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ViewHolder (binding: ItemCardListShareContactBinding): RecyclerView.ViewHolder(binding.root) {

        val checkBox = binding.checkBoxItemCardListShared
        val contactName = binding.tvItemCardListSharedContact

        fun onBind(contact: Contact){
            contactName.text = "${contact.surname} ${contact.name}"
            checkBox.isChecked = contact.isChecked
        }
        fun update(payloads: MutableList<Any>){
            val isChecked = payloads[0] as Boolean
            checkBox.isChecked = isChecked
        }
    }

    class CheckItemsDiffCallback(
        val oldList: List<Contact>,
        val newList: List<Contact>
    ): DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.name == newItem.name && oldItem.surname == newItem.surname
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                return if (oldItem.isChecked == newItem.isChecked) {
                    super.getChangePayload(oldItemPosition, newItemPosition)
                } else newItem.isChecked
        }
    }
}