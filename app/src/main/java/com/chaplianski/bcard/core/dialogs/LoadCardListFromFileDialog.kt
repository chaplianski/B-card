package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.helpers.ProcessCard
import com.chaplianski.bcard.core.helpers.ProcessCsvCard
import com.chaplianski.bcard.core.helpers.ProcessVcfCard
import com.chaplianski.bcard.core.utils.CSV_TYPE
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.utils.DESTINATION
import com.chaplianski.bcard.core.utils.VCF_TYPE
import com.chaplianski.bcard.core.viewmodels.LoadCardsDialogViewModel
import com.chaplianski.bcard.databinding.DialogLoadCardBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.Contact
import ezvcard.Ezvcard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoadCardListFromFileDialog :
    BasisDialogFragment<DialogLoadCardBinding>(DialogLoadCardBinding::inflate){

    var cardList = emptyList<Card>()
//    val loadingProgressBar = view?.findViewById<LottieAnimationView>(R.id.pb_load_card_dialog_loading)

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val loadCardsDialogViewModel: LoadCardsDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .loadCardsDialogInject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.btLoadCardDialogAdd
        val cancelButton = binding.btLoadCardDialogCancel
        val cardListRV = binding.rvLoadCardDialog

        var allCardList = emptyList<Card>()

        val checkboxAllCards = binding.checkBoxLoadCardDialogCheckAll
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID, -1L)
        val currentUri = arguments?.getString(CURRENT_URI)
        val processCard = ProcessCard(requireContext())

        val cardListAdapter = CardListShareFragmentAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = cardListAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            loadCardsDialogViewModel.getCards(SURNAME)
        }

        loadCardsDialogViewModel.getCardListState
            .flowWithLifecycle(lifecycle)
            .onEach {
                when(it){
                    is LoadCardsDialogViewModel.GetCardsState.Loading -> {}
                    is LoadCardsDialogViewModel.GetCardsState.Success -> {
                        allCardList = it.cardList
                    }
                    is LoadCardsDialogViewModel.GetCardsState.Failure -> {}
                }
            }
            .launchIn(lifecycleScope)

        currentUri?.toUri().also {
            val mimeType = it?.let { it1 -> context?.contentResolver?.getType(it1) }
            val inputStream = it?.let { it1 -> context?.contentResolver?.openInputStream(it1) }
            val readVcard = Ezvcard.parse(inputStream).all()//.first()
            val contentResolver = context?.contentResolver
            val processVcfCard = ProcessVcfCard()
            val processCsvCard = ProcessCsvCard()
            when(mimeType){
                VCF_TYPE -> {
                    cardList = if (contentResolver != null) {
                    processVcfCard.convertVcardToCardList(readVcard, contentResolver, requireContext())
                } else emptyList<Card>()
                }
                CSV_TYPE -> {
                    cardList = if (contentResolver != null) {
                        processCsvCard.getCardList(requireContext(), it)
                    } else emptyList<Card>()}

                }

            if (cardList.isNotEmpty()) {
                processCard.fillCardAdapter(cardList,
                    addButton,
                    cardListAdapter,
                    checkboxAllCards)
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
    private fun setupCheckDoubleCardListDialog() {
        CheckDoubleCardListDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner){status, contactList ->
            when(status){
                CheckDoubleCardListDialog.ADD_STATUS -> {
                    val addDoubleCardList = mutableListOf<Card>()
                    contactList.forEach {contact ->
                        val card = cardList.filter { it.name == contact.name && it.surname == contact.surname && it.workPhone == contact.workPhone }
                        addDoubleCardList.addAll(card)
                    }
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

    companion object {

        val CHECKED_OPTION = "checked load option"
        val ADD_STATUS = "add load cards status"
        val CANCEL_STATUS = "cancel load cards status"
        val AFTER_CHECK_DOUBLE_STATUS = "after check double status"
        val FAKE_CURRENT_CARD_ID = -1L
        val REGISTRY_KEY_LOAD_FILE = "registry key load photo"
        val SURNAME = "surname"
        val CURRENT_URI = "current uri"

        val TAG = LoadCardListFromFileDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager, uri: Uri){ //currentCardId: Long, destination: String ) {
            val dialogFragment = LoadCardListFromFileDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_URI to uri.toString()
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