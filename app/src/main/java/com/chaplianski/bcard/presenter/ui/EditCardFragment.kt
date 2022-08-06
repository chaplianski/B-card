package com.chaplianski.bcard.presenter.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentEditCardBinding
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.presenter.adapters.CardColorAdapter
import com.chaplianski.bcard.presenter.adapters.StrokeColorAdapter
import com.chaplianski.bcard.presenter.helpers.PhotoPicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class EditCardFragment : Fragment() {

    var _binding: FragmentEditCardBinding? = null
    val binding: FragmentEditCardBinding get() = _binding!!
    lateinit var photoPicker: PhotoPicker
    var checkedCornerSizeVariant = ""
    var checkedFormAvatarVariant = ""
    var cardColorVariant = ""
    var strokeColorVariant = ""

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
        val phoneField = binding.personInfo.otfUserPersonInfoPhone
        val phoneText = binding.personInfo.etCardImplPhone
        val emailField = binding.personInfo.otfUserPersonInfoEmail
        val emailText = binding.personInfo.etUserPersonInfoEmail
        val linkedinField = binding.personInfo.otfUserPersonInfoLinedin
        val linkedinText = binding.personInfo.etCardImplLinkedin
        val locationField = binding.personInfo.otfUserPersonInfoLocation
        val locationText = binding.personInfo.etUserPersonInfoLocation

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
            if (locationText.text?.isEmpty() == true) locationField.error = "Please, enter location"

            if (nameText.text?.isBlank() == false &&  phoneText.text?.isEmpty() == false && emailText.text?.isEmpty() == false && locationText.text?.isEmpty() == false) {

                val nameValue = nameField.editText?.text.toString()
                val phoneValue = phoneField.editText?.text.toString()
                val emailValue = emailField.editText?.text.toString()
                val linkedinValue = linkedinField.editText?.text.toString()
                val locationValue = locationField.editText?.text.toString()

                val newCard = Card(
                    0, nameValue, " ---", phoneValue, linkedinValue, emailValue, "", locationValue
                )

                findNavController().navigate(R.id.action_editCardFragment_to_cardsFragment)
            }


//            cardsFragmentViewModel.addCard()
        }



        avatar.setOnClickListener {
            addAvatarDialog()
        }



        changeErrorStatus(nameText, nameField)
        changeErrorStatus(phoneText, phoneField)
        changeErrorStatus(emailText, emailField)
        changeErrorStatus(locationText, locationField)




        // ***** Addition info Listeners *****

        val profileValue = profileInfo.text.toString()
        val profSkillsValue = profSkills.text.toString()
        val educationValue = education.text.toString()
        val workExperienceValue = workExperience.text.toString()
        val referenceValue = reference.text.toString()


        // ****** Setting card Listeners*****

        val cardColorsAdapter = CardColorAdapter()
        cardColors.layoutManager = GridLayoutManager(context, 5)
        cardColors.adapter = cardColorsAdapter

          cardColorsAdapter.colorCardClickListener = object: CardColorAdapter.ColorCardClickListener{
              override fun onShortClick(color: String) {
                  cardColorVariant = color
              }
          }

        cornerRound.setOnCheckedChangeListener { group, checkedId ->
            checkedCornerSizeVariant = when (checkedId){
                R.id.cb_corner_small -> "small"
                R.id.cb_corner_middle -> "middle"
                R.id.cb_corner_big -> "big"
                R.id.cb_corner_without -> "without"
                else -> {"unknown corner size"}
            }
        }



        formAvatar.setOnCheckedChangeListener { group, checkedId ->
            checkedFormAvatarVariant = when (checkedId) {
                R.id.cb_form_oval -> "oval"
                R.id.cb_form_rectangle -> "square"
                else -> {
                    "unknown form"
                }
            }
        }

//            Log.d("MyLog", "corner size = $checkedVariant")




        val strokeColorsAdapter = StrokeColorAdapter()
        strokeColors.layoutManager = GridLayoutManager(context, 6)
        strokeColors.adapter = strokeColorsAdapter

        strokeColorsAdapter.strokeColorListener = object : StrokeColorAdapter.StrokeColorListener{
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
//        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
//        val questionId = sharedPref?.getString(Constants.CURRENT_QUESTION_ID, "")

        val contentResolver = context?.getContentResolver()

        if (imageUri != null) {
            if (contentResolver != null) {
//                cardsFragmentViewModel.insertPhoto(imageUri)
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