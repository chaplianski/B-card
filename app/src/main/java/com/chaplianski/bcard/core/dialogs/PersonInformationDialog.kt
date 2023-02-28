package com.chaplianski.bcard.core.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.helpers.PhotoPicker
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.utils.CURRENT_USER_ID
import com.chaplianski.bcard.core.viewmodels.PersonInfoDialogViewModel
import com.chaplianski.bcard.databinding.DialogCardSettingsBinding
import com.chaplianski.bcard.databinding.DialogPersonInformationBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.PersonInfo
import javax.inject.Inject


class PersonInformationDialog : //DialogFragment() {
    BasisDialogFragment<DialogPersonInformationBinding>(DialogPersonInformationBinding::inflate) {

    lateinit var photoPicker: PhotoPicker

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val personInfoDialogViewModel: PersonInfoDialogViewModel by viewModels { vmFactory }

    var avatarUri = ""
//    var userIdValue = 0L

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .personInfoDialogInject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoPicker =
            PhotoPicker(requireContext(), requireActivity().activityResultRegistry) { uri ->
                savePhotoContact(uri)
            }

        val avatar = binding.ivUserPersonInfoAvatar
        val nameText = binding.etUserPersonInfoName
        val nameField = binding.otfUserPersonInfoName
        val surnameText = binding.etUserPersonInfoSurname
        val surnameField = binding.otfUserPersonInfoSurname
        val workPhoneText = binding.etUserPersonInfoWorkphone
        val workPhoneField = binding.otfUserPersonInfoPhone
        val homePhoneText = binding.etUserPersonInfoHomephone
        val homePhoneField = binding.otfUserPersonInfoHomephone
        val specialityText = binding.etUserPersonInfoSpeciality
        val organizationText = binding.etUserPersonInfoOrganization
        val emailText = binding.etUserPersonInfoEmail
        val townText = binding.etUserPersonInfoTown
        val countryText = binding.etUserPersonInfoCountry
        val saveButton = binding.btUserPersonInfoOk
        val cancelButton = binding.btUserPersonInfoCancel
        val cardID = arguments?.getLong(CURRENT_CARD_ID, 0L)
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        Log.d("MyLog", "onViewCreated person info, cardId = $cardID")

        avatar.setOnClickListener {
            photoPicker.getAndCropPhoto()
        }

        setupChooseVariantAddingPhoto()

        personInfoDialogViewModel.photoUri.observe(this.viewLifecycleOwner) {
            avatarUri = it
            Glide.with(requireContext()).load(avatarUri)
                .placeholder(R.drawable.ic_portrait)
                .centerCrop()
                .into(avatar)
        }

        val sharedPref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        var userId = sharedPref?.getLong(CURRENT_USER_ID, 0)
        if (cardID != null && cardID != 0L) {
            personInfoDialogViewModel.getCardData(cardID)
        }

        personInfoDialogViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->

            Glide.with(requireContext()).load(card.photo)
                .override(150, 150)
                .centerCrop()
                .into(avatar)
            userId = card.userId
            avatarUri = card.photo
            nameText.setText(card.name)
            surnameText.setText(card.surname)
            workPhoneText.setText(card.workPhone)
            emailText.setText(card.email)
            homePhoneText.setText(card.homePhone)
            townText.setText(card.town)
            countryText.setText(card.country)
            specialityText.setText(card.speciality)
            organizationText.setText(card.organization)
        }

        nameText.addTextChangedListener {
            if (nameText.text?.isNotEmpty() == true) nameField.error = null
        }

        surnameText.addTextChangedListener {
            if (nameText.text?.isNotEmpty() == true) surnameField.error = null
        }

        workPhoneText.addTextChangedListener {
            if (nameText.text?.isNotEmpty() == true) workPhoneField.error = null
        }

        saveButton.setOnClickListener {
            if (nameText.text?.isEmpty() == true) nameField.error = getString(R.string.please_enter_name)
            if (surnameText.text?.isEmpty() == true) surnameField.error = getString(R.string.please_enter_surname)
            if (workPhoneText.text?.isEmpty() == true && homePhoneText.text?.isEmpty() == false) workPhoneField.error =
                getString(R.string.please_enter_phone)
            if (nameText.text?.isNotEmpty() == true && surnameText.text?.isNotEmpty() == true && workPhoneText.text?.isNotEmpty() == true) {
                val personInfo = cardID?.let {
                    PersonInfo(
                        id = cardID ?: 0L,
                        userId = userId ?: 0L,
                        name = nameText.text?.toString() ?: "",
                        surname = surnameText.text?.toString() ?: "",
                        photo = avatarUri ?: "",
                        workPhone = workPhoneText.text?.toString() ?: "",
                        homePhone = homePhoneText.text?.toString() ?: "",
                        email = emailText.text?.toString() ?: "",
                        speciality = specialityText.text?.toString() ?: "",
                        organization = organizationText.text?.toString() ?: "",
                        town = townText.text?.toString() ?: "",
                        country = countryText.text?.toString() ?: ""
                    )
                }
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        CHECKED_OPTION to SAVE_STATUS,
                        CURRENT_CARD_ID to cardID,
                        PERSON_INFO_DATA to personInfo
                    )
                )
                dismiss()
            }
        }
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CHECKED_OPTION to CANCEL_STATUS,
                    CURRENT_CARD_ID to cardID,
                    PERSON_INFO_DATA to PersonInfo()
                )
            )
            dismiss()
        }
    }

    private fun setupChooseVariantAddingPhoto() {
        ChooseVariantAddingPhotoDialog.setupListener(
            parentFragmentManager,
            this.viewLifecycleOwner
        ) { status ->
            when (status) {
                ChooseVariantAddingPhotoDialog.GALLERY_STATUS -> photoPicker.pickPhoto()
                ChooseVariantAddingPhotoDialog.CAMERA_STATUS -> photoPicker.takePhoto()
            }
        }
    }

    private fun savePhotoContact(imageUri: Uri?) {

        val contentResolver = context?.contentResolver

        if (imageUri != null) {
            if (contentResolver != null) {
                context?.let {
                    personInfoDialogViewModel.insertPhoto(
                        imageUri, contentResolver,
                        it
                    )
                }
            }
        }
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val SAVE_STATUS = "person save button status"
        val CANCEL_STATUS = "person cancel button status"
        val PERSON_INFO_DATA = "person information data"

        val TAG = PersonInformationDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = PersonInformationDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Long, String, Parcelable) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
                    val personInfo = result.getParcelable<PersonInfo>(PERSON_INFO_DATA)
                    if (personInfo != null && status != null) {
                        listener.invoke(cardId, status, personInfo)
                    }
                })
        }
    }
}