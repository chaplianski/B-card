package com.chaplianski.bcard.presenter.ui

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentEditCardBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.presenter.adapters.CardColorAdapter
import com.chaplianski.bcard.presenter.adapters.StrokeColorAdapter
import com.chaplianski.bcard.presenter.factories.EditCardFragmentViewModelFactory
import com.chaplianski.bcard.presenter.helpers.PhotoPicker
import com.chaplianski.bcard.presenter.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.presenter.utils.CURRENT_USER_ID
import com.chaplianski.bcard.presenter.viewmodels.EditCardFragmentViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject


class EditCardFragment : Fragment() {

    @Inject
    lateinit var editCardFragmentViewModelFactory: EditCardFragmentViewModelFactory
    val editCardFragmentViewModel: EditCardFragmentViewModel by viewModels { editCardFragmentViewModelFactory }


    var _binding: FragmentEditCardBinding? = null
    val binding: FragmentEditCardBinding get() = _binding!!
    lateinit var photoPicker: PhotoPicker
    var checkedCornerSizeVariant = 1F
    var checkedFormAvatarVariant = ""
    var cardColorVariant = ""
    var strokeColorVariant = ""
    var avatarUri = ""

    override fun onAttach(context: Context) {
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .editCardFragmentInject(this)
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoPicker =
            PhotoPicker(context!!, requireActivity().activityResultRegistry) { uri ->
                savePhotoToAnswer(uri)
            }
        // ***** Person info values *****
        val personInfo: ConstraintLayout = binding.personInfo.clUserPersonInfo
        val personInfoButton: TextView = binding.tvEditFragmentProfileTitle
        val avatar: ImageView = binding.personInfo.ivUserPersonInfoAvatar
        val nameField = binding.personInfo.otfUserPersonInfoName
        val nameText = binding.personInfo.etUserPersonInfoName
        val surnameField = binding.personInfo.otfUserPersonInfoSurname
        val surnameText = binding.personInfo.etUserPersonInfoSurname
        val phoneField = binding.personInfo.otfUserPersonInfoPhone
        val phoneText = binding.personInfo.etCardImplPhone
        val emailField = binding.personInfo.otfUserPersonInfoEmail
        val emailText = binding.personInfo.etUserPersonInfoEmail
        val linkedinField = binding.personInfo.otfUserPersonInfoLinedin
        val linkedinText = binding.personInfo.etCardImplLinkedin
        val townField = binding.personInfo.otfUserPersonInfoTown
        val townText = binding.personInfo.etUserPersonInfoTown
        val countryField = binding.personInfo.otfUserPersonInfoCountry
        val countryText = binding.personInfo.etUserPersonInfoCountry
        val specialityField = binding.personInfo.otfUserPersonInfoSpeciality
        val specialityText = binding.personInfo.etUserPersonInfoSpeciality
        val organizationField = binding.personInfo.otfUserPersonInfoOrganization
        val organizationText = binding.personInfo.etUserPersonInfoOrganization

        // ***** Addition info values
        val additionInfo: ConstraintLayout = binding.additionInfo.clUserInfo
        val additionInfoButton: TextView = binding.tvEditFragmentAdditionInfo
        val profileInfo: EditText = binding.additionInfo.userEditInformationProfileInfo
        val profSkills: EditText = binding.additionInfo.userEditInformationProfSkills
        val education: EditText = binding.additionInfo.userEditInformationEducation
        val workExperience: EditText = binding.additionInfo.userEditInformationWorkExperience
        val reference: EditText = binding.additionInfo.userEditInformationReference

        // ***** Setting card values
        val cardSettings: ConstraintLayout = binding.cardSettings.clCardSettings
        val cardSettingsButton: TextView = binding.tvEditFragmentProfileCardSettings
        val cardColors: RecyclerView = binding.cardSettings.rvCardSettingsColor
        val strokeColors: RecyclerView = binding.cardSettings.rvCardSettingsStrokeColor
        val cornerRound: RadioGroup = binding.cardSettings.rgCardSettingsCornersVariant
        val formAvatar: RadioGroup = binding.cardSettings.rgCardSettingsFormPhoto


        val saveButtonEdit: Button = binding.btEditCardSave
        personInfo.visibility = View.VISIBLE


        // ***** Fill fields  *****

        val cardID = arguments?.getLong(CURRENT_CARD_ID, 0L)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var userID = sharedPref?.getLong(CURRENT_USER_ID, 0L)
        if (cardID != null){
            editCardFragmentViewModel.getCardData(cardID)
        }

        editCardFragmentViewModel.currentCard.observe(this.viewLifecycleOwner){ card ->

            Glide.with(context!!).load(card.photo)
                .override(150, 150)
                .centerCrop()
                .into(avatar)
            userID = card.userId
            avatarUri = card.photo
            nameText.setText(card.name)
            surnameText.setText(card.surname)
            phoneText.setText(card.phone)
            emailText.setText(card.email)
            linkedinText.setText(card.linkedin)
            townText.setText(card.town)
            countryText.setText(card.country)
            specialityText.setText(card.speciality)
            organizationText.setText(card.organization)
            profileInfo.setText(card.profilInfo)
            profSkills.setText(card.professionalSkills)
            education.setText(card.education)
            workExperience.setText(card.workExperience)
            reference.setText(card.reference)
            cardColorVariant = card.cardColor
            strokeColorVariant = card.strokeColor
            checkedFormAvatarVariant = card.formPhoto
            checkedCornerSizeVariant = card.cornerRound


            // ****** Setting card Listeners*****

            fillSettingParameters(cardColors, cornerRound, formAvatar, strokeColors)
        }


        // ***** Common Listeners *****
        personInfoButton.setOnClickListener {

            personInfo.visibility =
                if (personInfo.isVisible) View.GONE
                else View.VISIBLE
            if (additionInfo.isVisible) additionInfo.visibility = View.GONE
            if (cardSettings.isVisible) cardSettings.visibility = View.GONE
        }

        additionInfoButton.setOnClickListener {

            additionInfo.visibility =
                if (additionInfo.isVisible) View.GONE
                else View.VISIBLE
            if (personInfo.isVisible) personInfo.visibility = View.GONE
            if (cardSettings.isVisible) cardSettings.visibility = View.GONE
        }

        cardSettingsButton.setOnClickListener {

            cardSettings.visibility =
                if (cardSettings.isVisible) View.GONE
                else View.VISIBLE
            if (additionInfo.isVisible) additionInfo.visibility = View.GONE
            if (personInfo.isVisible) personInfo.visibility = View.GONE


        }

        //***** Person info Listener *****

        saveButtonEdit.setOnClickListener {

            if (nameText.text?.isEmpty() == true) {
                nameField.error = "Please, enter name"
                personInfo.visibility = View.VISIBLE
                if (additionInfo.isVisible) additionInfo.visibility = View.GONE
                if (cardSettings.isVisible) cardSettings.visibility = View.GONE
            }
            if (phoneText.text?.isEmpty() == true)  phoneField.error = "Please, enter phone"
            if (emailText.text?.isEmpty() == true) emailField.error = "Please, enter email"
//            if (locationText.text?.isEmpty() == true) locationField.error = "Please, enter location"

            if (nameText.text?.isBlank() == false &&  phoneText.text?.isEmpty() == false && emailText.text?.isEmpty() == false) { // && locationText.text?.isEmpty() == false) {

                var cardIdValue = 0L
                var userIdValue = 0L
                if (cardID != null && userID != null) {
                    cardIdValue = cardID
                    userIdValue = userID as Long
                }
                val nameValue = nameField.editText?.text.toString()
                val surnameValue = surnameField.editText?.text.toString()
                val phoneValue = phoneField.editText?.text.toString()
                val emailValue = emailField.editText?.text.toString()
                val linkedinValue = linkedinField.editText?.text.toString()
                val townValue = townField.editText?.text.toString()
                val countryValue = countryField.editText?.text.toString()
                val specialityValue = specialityField.editText?.text.toString()
                val organizationValue = organizationField.editText?.text.toString()
                val profiInfoValue = profileInfo.text.toString()
                val profSkillsValue = profSkills.text.toString()
                val educationValue = education.text.toString()
                val workExperienceValue = workExperience.text.toString()
                val referenceValue = reference.text.toString()
                val cardColorValue = if (cardColorVariant == "") "#F5F5F5" else cardColorVariant
                val strokeColorValue = if(strokeColorVariant == "") "#616161" else strokeColorVariant
                val cardCornerValue = if(checkedCornerSizeVariant == 1f) 30f else checkedCornerSizeVariant
                val formCardValue = if (checkedFormAvatarVariant == "") "oval" else checkedFormAvatarVariant
                val newCard = Card(
                    cardIdValue, userIdValue, nameValue, surnameValue, avatarUri, phoneValue, linkedinValue, emailValue,
                    specialityValue, organizationValue, townValue, countryValue,profiInfoValue,educationValue,profSkillsValue,
                    workExperienceValue,referenceValue,cardColorValue,strokeColorValue,
                    cardCornerValue,formCardValue
                )
                Log.d("MyLog", "card = $newCard")


                if (cardIdValue == 0L){
                    editCardFragmentViewModel.addCard(newCard)
                } else editCardFragmentViewModel.updateCard(newCard)

                findNavController().navigate(R.id.action_editCardFragment_to_cardsFragment)
            }
        }
        fillSettingParameters(cardColors, cornerRound, formAvatar, strokeColors)

        avatar.setOnClickListener {
            addAvatarDialog()
        }
        editCardFragmentViewModel.photoUri.observe(this.viewLifecycleOwner){
            avatarUri = it
            Glide.with(context!!).load(avatarUri)
                .override(150, 150)
                .placeholder(R.drawable.ic_portrait)
                .centerCrop()
                .into(avatar)
        }
        changeErrorStatus(nameText, nameField)
        changeErrorStatus(phoneText, phoneField)
        changeErrorStatus(emailText, emailField)
//        changeErrorStatus(locationText, locationField)
    }

