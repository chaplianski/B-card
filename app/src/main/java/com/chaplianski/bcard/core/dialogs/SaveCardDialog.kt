package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.helpers.ProcessCsvCard
import com.chaplianski.bcard.core.helpers.mapCardToContactCsv
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.core.viewmodels.SaveCardDialogViewModel
import com.chaplianski.bcard.databinding.DialogSaveCardBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.ContactContent
import com.chaplianski.bcard.domain.model.ContactCsv
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.AddressType
import ezvcard.parameter.EmailType
import ezvcard.parameter.ImageType
import ezvcard.parameter.TelephoneType
import ezvcard.property.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

class SaveCardDialog :
    BasisDialogFragment<DialogSaveCardBinding>(DialogSaveCardBinding::inflate) {

    val vcardList = mutableListOf<VCard>()
    val cardListForCsv = mutableListOf<ContactCsv>()
    val saveCards = registerForActivityResult(
        ActivityResultContracts.CreateDocument(
            SAVING_TYPE_FILE_VCF
        )
    ) {
        saveFile(it)
    }

    val saveCSVCards = registerForActivityResult(
        ActivityResultContracts.CreateDocument(
            SAVING_TYPE_FILE_CSV
        )
    ) {
        saveCSVFile(it)
    }

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val saveCardDialogViewModel: SaveCardDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .saveCardDialogInject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveVCFButton = binding.btSaveCardDialogVcf
        val saveCSVButton = binding.btSaveCardDialogCsv
        val cancelButton = binding.btSaveCardDialogCancel
        val cardListRV = binding.rvSaveCardDialog
        var checkedCardCount = 0
        val checkboxAllCards = binding.checkBoxSaveCardDialogCheckAll
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID, -1L)
        val cardListAdapter = CardListShareFragmentAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = cardListAdapter

        saveCardDialogViewModel.getCardListState
            .flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    is SaveCardDialogViewModel.GetCardsState.Loading -> {}
                    is SaveCardDialogViewModel.GetCardsState.Success -> {
                        val cardList = it.cardList
                        val fullContactList = mutableListOf<ContactContent>()

                        val letterList =
                            cardList.sortedBy { it.surname }
                                .map { it.surname.first().uppercaseChar() }.toSet()
                        letterList.forEach { letter ->
                            cardList.sortedBy { it.surname }.forEachIndexed { index, card ->
                                if (letter == card.surname.first().uppercaseChar()) {
                                    if (!fullContactList.contains(ContactContent.Letter(letter))) {
                                        fullContactList.add(ContactContent.Letter(letter))
                                    }
                                    if (card.id == currentCardId) {
                                        card.isChecked = true
                                    }
                                    fullContactList.add(ContactContent.Contact(card))
                                }
                            }
                            checkedCardCount = 1
                            saveVCFButton.text = getString(
                                R.string.save_items,
                                checkedCardCount
                            )
                        }
                        cardListAdapter.updateList(fullContactList)

                        cardListAdapter.checkBoxListener =
                            object : CardListShareFragmentAdapter.CheckBoxListener {
                                override fun onCheck(card: ContactContent.Contact) {
                                    cardList.forEach { cardItem ->
                                        if (cardItem.id == card.card.id) {
                                            cardItem.isChecked = !cardItem.isChecked
                                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
                                        }
                                        saveVCFButton.text = getString(
                                            R.string.save_items,
                                            checkedCardCount
                                        )
                                    }
                                }
                            }

                        val listCard = mutableListOf<Card>()

                        saveVCFButton.setOnClickListener {
                            vcardList.clear()
                            listCard.clear()
                            cardList.forEach { cardItem ->
                                if (cardItem.isChecked) {
                                    listCard.add(cardItem)
                                    vcardList.add(createVCard(cardItem))
                                }
                            }

                            if (!listCard.isNullOrEmpty()) {
                                when (listCard.size) {
                                    1 -> {
                                        val card = listCard[0]
                                        val file = File(getString(R.string.two_values_together, card.surname, TYPE_FILE_VCF))
                                        saveVCardToPhone(file, card.surname, TYPE_FILE_VCF)
                                    }
                                    else -> {
                                        val file = File(getString(R.string.two_values_together, COMMON_NAME_FILE, TYPE_FILE_VCF))
                                        saveVCardToPhone(file, COMMON_NAME_FILE, TYPE_FILE_VCF)
                                    }
                                }
                            }
                        }

                        saveCSVButton.setOnClickListener {
                            cardListForCsv.clear()
                            cardList.forEach { cardItem ->
                                if (cardItem.isChecked) {
                                    cardListForCsv.add(cardItem.mapCardToContactCsv())
                                }
                            }

                            if (!cardListForCsv.isNullOrEmpty()) {
                                when (cardListForCsv.size) {
                                    1 -> {
                                        val card = cardListForCsv[0]
                                        val file = File(getString(R.string.two_values_together, card.familyName, TYPE_FILE_CSV))
                                        card.familyName?.let { it1 ->
                                            saveVCardToPhone(file,
                                                it1, TYPE_FILE_CSV)
                                        }
                                    }
                                    else -> {
                                       val file = File(getString(R.string.two_values_together, COMMON_NAME_FILE, TYPE_FILE_VCF))
                                        saveVCardToPhone(file, COMMON_NAME_FILE, TYPE_FILE_CSV)
                                    }

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

                            val newContactList = mutableListOf<ContactContent>()
                            val newLetterList =
                                cardList.sortedBy { it.surname }
                                    .map { it.surname.first().uppercaseChar() }.toSet()
                            newLetterList.forEach { letter ->
                                cardList.sortedBy { it.surname }.forEachIndexed { index, card ->
                                    if (letter == card.surname.first().uppercaseChar()) {
                                        if (!newContactList.contains(
                                                ContactContent.Letter(
                                                    letter
                                                )
                                            )
                                        ) {
                                            newContactList.add(ContactContent.Letter(letter))
                                        }
                                        newContactList.add(ContactContent.Contact(card))
                                    }
                                }
                                saveVCFButton.text = getString(
                                    R.string.save_items,
                                    checkedCardCount
                                )
                            }
                            cardListAdapter.updateList(fullContactList)
                            allCardCheckFlag = !allCardCheckFlag
                        }
                    }
                    is SaveCardDialogViewModel.GetCardsState.Failure -> {}
                }
            }
            .launchIn(lifecycleScope)

        lifecycleScope.launch(Dispatchers.IO) {
            saveCardDialogViewModel.getCards(SURNAME)
        }

        saveVCFButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to SAVE_STATUS)
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to CANCEL_STATUS)
            )
            dismiss()
        }
    }

    @Throws(IOException::class)
    private fun createVCard(card: Card): VCard {
        val vcard = VCard()
        vcard.kind = Kind.individual()
        val structureName = StructuredName()
        structureName.given = card.name
        structureName.family = card.surname
        vcard.structuredName = structureName
        vcard.setFormattedName("${card.name} ${card.surname}")
        vcard.addTitle(card.speciality)
        vcard.setOrganization(card.organization)
        val adr = Address()
        adr.locality = card.town
        adr.country = card.country
        adr.types.add(AddressType.WORK)
        vcard.addAddress(adr)
        vcard.addTelephoneNumber(card.workPhone, TelephoneType.WORK)
        vcard.addEmail(card.email, EmailType.WORK)
        vcard.addTelephoneNumber(card.homePhone, TelephoneType.HOME)
        val file = File(card.photo)
        val photo = Photo(file, ImageType.JPEG)
        vcard.addPhoto(photo)
        vcard.uid = Uid.random()
        vcard.revision = Revision.now()

        val profile = vcard.addExtendedProperty(PROFIL_INFO, card.additionalContactInfo)
        val skills = vcard.addExtendedProperty(PROFESSIONAL_SKILLS, card.professionalInfo)
        val privateInfo = vcard.addExtendedProperty(WORK_EXPERIENCE, card.privateInfo)
        val reference = vcard.addExtendedProperty(REFERENCE, card.reference)
        val cardColor = vcard.addExtendedProperty(CARD_TEXTURE, card.cardTexture.toString())
        val strokeColor = vcard.addExtendedProperty(CARD_TEXT_COLOR, card.cardTextColor)
        val cardCorner = vcard.addExtendedProperty(CARD_CORNER, card.isCardCorner.toString())
        val formPhoto = vcard.addExtendedProperty(FORM_PHOTO, card.cardFormPhoto)

        profile.group = ADD_INFO
        skills.group = ADD_INFO
        privateInfo.group = ADD_INFO
        reference.group = ADD_INFO
        cardColor.group = CARD_SETTINGS
        strokeColor.group = CARD_SETTINGS
        cardCorner.group = CARD_SETTINGS
        formPhoto.group = CARD_SETTINGS
        return vcard
    }

    private fun saveVCardToPhone(file: File, fileName: String, fileType: String) {
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        path.mkdirs()
        try {
            if (fileType == TYPE_FILE_VCF) saveCards.launch(getString(R.string.two_values_together, fileName, fileType))
            else saveCSVCards.launch("${fileName}$fileType")
        } catch (e: Exception) {
            Log.d("MyLog", "e = $e")
        }
    }

    private fun saveFile(uri: Uri?) {
        val outputStream = uri?.let {
            context?.contentResolver?.openOutputStream(uri)
        }
        Ezvcard.write(vcardList).go(outputStream)
        outputStream?.flush()
        outputStream?.close()
        dismiss()
    }

    private fun saveCSVFile(uri: Uri?) {
        val processCsvCard = ProcessCsvCard()
        val outputStream = uri?.let {
            context?.contentResolver?.openOutputStream(uri)
        }
        if (outputStream != null){
            processCsvCard.writeCsvFile(cardListForCsv, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        dismiss()
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val SAVE_STATUS = "save status"
        val CANCEL_STATUS = "cancel status"
        val SURNAME = "surname"
        val SAVING_TYPE_FILE_VCF = "application/vcf"
        val SAVING_TYPE_FILE_CSV = "text/csv"
        val TYPE_FILE_VCF = ".vcf"
        val TYPE_FILE_CSV = ".csv"
        val COMMON_NAME_FILE = "contacts"
        val TAG = SaveCardDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = SaveCardDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Long, String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
                    if (status != null) {
                        listener.invoke(cardId, status)
                    }
                })
        }
    }
}