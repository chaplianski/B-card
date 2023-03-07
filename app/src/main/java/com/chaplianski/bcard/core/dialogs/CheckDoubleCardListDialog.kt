package com.chaplianski.bcard.core.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.DoubleCardListAdapter
import com.chaplianski.bcard.databinding.DialogCheckDoubleCardListBinding
import com.chaplianski.bcard.core.model.Contact

class CheckDoubleCardListDialog :
    BasisDialogFragment<DialogCheckDoubleCardListBinding>(DialogCheckDoubleCardListBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.btCheckDoubleCardListDialogAdd
        val cancelButton = binding.btCheckDoubleCardListDialogCancel
        val cardListRV = binding.rvCheckDoubleCardListDialog
        var checkedContactCount = 0
        var allCardCheckFlag = false
        val checkboxAllContacts = binding.checkBoxCheckDoubleCardListDialogCheckAll
        val currentContactList = arguments?.getParcelableArrayList<Contact>(DOUBLE_CARD_LIST)

        val contactListAdapter = DoubleCardListAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = contactListAdapter

        contactListAdapter.checkItemListener =
            object : DoubleCardListAdapter.CheckItemListener {

                override fun onClick(contact: Contact) {
                    currentContactList?.forEach { currentContact ->
                        if (contact.name == currentContact.name && contact.surname == currentContact.surname && contact.workPhone == currentContact.workPhone) {
                            currentContact.isChecked = !currentContact.isChecked
                            if (currentContact.isChecked) checkedContactCount++ else checkedContactCount--
                        }
                        addButton.text = getString(R.string.add_items, checkedContactCount)
                    }
                }
            }

        checkboxAllContacts.setOnClickListener {
            if (!allCardCheckFlag){
                currentContactList?.forEach {
                    it.isChecked = true
                }
                checkedContactCount = currentContactList?.size ?: 0
            } else {
                currentContactList?.forEach {
                    it.isChecked = false
                }
                checkedContactCount = 0
            }
            addButton.text = getString(R.string.add_items, checkedContactCount)

            currentContactList?.toList()?.let { it1 -> contactListAdapter.updateList(it1) }
            allCardCheckFlag = !allCardCheckFlag
        }

        currentContactList?.toList()?.let { contactListAdapter.updateList(it) }

        addButton.setOnClickListener {
            val saveList = currentContactList?.filter { it.isChecked == true } as ArrayList<Contact>
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to ADD_STATUS, DOUBLE_CARD_LIST to saveList)
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS)
            )
            dismiss()
        }
    }

    companion object {

        val CHECKED_OPTION = "checked double option"
        val ADD_STATUS = "add double cards status"
        val CANCEL_STATUS = "cancel double cards status"
        val DOUBLE_CARD_LIST = "double card list"
        val TAG = CheckDoubleCardListDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager, contactList: List<Contact>) {
            val dialogFragment = CheckDoubleCardListDialog()
            val contactArrayList = contactList as ArrayList<Contact>
            dialogFragment.arguments = bundleOf(
                DOUBLE_CARD_LIST to contactArrayList
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String, ArrayList<Contact>) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val status = result.getString(CHECKED_OPTION)
                    val contactList = result.getParcelableArrayList<Contact>(DOUBLE_CARD_LIST)
                    if (status != null && contactList != null) {
                            listener.invoke(status, contactList)
                    }
                })
        }
    }
}