    private fun fillSettingParameters(
        cardColors: RecyclerView,
        cornerRound: RadioGroup,
        formAvatar: RadioGroup,
        strokeColors: RecyclerView
    ) {
        val cardColorsAdapter = CardColorAdapter(cardColorVariant)
        cardColors.layoutManager = GridLayoutManager(context, 6)
        cardColors.adapter = cardColorsAdapter

        cardColorsAdapter.colorCardClickListener =
            object : CardColorAdapter.ColorCardClickListener {
                override fun onShortClick(color: String) {
                    cardColorVariant = color
                }
            }

        val smallCorner = view?.findViewById<RadioButton>(R.id.cb_corner_small)
        val middleCorner = view?.findViewById<RadioButton>(R.id.cb_corner_middle)
        val bigCorner = view?.findViewById<RadioButton>(R.id.cb_corner_big)
        val withoutCorner = view?.findViewById<RadioButton>(R.id.cb_corner_without)

        if (checkedCornerSizeVariant != 1F){
            when (checkedCornerSizeVariant){
                5F -> smallCorner?.isChecked = true
                25F -> middleCorner?.isChecked = true
                50F -> bigCorner?.isChecked = true
                else -> withoutCorner?.isChecked = true
            }
        }

        cornerRound.setOnCheckedChangeListener { group, checkedId ->
            checkedCornerSizeVariant = when (checkedId) {
                R.id.cb_corner_small -> 5F
                R.id.cb_corner_middle -> 25F
                R.id.cb_corner_big -> 50F
                else -> 0F
            }
        }

        val ovalButton = view?.findViewById<RadioButton>(R.id.cb_form_oval)
        val squareButton = view?.findViewById<RadioButton>(R.id.cb_form_rectangle)

        if (checkedFormAvatarVariant != ""){
            if (checkedFormAvatarVariant == "oval") ovalButton?.isChecked = true
            else squareButton?.isChecked = true
        }

        formAvatar.setOnCheckedChangeListener { group, checkedId ->
            checkedFormAvatarVariant = when (checkedId) {
                R.id.cb_form_oval -> "oval"
                else -> "square"
            }
        }

        val strokeColorsAdapter = StrokeColorAdapter(strokeColorVariant)
        strokeColors.layoutManager = GridLayoutManager(context, 6)
        strokeColors.adapter = strokeColorsAdapter

        strokeColorsAdapter.strokeColorListener = object : StrokeColorAdapter.StrokeColorListener {
            override fun onShortClick(strokeColor: String) {
                strokeColorVariant = strokeColor
            }

        }
    }

    private fun changeErrorStatus(
        text: TextInputEditText,
        field: TextInputLayout
    ) {
        text.doOnTextChanged { inputText, _, _, _ ->
            if (inputText?.length!! > 0) {
                field.error = null
            }
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun savePhotoToAnswer(imageUri: Uri?) {

        val contentResolver = context?.getContentResolver()

        if (imageUri != null) {
            if (contentResolver != null) {
                context?.let {
                    editCardFragmentViewModel.insertPhoto(imageUri, contentResolver,
                        it
                    )
                }
            }
        }
    }

    private fun addAvatarDialog() {
        val photoDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Choose variant of adding photo")
        builder.setItems(photoDialogItems) { _, position ->
            when (position) {
                0 -> photoPicker.pickPhoto()
                1 -> photoPicker.takePhoto()
            }
        }
        builder.show()
    }
}