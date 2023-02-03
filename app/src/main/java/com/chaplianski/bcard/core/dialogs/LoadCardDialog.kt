package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.helpers.AccountContactsPicker
import com.chaplianski.bcard.core.helpers.ContactPicker
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.utils.DESTINATION
import com.chaplianski.bcard.core.utils.LOAD_FROM_FILE
import com.chaplianski.bcard.core.utils.LOAD_FROM_GOOGLE_ACCOUNT
import com.chaplianski.bcard.core.viewmodels.LoadCardsDialogViewModel
import com.chaplianski.bcard.databinding.DialogLoadCardBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.Contact
import com.chaplianski.bcard.domain.model.ContactContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoadCardDialog : DialogFragment() {

    private var _binding: DialogLoadCardBinding? = null
    val binding get() = _binding!!

    var cardList = emptyList<Card>()
    val loadingProgressBar = view?.findViewById<LottieAnimationView>(R.id.pb_load_card_dialog_loading)

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val loadCardsDialogViewModel: LoadCardsDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .loadCardsDialogInject(this)
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
        _binding = DialogLoadCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.btLoadCardDialogAdd
        val cancelButton = binding.btLoadCardDialogCancel
        val cardListRV = binding.rvLoadCardDialog


        var allCardList = emptyList<Card>()

        val checkboxAllCards = binding.checkBoxLoadCardDialogCheckAll
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID, -1L)
        val currentDestination = arguments?.getString(DESTINATION)

        val cardListAdapter = CardListShareFragmentAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = cardListAdapter


        loadCardsDialogViewModel.getCards()
        loadCardsDialogViewModel.allCards.observe(this.viewLifecycleOwner) {
            allCardList = it
        }

//        loadingProgressBar.playAnimation()
//        loadingProgressBar.isVisible = true

        lifecycleScope.launch(Dispatchers.IO) {

            when (currentDestination) {
                LOAD_FROM_FILE -> {
                    val filePicker =
                        ContactPicker(
                            requireContext(),
                            requireActivity().activityResultRegistry
                        ) { listCard ->
                            fillCardAdapter(listCard, addButton, cardListAdapter, checkboxAllCards)
                        }
                    filePicker.loadFiles()
                }
                LOAD_FROM_GOOGLE_ACCOUNT -> {

                    val accountContactPicker = AccountContactsPicker(
                        requireContext(),
                        requireActivity().activityResultRegistry
                    ) { listCard ->
                        fillCardAdapter(listCard, addButton, cardListAdapter, checkboxAllCards)
                    }
                    accountContactPicker.checkPermission()
                }
            }
        }


        addButton.setOnClickListener {
            val listCard = mutableListOf<Card>()

            cardList.forEach { cardItem ->
                if (cardItem.isChecked) {
                    listCard.add(cardItem)
                }
            }
            val doubleCardList = mutableListOf<Card>()
            when {
                allCardList.isEmpty() && listCard.isNotEmpty() -> {
                    listCard.forEach {
                        Log.d("MyLog", "add card when empty list card")
                        loadCardsDialogViewModel.addCard(it)
                    }
                }
                allCardList.isNotEmpty() && listCard.isNotEmpty() -> {

                    listCard.forEach { checkedCard ->
                        val doubleCheckedCard =
                            allCardList.filter { checkedCard.name == it.name && checkedCard.surname == it.surname }
                        if (doubleCheckedCard.isNotEmpty()){
                            doubleCardList.add(checkedCard)
                        } else loadCardsDialogViewModel.addCard(checkedCard)
                    }
                }
            }

            if (doubleCardList.isNotEmpty()) {
                val contactList = mutableListOf<Contact>()
                doubleCardList.forEach {
                    val contact = Contact(
                        name = it.name,
                        surname = it.surname,
                        workPhone = it.workPhone,
                        isChecked = false
                    )
                    contactList.add(contact)
                }
//                Log.d("MyLog", "contactList = ${contactList.size}, doubleListSize = ${doubleCardList.size}")
                showCheckDoubleCardListDialog(contactList)
            }
            if (doubleCardList.isNotEmpty()){
                setupCheckDoubleCardListDialog()
            } else {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(CHECKED_OPTION to ADD_STATUS, CURRENT_CARD_ID to currentCardId)
                )
                dismiss()
            }
        }
