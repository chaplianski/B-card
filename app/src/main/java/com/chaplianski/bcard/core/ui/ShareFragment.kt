package com.chaplianski.bcard.core.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.factories.ShareFragmentViewModelFactory
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.ShareFragmentViewModel
import com.chaplianski.bcard.di.DaggerAppComponent
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


class ShareFragment : Fragment() {

    @Inject
    lateinit var shareFragmentViewModelFactory: ShareFragmentViewModelFactory
    val shareFragmentViewModel: ShareFragmentViewModel by viewModels { shareFragmentViewModelFactory }

    var selectedFile = ""
    var vCard: VCard? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .shareFragmentInject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadContact: Button = view.findViewById(R.id.bt_share_fragment_load)
        val saveContact: Button = view.findViewById(R.id.bt_share_fragment_save)
        val loadQR: Button = view.findViewById(R.id.bt_share_fragment_qr_load)
        val saveQR: Button = view.findViewById(R.id.bt_share_fragment_qr_save)
        val cardId = arguments?.getLong(CURRENT_CARD_ID)


        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveContact.isEnabled = false
        }

        saveContact.setOnClickListener {

            val bundle = Bundle()
            if (cardId != null) {
                bundle.putLong(CURRENT_CARD_ID, cardId)
            }
//            findNavController().navigate(R.id.action_shareFragment_to_checkCardListFragment, bundle)

//            if (cardId != null) {
//                shareFragmentViewModel.getCard(cardId)
//            }
//
//            shareFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->
//
//                vCard = createVCard(card)
//                val path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOCUMENTS
//                )
//                path.mkdirs()
//                val file = File("${card.surname}.vcf")
//                val uriForFile = Uri.fromFile(file)
//
//                try {
//                   createFile(uriForFile, "${card.surname}.vcf")
//                }catch (e: Exception){
//                    Log.d("MyLog", "e = $e")
//                }
//
//            }
        }

        loadContact.setOnClickListener {
//            findNavController().navigate(R.id.action_shareFragment_to_checkCardListLoadFragment)
//            val intent = Intent()
//                .setType("*/*")
//                .setAction(Intent.ACTION_GET_CONTENT)
//            startActivityForResult(Intent.createChooser(intent, "Select a file"), 222)

        }

        saveQR.setOnClickListener {
//            findNavController().navigate(R.id.action_shareFragment_to_QRFragment)
        }

