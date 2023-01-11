package com.chaplianski.bcard.core.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.factories.CheckCardListFragmentViewModelFactory
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.CheckCardListFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCheckCardListSaveBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.AddressType
import ezvcard.parameter.EmailType
import ezvcard.parameter.ImageType
import ezvcard.parameter.TelephoneType
import ezvcard.property.*
import java.io.File
import java.io.IOException
import javax.inject.Inject


class CheckCardListSaveFragment : Fragment() {

    var _binding: FragmentCheckCardListSaveBinding? = null
    val binding get() = _binding!!
    var vCard: VCard? = null
    val vcardList = mutableListOf<VCard>()

    @Inject
    lateinit var checkCardListFragmentViewModelFactory: CheckCardListFragmentViewModelFactory
    val checkCardListFragmentViewModel: CheckCardListFragmentViewModel by viewModels { checkCardListFragmentViewModelFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .checkCardListFragmentInject(this)
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentCheckCardListSaveBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardRV = binding.rvCheckCardFragment
        val cardListAdapter = CardListShareFragmentAdapter()
        val saveButton = binding.btCheckCardFragmentSave
        val cancelButton = binding.btCheckCardFragmentCancel
        val quantityCheckedCards = binding.tvCheckCardListFragmentQuantityCards
        val allCheckBox = binding.checkBoxCheckCardListFragmentCheckAll
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)
        var checkedCardCount = 0
        var checkAllFlag = false

        quantityCheckedCards.text = "$checkedCardCount cards"

        checkCardListFragmentViewModel.getCardList()

