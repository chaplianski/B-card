package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.core.adapters.DoubleCardListAdapter
import com.chaplianski.bcard.databinding.DialogCheckDoubleCardListBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.Contact


class CheckDoubleCardListDialog : DialogFragment() {
    private var _binding: DialogCheckDoubleCardListBinding? = null
    val binding get() = _binding!!

//    @Inject
//    lateinit var vmFactory: ViewModelProvider.Factory
//    private val checkDoubleCardListDialogViewModel: CheckDoubleCardListDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .checkDoubleCardListDialogInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.attributes
        params?.y = 30
        window?.attributes = params
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogCheckDoubleCardListBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_person_information, container, false)
    }

    //    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.btCheckDoubleCardListDialogAdd
        val cancelButton = binding.btCheckDoubleCardListDialogCancel
        val cardListRV = binding.rvCheckDoubleCardListDialog
        var checkedContactCount = 0
        var allCardCheckFlag = false
        var allCardList = emptyList<Card>()
        var cardList = emptyList<Card>()
        val checkboxAllContacts = binding.checkBoxCheckDoubleCardListDialogCheckAll
//        val currentCardList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arguments?.getParcelableArrayList(CARD_LIST, Card::class.java)
//        } else {
//            arguments?.getParcelableArrayList<Card>(CARD_LIST)
//        }
        val currentContactList = arguments?.getParcelableArrayList<Contact>(DOUBLE_CARD_LIST)

        val contactListAdapter = DoubleCardListAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = contactListAdapter


//    val cardList1 = mutableListOf<Card>()
//    currentCardList?.forEach {
//        cardList1.add(it)
//    }

        contactListAdapter.checkItemListener =
            object : DoubleCardListAdapter.CheckItemListener {

                override fun onClick(contact: Contact) {
                    currentContactList?.forEach { currentContact ->
                        if (contact.name == currentContact.name && contact.surname == currentContact.surname && contact.workPhone == currentContact.workPhone) {
                            currentContact.isChecked = !currentContact.isChecked
                            if (currentContact.isChecked) checkedContactCount++ else checkedContactCount--
                        }
                        addButton.text = "Save [$checkedContactCount]"
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
            Log.d("MyLog", "check all = ${checkedContactCount}")
            addButton.text = "Save [$checkedContactCount]"

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
//
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS)
            )
            dismiss()
        }
    }


    override fun onStart() {
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val ADD_STATUS = "add cards status"
        val CANCEL_STATUS = "cancel load cards status"
        val DOUBLE_CARD_LIST = "double card list"


        val ADD_INFO = "add info"
        val CARD_SETTINGS = "card settings"
        val PROFIL_INFO = "profile info"
        val CARD_LIST = "card list"

        const val PICK_FILE = 222

        val TAG = CheckDoubleCardListDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager, contactList: List<Contact>) {
            val dialogFragment = CheckDoubleCardListDialog()
            val contactArrayList = contactList as ArrayList<Contact>
            Log.d("MyLog", "arraylist show = ${contactArrayList}")
            dialogFragment.arguments = bundleOf(
//                CURRENT_CARD_ID to currentCardId
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
//                    val additionalInfo = result.getParcelable<AdditionalInfo>(ADDITIONAL_INFO)
                    if (status != null && contactList != null) {
                            listener.invoke(status, contactList)
                    }
                })
        }
    }
}