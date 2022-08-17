package com.chaplianski.bcard.presenter.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract.Programs.Genres.EDUCATION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.chaplianski.bcard.R
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.presenter.factories.ShareFragmentViewModelFactory
import com.chaplianski.bcard.presenter.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.presenter.viewmodels.ShareFragmentViewModel
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.VCardVersion
import ezvcard.parameter.AddressType
import ezvcard.parameter.EmailType
import ezvcard.parameter.ImageType
import ezvcard.parameter.TelephoneType
import ezvcard.property.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject


class ShareFragment : Fragment() {

    @Inject
    lateinit var shareFragmentViewModelFactory: ShareFragmentViewModelFactory
    val shareFragmentViewModel: ShareFragmentViewModel by viewModels { shareFragmentViewModelFactory }

    var selectedFile = ""

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


        saveContact.setOnClickListener {

            if (cardId != null) {
                shareFragmentViewModel.getCard(cardId)
            }

            shareFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->
                val vCard = createVCard(card)
                val file = File("${card.name}.vcf")
                Ezvcard.write(vCard).version(VCardVersion.V4_0).go(file)

            }
        }

        loadContact.setOnClickListener {

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

        }

        saveQR.setOnClickListener {
            findNavController().navigate(R.id.action_shareFragment_to_QRFragment)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            selectedFile = data?.data.toString() //The uri with the location of the file
            val file = File(selectedFile)
            val vcard = Ezvcard.parse(file).first()
            addVCardData(vcard)
            Log.d("MyLog", "file = $selectedFile")
        }
    }

    @Throws(IOException::class)
    private fun createVCard(card: Card): VCard? {
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
        vcard.addTelephoneNumber(card.phone, TelephoneType.WORK)
//        vcard.addTelephoneNumber("1-555-555-5678", TelephoneType.WORK, TelephoneType.CELL)
        vcard.addEmail(card.email, EmailType.HOME)
//        vcard.addEmail("doe.john@acme.com", EmailType.WORK)
        vcard.addUrl("https://www.linkedin.com/in/${card.linkedin}")
//        vcard.setCategories("widgetphile", "biker", "vCard expert")
//        vcard.setGeo(37.6, -95.67)
//        val tz = TimeZone.getTimeZone("America/New_York")
//        vcard.timezone = Timezone(tz)
        var file = File("portrait.jpg")
        val photo = Photo(file, ImageType.JPEG)
        vcard.addPhoto(photo)
//        file = File("pronunciation.ogg")
//        val sound = Sound(file, SoundType.OGG)
//        vcard.addSound(sound)
        vcard.uid = Uid.random()
        vcard.revision = Revision.now()


        val profile = vcard.addExtendedProperty(PROFIL_INFO, card.profilInfo)
        val skills = vcard.addExtendedProperty(PROFESSIONAL_SKILLS, card.professionalSkills)
        val education = vcard.addExtendedProperty(EDUCATION, card.education)
        val workExperience = vcard.addExtendedProperty(WORK_EXPERIENCE, card.workExperience)
        val reference = vcard.addExtendedProperty(REFERENCE, card.reference)

        val cardColor = vcard.addExtendedProperty(CARD_COLOR, card.cardColor)
        val strokeColor = vcard.addExtendedProperty(STROKE_COLOR, card.strokeColor)
        val cardCorner = vcard.addExtendedProperty(CARD_CORNER, card.cornerRound.toString())
        val formPhoto = vcard.addExtendedProperty(FORM_PHOTO, card.formPhoto)

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
        var cardColorValue = ""
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
                CARD_COLOR -> cardColorValue = property.getParameter(CARD_COLOR)
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

    companion object {
        val ADD_INFO = "add info"
        val CARD_SETTINGS = "card settings"
        val PROFIL_INFO = "profil info"
        val PROFESSIONAL_SKILLS = "professional skills"
        val EDUCATION = "education"
        val WORK_EXPERIENCE = "work experience"
        val REFERENCE = "reference"
        val CARD_COLOR = "card color"
        val STROKE_COLOR = "stroke color"
        val CARD_CORNER = "card corner"
        val FORM_PHOTO = "form photo"
    }
}