//        shareFragmentViewModel.loadedCardList.observe(this.viewLifecycleOwner){
//            Log.d("MyLog", "cardList = ${it.map { it.surname }}")
//        }

    }




    private fun createFile(pickerInitialUri: Uri, fileName: String){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vcf"
            putExtra(Intent.EXTRA_TITLE, fileName)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    private fun openFile(pickerInitialUri: Uri){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vcf"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, PICK_FILE)
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            val outputStream = data?.data?.let {
                context?.contentResolver?.openOutputStream(it)
            }
//            outputStream?.write(vCard)
            Ezvcard.write(vCard).go(outputStream)
            outputStream?.flush()
            outputStream?.close()
//            Ezvcard.write(vCard).version(VCardVersion.V3_0).go(file)
//            val file = File(selectedFile)
//            val vcard = Ezvcard.parse(file).first()
//            addVCardData(vcard)
            Log.d("MyLog", "file = $selectedFile")
        }
        if (requestCode == 222 && resultCode == RESULT_OK){
            data?.data?.also {
                lifecycleScope.launchWhenResumed {
                    val inputStream = context?.contentResolver?.openInputStream(it)
//                val file = File(it.path.toString())
                    val readVcard = Ezvcard.parse(inputStream).all()
//                    shareFragmentViewModel.convertVCardListToCardList(readVcard)
//                    readVcard.forEach {
//                        Log.d("MyLog", "readVcard = $it.")
//                    }

                }

            }
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


        val profile = vcard.addExtendedProperty(PROFIL_INFO, card.additionalContactInfo)
        val skills = vcard.addExtendedProperty(PROFESSIONAL_SKILLS, card.professionalInfo)
        val education = vcard.addExtendedProperty(EDUCATION, card.education)
        val workExperience = vcard.addExtendedProperty(WORK_EXPERIENCE, card.privateInfo)
        val reference = vcard.addExtendedProperty(REFERENCE, card.reference)

        val cardColor = vcard.addExtendedProperty(CARD_COLOR, card.cardTexture.toString())
        val strokeColor = vcard.addExtendedProperty(STROKE_COLOR, card.cardTextColor)
        val cardCorner = vcard.addExtendedProperty(CARD_CORNER, card.isCardCorner.toString())
        val formPhoto = vcard.addExtendedProperty(FORM_PHOTO, card.cardFormPhoto)

        profile.group = ADD_INFO
        skills.group = ADD_INFO
        education.group = ADD_INFO
        workExperience.group = ADD_INFO
        reference.group = ADD_INFO
        cardColor.group = CARD_SETTINGS
        strokeColor.group = CARD_SETTINGS
        cardCorner.group = CARD_SETTINGS
        formPhoto.group = CARD_SETTINGS

        Log.d("MyLog", "vcard = $vcard")
        return vcard
    }

    fun addVCardData(vcard: VCard) {
        val formatedName = vcard.formattedName
        val addInfoProperties = vcard.getExtendedProperties(ADD_INFO)
        val cardSettingsProperties = vcard.getExtendedProperties(CARD_SETTINGS)

        val surnameValue = vcard.structuredName.family
        val nameValue = vcard.structuredName.given
        val phoneValue = vcard.telephoneNumbers[0].text
        val emailValue = vcard.emails[0].value
        val townValue = vcard.addresses[0].locality
        val countryValue = vcard.addresses[0].country
        val specialityValue = vcard.titles[0].value
        val organizationValue = vcard.organization.values[0]
        val linkedInValue = vcard.urls[0].value
        val photoValue = vcard.photos[0].url
        var profilInfoValue = ""
        var profSkillsValue = ""
        var educationValue = ""
        var workExperienceValue = ""
        var referenceValue = ""
        var cardColorValue = 0
        var strokeColorValue = ""
        var cardCornerRadiusValue = 0f
        var formPhotoValue = ""

        for (property in addInfoProperties) {
            when (property.propertyName) {
                PROFIL_INFO -> profilInfoValue = property.getParameter(PROFIL_INFO)
                PROFESSIONAL_SKILLS -> profSkillsValue = property.getParameter(PROFESSIONAL_SKILLS)
                EDUCATION -> educationValue = property.getParameter(EDUCATION)
                WORK_EXPERIENCE -> workExperienceValue = property.getParameter(WORK_EXPERIENCE)
                REFERENCE -> referenceValue = property.getParameter(REFERENCE)
            }
        }
        for (property in cardSettingsProperties) {
            when (property.propertyName) {
                CARD_COLOR -> cardColorValue = property.getParameter(CARD_COLOR).toInt()
                PROFESSIONAL_SKILLS -> strokeColorValue = property.getParameter(STROKE_COLOR)
                EDUCATION -> cardCornerRadiusValue =
                    property.getParameter(CARD_CORNER).toString().toFloat()
                WORK_EXPERIENCE -> formPhotoValue = property.getParameter(FORM_PHOTO)

            }
        }

        val card = Card(
            0,
            0,
            nameValue,
            surnameValue,
            photoValue,
            phoneValue,
            linkedInValue,
            emailValue,
            specialityValue,
            organizationValue,
            townValue,
            countryValue,
            profilInfoValue,
            educationValue,
            profSkillsValue,
            workExperienceValue,
            referenceValue,
            cardColorValue,
            strokeColorValue,
            cardCornerRadiusValue,
            formPhotoValue
        )

        shareFragmentViewModel.addCard(card)


    }

    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }

    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    fun decodePicString (encodedString: String): Bitmap {
        val imageBytes = Base64.decode(encodedString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return decodedImage
    }

    companion object {
        val ADD_INFO = "add info"
        val CARD_SETTINGS = "card settings"
        val PROFIL_INFO = "profile info"
        val PROFESSIONAL_SKILLS = "professional skills"
        val EDUCATION = "education"
        val WORK_EXPERIENCE = "work experience"
        val REFERENCE = "reference"
        val CARD_COLOR = "card color"
        val STROKE_COLOR = "stroke color"
        val CARD_CORNER = "card corner"
        val FORM_PHOTO = "form photo"

        const val CREATE_FILE = 111
        const val PICK_FILE = 222
    }
}

