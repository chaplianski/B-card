package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.AdditionalInfoDialogViewModel
import com.chaplianski.bcard.databinding.DialogAdditionalInformationBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.core.model.AdditionalInfo
import javax.inject.Inject

class AdditionalInformationDialog :
    BasisDialogFragment<DialogAdditionalInformationBinding>(DialogAdditionalInformationBinding::inflate){

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val additionalInfoDialogViewModel: AdditionalInfoDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .additionalInfoDialogInject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addInfoText = binding.etUserAdditionalInfoAddress
        val workInformationText = binding.etUserAdditionalInfoProfSkills
        val privateInfoText = binding.etUserAdditionalInfoPrivateInfo
        val referenceText = binding.editUserAdditionalInfoReference
        val saveButton = binding.btAdditionalInfoSave
        val cancelButton = binding.btAdditionalInfoCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        if (currentCardId != null && currentCardId != 0L) {
            additionalInfoDialogViewModel.getCardData(currentCardId)
        }

        additionalInfoDialogViewModel.currentCard.observe(this.viewLifecycleOwner){ card ->
            addInfoText.setText(card.additionalContactInfo)
            workInformationText.setText(card.professionalInfo)
            privateInfoText.setText(card.privateInfo)
            referenceText.setText(card.reference)
        }

        saveButton.setOnClickListener {
            val additionalInfo = AdditionalInfo(
                cardId = currentCardId ?: 0L,
                address = addInfoText.text.toString(),
                workInfo = workInformationText.text.toString(),
                privateInfo = privateInfoText.text.toString(),
                reference = referenceText.text.toString()
            )
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to SAVE_STATUS, ADDITIONAL_INFO to additionalInfo)
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS, ADDITIONAL_INFO to AdditionalInfo())
            )
            dismiss()
        }
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val SAVE_STATUS = "additional info save button status"
        val CANCEL_STATUS = "additional info cancel button status"
        val ADDITIONAL_INFO = "additional information"
        val TAG = AdditionalInformationDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = AdditionalInformationDialog()
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
                    val additionalInfo = result.getParcelable<AdditionalInfo>(ADDITIONAL_INFO)
                    if (additionalInfo != null && status != null ) {
                        listener.invoke(cardId, status, additionalInfo)
                    }
                })
        }
    }
}