        checkCardListFragmentViewModel.cardList.observe(this.viewLifecycleOwner){

            val cardList = it.sortedBy { it.surname }
            var positionCurrentCheckedCard = 0
            if (currentCardId != null){
                cardList.forEachIndexed { index, cardItem ->
                    if (cardItem.id == currentCardId) {
                        cardItem.isChecked = true
                        positionCurrentCheckedCard = index
                    }
                }
                checkedCardCount = 1
                saveButton.text = "Save [$checkedCardCount]"
//                quantityCheckedCards.text = "$checkedCardCount cards"
            }

            cardRV.layoutManager = LinearLayoutManager(context)
            cardRV.adapter = cardListAdapter
            cardListAdapter.updateList(cardList)
            cardRV.scrollToPosition(positionCurrentCheckedCard)

            val listCard = mutableListOf<Card>()

            cardListAdapter.checkBoxListener = object : CardListShareFragmentAdapter.CheckBoxListener{
                override fun onCheck(card: Card) {
                    cardList.forEach { cardItem ->
                        if (cardItem.id == card.id) {
                            cardItem.isChecked = !cardItem.isChecked
                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
                        }
//                        quantityCheckedCards.text = "$checkedCardCount cards"
                        saveButton.text = "Save [$checkedCardCount]"
                    }
                }
            }

            saveButton.setOnClickListener {
                vcardList.clear()
                listCard.clear()
                cardList.forEach { cardItem ->
                    if (cardItem.isChecked){
                        listCard.add(cardItem)
                        vcardList.add(createVCard(cardItem))
                    }

                }

                Log.d("MyLog", "${vcardList.size}")
                if (!listCard.isNullOrEmpty()){
                    when(listCard.size){
                        1 -> {
                            val card = listCard[0]
                            val file = File("${card.surname}.vcf")
                            saveVCardToPhone(file, card.surname)
                        }
                        else -> {
                            val file = File("contacts.vcf")
                            saveVCardToPhone(file, "contacts")
                        }

                    }
                }

                //                Log.d("MyLog", "list checked = ${listCard.size}")
            }

            allCheckBox.setOnClickListener {
               if (!checkAllFlag){
                    cardList.forEach { cardItem ->
                        cardItem.isChecked = true
                    }
                    checkedCardCount = cardList.size
               } else {
                    cardList.forEach { cardItem ->
                        cardItem.isChecked = false
                    }
                   checkedCardCount = 0
               }
                Log.d("MyLog", "${cardList.map { it.isChecked }}")
                checkAllFlag = !checkAllFlag
                cardListAdapter.updateList(cardList.map { it.copy() })
//                quantityCheckedCards.text = "$checkedCardCount cards"
                saveButton.text = "Save [$checkedCardCount]"
            }

        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_checkCardListSaveFragment_to_shareFragment)
        }


    }

    private fun saveVCardToPhone(file: File, fileName: String) {
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        path.mkdirs()
        val uriForFile = Uri.fromFile(file)
        try {
            createFile(uriForFile, "${fileName}.vcf")
        } catch (e: Exception) {
            Log.d("MyLog", "e = $e")
        }
    }

    @Throws(IOException::class)
    private fun createVCard(card: Card): VCard {
        val vcard = VCard()
        vcard.kind = Kind.individual()
//        vcard.gender = Gender.male()
//        vcard.addLanguage("en-US")
        val n = StructuredName()
        n.family = card.name//"Doe"
        n.given = card.surname //"Jonathan"
//        n.prefixes.add("Mr")
        vcard.structuredName = n
        vcard.setFormattedName("${card.name} ${card.surname}")
//        vcard.setNickname("John", "Jonny")
        vcard.addTitle(card.speciality)
        vcard.setOrganization(card.organization)
        val adr = Address()
//        adr.streetAddress = "123 Wall St."
        adr.locality = card.town
//        adr.region = "NY"
//        adr.postalCode = "12345"
        adr.country = card.country
//        adr.label = "123 Wall St.\nNew York, NY 12345\nUSA"
        adr.types.add(AddressType.WORK)
//        vcard.addAddress(adr)
//        adr = Address()
//        adr.streetAddress = "123 Main St."
//        adr.locality = "Albany"
//        adr.region = "NY"
//        adr.postalCode = "54321"
//        adr.country = "USA"
//        adr.label = "123 Main St.\nAlbany, NY 54321\nUSA"
//        adr.types.add(AddressType.HOME)
//        vcard.addAddress(adr)
        vcard.addTelephoneNumber(card.workPhone, TelephoneType.WORK)
//        vcard.addTelephoneNumber("1-555-555-5678", TelephoneType.WORK, TelephoneType.CELL)
        vcard.addEmail(card.email, EmailType.HOME)
//        vcard.addEmail("doe.john@acme.com", EmailType.WORK)
        vcard.addTelephoneNumber(card.homePhone, TelephoneType.HOME)
//        vcard.setCategories("widgetphile", "biker", "vCard expert")
//        vcard.setGeo(37.6, -95.67)
//        val tz = TimeZone.getTimeZone("America/New_York")
//        vcard.timezone = Timezone(tz)
        val file = File(card.photo)
//        var file = File("portrait.jpg")
        val photo = Photo(file, ImageType.JPEG)
        vcard.addPhoto(photo)


//        file = File("pronunciation.ogg")
//        val sound = Sound(file, SoundType.OGG)
//        vcard.addSound(sound)
        vcard.uid = Uid.random()
        vcard.revision = Revision.now()


        val profile = vcard.addExtendedProperty(ShareFragment.PROFIL_INFO, card.profileInfo)
        val skills = vcard.addExtendedProperty(ShareFragment.PROFESSIONAL_SKILLS, card.professionalSkills)
        val education = vcard.addExtendedProperty(ShareFragment.EDUCATION, card.education)
        val workExperience = vcard.addExtendedProperty(ShareFragment.WORK_EXPERIENCE, card.workExperience)
        val reference = vcard.addExtendedProperty(ShareFragment.REFERENCE, card.reference)

        val cardColor = vcard.addExtendedProperty(ShareFragment.CARD_COLOR, card.cardColor.toString())
        val strokeColor = vcard.addExtendedProperty(ShareFragment.STROKE_COLOR, card.strokeColor)
        val cardCorner = vcard.addExtendedProperty(ShareFragment.CARD_CORNER, card.cornerRound.toString())
        val formPhoto = vcard.addExtendedProperty(ShareFragment.FORM_PHOTO, card.formPhoto)

        profile.group = ShareFragment.ADD_INFO
        skills.group = ShareFragment.ADD_INFO
        education.group = ShareFragment.ADD_INFO
        workExperience.group = ShareFragment.ADD_INFO
        reference.group = ShareFragment.ADD_INFO
        cardColor.group = ShareFragment.CARD_SETTINGS
        strokeColor.group = ShareFragment.CARD_SETTINGS
        cardCorner.group = ShareFragment.CARD_SETTINGS
        formPhoto.group = ShareFragment.CARD_SETTINGS

        Log.d("MyLog", "vcard = $vcard")
        return vcard
    }

    private fun createFile(pickerInitialUri: Uri, fileName: String){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vcf"
            putExtra(Intent.EXTRA_TITLE, fileName)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, ShareFragment.CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            val outputStream = data?.data?.let {
                context?.contentResolver?.openOutputStream(it)
            }
//            outputStream?.write(vCard)
            Ezvcard.write(vcardList).go(outputStream)
            outputStream?.flush()
            outputStream?.close()
//            Ezvcard.write(vCard).version(VCardVersion.V3_0).go(file)
//            val file = File(selectedFile)
//            val vcard = Ezvcard.parse(file).first()
//            addVCardData(vcard)
            Log.d("MyLog", "file = $selectedFile")
        }
        if (requestCode == 222 && resultCode == Activity.RESULT_OK){
            data?.data?.also {

                val inputStream = context?.contentResolver?.openInputStream(it)
//                val file = File(it.path.toString())
                val readVcard = Ezvcard.parse(inputStream).first()
                Log.d("MyLog", "readVcard = $readVcard")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}