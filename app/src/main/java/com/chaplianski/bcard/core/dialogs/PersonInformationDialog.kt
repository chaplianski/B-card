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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.factories.PersonInfoDialogViewModelFactory
import com.chaplianski.bcard.core.helpers.PhotoPicker
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.utils.CURRENT_USER_ID
import com.chaplianski.bcard.core.viewmodels.PersonInfoDialogViewModel
import com.chaplianski.bcard.databinding.DialogPersonInformationBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.PersonInfo
import javax.inject.Inject


class PersonInformationDialog : DialogFragment() {

    private var _binding: DialogPersonInformationBinding? = null
    val binding get() = _binding!!

    lateinit var photoPicker: PhotoPicker
    @Inject
    lateinit var personInfoDialogViewModelFactory: PersonInfoDialogViewModelFactory
    val personInfoDialogViewModel: PersonInfoDialogViewModel by viewModels { personInfoDialogViewModelFactory }

    var avatarUri = ""
//    var userIdValue = 0L

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .personInfoDialogInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 230
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogPersonInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_person_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoPicker =
            PhotoPicker(requireContext(), requireActivity().activityResultRegistry) { uri ->
                savePhotoToAnswer(uri)
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

        avatar.setOnClickListener {
            addAvatarDialog()
        }
        personInfoDialogViewModel.photoUri.observe(this.viewLifecycleOwner) {
            avatarUri = it
            Glide.with(requireContext()).load(avatarUri)
//                .override(150, 150)
                .placeholder(R.drawable.ic_portrait)
                .centerCrop()
                .into(avatar)
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var userID = sharedPref?.getLong(CURRENT_USER_ID, 0L)
        if (cardID != null) {
            personInfoDialogViewModel.getCardData(cardID)
        }

        personInfoDialogViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->

            Glide.with(requireContext()).load(card.photo)
                .override(150, 150)
                .centerCrop()
                .into(avatar)
            userID = card.userId
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
            if (nameText.text?.isEmpty() == true) nameField.error = "Please, enter name"
            if (surnameText.text?.isEmpty() == true) surnameField.error = "Please, enter surname"
            if (workPhoneText.text?.isEmpty() == true) workPhoneField.error = "Please, enter phone"
            if (nameText.text?.isNotEmpty() == true && surnameText.text?.isNotEmpty() == true && workPhoneText.text?.isNotEmpty() == true ){
                Log.d("MyLog", "save person info")
                val personInfo = cardID?.let {
                    PersonInfo(
                        id = cardID ?: 0L,
                        userId = userID ?: 0L,
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
                bundleOf( PERSON_INFO_DATA to personInfo)
            )
            dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun savePhotoToAnswer(imageUri: Uri?) {

        val contentResolver = context?.getContentResolver()

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

        //        val KEY_RESPONSE = "key response"
        val CHECKED_OPTION = "checked option"
        val PERSON_INFO_STATUS = "person information status"
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
            listener: (Long, Parcelable) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val personInfo = result.getParcelable<PersonInfo>(PERSON_INFO_DATA)
                    if (personInfo != null) {
                        listener.invoke(cardId, personInfo)
                    }

                })
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