//
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS, CURRENT_CARD_ID to currentCardId)
            )
            dismiss()
        }
    }

    private fun fillCardAdapter(
        listCard: List<Card>,
        addButton: AppCompatButton,
        cardListAdapter: CardListShareFragmentAdapter,
        checkboxAllCards: CheckBox,
//        loadingProgressBar: LottieAnimationView
    ) {
//        Log.d("MyLog", "fill card list = $listCard")
        loadingProgressBar?.cancelAnimation()
        loadingProgressBar?.isVisible = false

        var checkedCardCount = 0
        cardList = listCard
        val newContactList = mutableListOf<ContactContent>()
        cardList.forEach {

            if (it.surname.isEmpty())  {
                it.surname = it.name
                it.name = ""
            }
//            Log.d("MyLog", "name = ${it.name}, surname = ${it.surname}, letter = ${it.surname.first().uppercaseChar()}")
        }

        var newLetterList = cardList
            .sortedBy { it.surname }
            .map { it.surname.first().uppercaseChar()}
            .toSet()

        Log.d("MyLog", "letters = $newLetterList")
        newLetterList.forEach { letter ->

            cardList
                .sortedBy { it.surname }
                .forEachIndexed { index, card ->

                if (letter == card.surname.first().uppercaseChar()) {
                    if (!newContactList.contains(ContactContent.Letter(letter))) {
                        newContactList.add(ContactContent.Letter(letter))
                    }
                    newContactList.add(ContactContent.Contact(card))
                }
            }
            addButton.text = "Add [$checkedCardCount]"
        }

        cardListAdapter.updateList(newContactList)

        cardListAdapter.checkBoxListener =
            object : CardListShareFragmentAdapter.CheckBoxListener {
                override fun onCheck(card: ContactContent.Contact) {
                    cardList.forEach { cardItem ->
                        if (cardItem.name == card.card.name && cardItem.surname == card.card.surname) {
                            cardItem.isChecked = !cardItem.isChecked
                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
                        }
                        addButton.text = "Add [$checkedCardCount]"
                    }
                }
            }

        var allCardCheckFlag = false
        checkboxAllCards.setOnClickListener {
            if (!allCardCheckFlag) {
                cardList.forEach {
                    it.isChecked = true
                }
                checkedCardCount = cardList.size
            } else {
                cardList.forEach {
                    it.isChecked = false
                }
                checkedCardCount = 0
            }

            newContactList.clear()
            newLetterList =
                cardList.sortedBy { it.surname }.map { it.surname.first().uppercaseChar() }
                    .toSet()
            newLetterList.forEach { letter ->
                cardList.sortedBy { it.surname }.forEachIndexed { index, card ->
                    if (letter == card.surname.first().uppercaseChar()) {
                        if (!newContactList.contains(ContactContent.Letter(letter))) {
                            newContactList.add(ContactContent.Letter(letter))
                        }
                        newContactList.add(ContactContent.Contact(card))
                    }
                }
                addButton.text = "Save [$checkedCardCount]"
            }
            cardListAdapter.updateList(newContactList)
            allCardCheckFlag = !allCardCheckFlag
        }
    }

    private fun setupCheckDoubleCardListDialog() {
        CheckDoubleCardListDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner){status, contactList ->
            when(status){
                CheckDoubleCardListDialog.ADD_STATUS -> {
                    val addDoubleCardList = mutableListOf<Card>()
                    contactList.forEach {contact ->
                        val card = cardList.filter { it.name == contact.name && it.surname == contact.surname && it.workPhone == contact.workPhone }
                        addDoubleCardList.addAll(card)
                    }
                    Log.d("MyLog", "contactList = $contactList, addDoubleList = $addDoubleCardList")
                    addDoubleCardList.forEach {
                        loadCardsDialogViewModel.addCard(it)
                    }
                    parentFragmentManager.setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(CHECKED_OPTION to AFTER_CHECK_DOUBLE_STATUS, CURRENT_CARD_ID to FAKE_CURRENT_CARD_ID)
                    )
                    dismiss()
                }
                CheckDoubleCardListDialog.CANCEL_STATUS -> {
                    parentFragmentManager.setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(CHECKED_OPTION to AFTER_CHECK_DOUBLE_STATUS, CURRENT_CARD_ID to FAKE_CURRENT_CARD_ID)
                    )
                    dismiss()
                }
            }
        }
    }

    private fun showCheckDoubleCardListDialog(contactList: List<Contact>) {
        CheckDoubleCardListDialog.show(parentFragmentManager, contactList)
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
        val AFTER_CHECK_DOUBLE_STATUS = "after check double status"
        val FAKE_CURRENT_CARD_ID = -1L
        val REGISTRY_KEY_LOAD_FILE = "registry key load photo"


        val CARD_SETTINGS = "card settings"
        val PROFIL_INFO = "profile info"

        val TAG = LoadCardDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager, currentCardId: Long, destination: String ) {
            val dialogFragment = LoadCardDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId,
                DESTINATION to destination
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String, Long) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
                    if (status != null) {
                        listener.invoke(status, cardId)
                    }
                })
        }
    }
}